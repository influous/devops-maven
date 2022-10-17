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
        DOCKER_CREDENTIALS = credentials('docker-hub-repo')
        EC2_USER = 'ec2-user'
        IMAGE_BASE = 'influous/devops-maven'
        IMAGE_TAG = '1.1'
        BUILD_TAG = "${IMAGE_TAG}-${BUILD_NUMBER}"
        BUILD_LATEST = 'latest'
        IMAGE_BUILD = "${IMAGE_BASE}:${IMAGE_TAG}-${BUILD_NUMBER}"
        IMAGE_LATEST = "${IMAGE_BASE}:${BUILD_LATEST}"
        AWS_ACCESS_KEY_ID = credentials('jenkins_aws_access_key_id')
        AWS_SECRET_ACCESS_KEY = credentials('jenkins_aws_secret_access_key')
        TF_VAR_env_prefix = 'test'
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
        stage('Provision Server') {
            steps {
                script {
                    dir('terraform') {
                        sh "terraform init"
                        sh "terraform apply --auto-approve"
                        EC2_PUBLIC_IP = sh(
                            script: "terraform output aws_ami_ip",
                            returnStdout: true
                        ).trim()
                    }
                }
            }
        }
        stage('EC2 Deploy') {
            steps {
                script {
                    echo 'Waiting for EC2 server to initialize...'
                    sleep(time: 90, unit: "SECONDS")

                    echo "Deploying to EC2 instance on ${env.EC2_PUBLIC_IP}"
                    def shellCmds = "bash ./server_cmds.sh ${env.IMAGE_LATEST} ${DOCKER_CREDENTIALS_USR} ${DOCKER_CREDENTIALS_PSW}"
                    sshagent(['ec2-ssh-key-nu']) {
                        sh "scp -o StrictHostKeyChecking=no server_cmds.sh ${EC2_USER}@${EC2_PUBLIC_IP}:/home/ec2-user"
                        sh "scp -o StrictHostKeyChecking=no docker-compose.yaml ${EC2_USER}@${EC2_PUBLIC_IP}:/home/ec2-user"
                        sh "ssh -o StrictHostKeyChecking=no ${EC2_USER}@${EC2_PUBLIC_IP} ${shellCmds}"
                    }
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
