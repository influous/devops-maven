#!/usr/bin/env groovy

library identifier: 'jenkins-shared-library@main', retriever: modernSCM(
    [$class: 'GitSCMSource',
    remote: 'https://github.com/influous/jenkins-shared-library',
    credentialsId: 'github'
    ]
)

pipeline {
    parameters {
        string(name: 'VERSION', defaultValue: "${env.VERSION}", description: '')
        // choice(name: 'VERSION', choices: ['1.0.0', '1.1.0', '1.2.0'], description: '')
        booleanParam(name: 'executeTests', defaultValue: true, description: '')
    }

    tools {
        maven 'maven-3.8.6'
    }
    
    environment {
        EC2_USER = 'ubuntu'
        EC2_ADDRESS = '3.75.225.159'
        IMAGE_BASE = 'influous/devops-maven'
        IMAGE_TAG = '1.0'
        IMAGE_BUILD = "${IMAGE_BASE}:${IMAGE_TAG}-${BUILD_NUMBER}"
        IMAGE_LATEST = "${IMAGE_BASE}:latest"
    }

    agent any

    stages {
        stage('Init') {
            steps {
                script {
                    echo "Image name: ${env.IMAGE_BASE}"
                    echo "Build: ${env.IMAGE_BUILD}"
                    echo "Working on branch: ${env.BRANCH_NAME}"
                }
            }
        }

        stage('Increment Version') {
            steps {
                script {
                    incrementVersion()
                }
            }
        }

        stage('Build Jar') {
            when {
                expression {
                    env.BRANCH_NAME == 'jenkins-jobs'
                }
            }
            steps {
                script {
                    // echo "Building the application v${params.VERSION}..."
                    echo "Building the application v${env.IMAGE_TAG}..."
                    buildJar()
                }
            }
        }
        stage('Build & Push Docker Image') {
            steps {
                script {
                    buildImage(env.IMAGE_BUILD, env.IMAGE_LATEST)
                    dockerLogin()
                    dockerPush(env.IMAGE_BUILD, env.IMAGE_LATEST)
                }
            }
        }
        stage('Test') {
            when {
                expression {
                    env.BRANCH_NAME == 'jenkins-jobs'
                    params.executeTests // if true, this stage is executed
                }
            }
            steps {
                script {
                    testApp()
                }
            }
        }
        stage('Deploy') {
            steps {
                script {
                    deployApp()
                }
            }
        }
        stage('Commit Version Update') {
            steps {
                script {
                    updateGit()
                }
            }
        }
    }

}
