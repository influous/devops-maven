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
        booleanParam(name: 'executeTests', defaultValue: true, description: '')
    }

    tools {
        maven 'maven-3.8.6'
    }
    
    environment {
        APP_NAME = 'devops-maven'
        DOCKER_REPO_SERVER = '909155662125.dkr.ecr.eu-central-1.amazonaws.com'
        DOCKER_REPO = "${DOCKER_REPO_SERVER}/devops-maven"
        EC2_USER = 'ubuntu'
        EC2_ADDRESS = '3.75.225.159'
        IMAGE_BASE = 'influous/devops-maven'
        IMAGE_TAG = '1.1'
        BUILD_TAG = "${IMAGE_TAG}-${BUILD_NUMBER}"
        BUILD_LATEST = 'latest'
        IMAGE_BUILD = "${IMAGE_BASE}:${IMAGE_TAG}-${BUILD_NUMBER}"
        IMAGE_LATEST = "${IMAGE_BASE}:${BUILD_LATEST}"
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
            steps {
                script {
                    buildJar()
                }
            }
        }
        stage('Build & Push Docker Image') {
            steps {
                script {
                    buildImage()
                    dockerLogin()
                    dockerPush()
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
        stage('EC2 Deploy') {
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
