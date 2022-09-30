#!/usr/bin/env groovy

library identifier: 'jenkins-shared-library@main', retriever: modernSCM(
    [$class: 'GitSCMSource',
    remote: 'https://github.com/influous/jenkins-shared-library',
    credentialsId: 'github'
    ]
)

pipeline {
    parameters {
        // choice(name: 'VERSION', choices: ['1.0.0', '1.1.0', '1.2.0'], description: '')
        booleanParam(name: 'executeTests', defaultValue: true, description: '')
    }

    tools {
        maven 'maven-3.8.6'
    }
    
    environment {
        EC2_USER = 'ubuntu'
        EC2_ADDRESS = '3.75.211.238'
        IMAGE_NAME = "influous/react-nodejs-example:1.0-${BUILD_NUMBER}"
    }

    agent any

    stages {
        stage('init') {
            steps {
                script {
                    echo "Image name: ${env.IMAGE_NAME}"
                    echo "Working on branch: ${env.BRANCH_NAME}"
                }
            }
        }

        stage('increment version') {
            steps {
                script {
                    incrementVersion()
                }
            }
        }

        stage('build jar') {
            when {
                expression {
                    env.BRANCH_NAME == 'jenkins-jobs'
                }
            }
            steps {
                script {
                    echo "Building the JAR application v${params.VERSION}..."
                    buildJar()
                }
            }
        }
        stage('build and push image') {
            steps {
                script {
                    buildImage(env.IMAGE_NAME)
                    dockerLogin()
                    dockerPush(env.IMAGE_NAME)
                }
            }
        }
        stage('test') {
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
        stage('deploy') {
            steps {
                script {
                    deployApp()
                }
            }
        }
        stage('commit version update') {
            steps {
                script {
                    updateGit()
                }
            }
        }
    }

}
