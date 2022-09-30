#!/usr/bin/env groovy

library identifier: 'jenkins-shared-library@main', retriever: modernSCM(
    [$class: 'GitSCMSource',
    remote: 'https://github.com/influous/jenkins-shared-library',
    credentialsId: 'github'
    ]
)

def gvScript

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
        IMAGE_NAME = 'influous/react-nodejs-example:1.0'
    }

    agent any

    stages {
        stage('init') {
            steps {
                script {
                    echo "${env.BRANCH_NAME}"
                    gvScript = load "script.groovy"
                }
            }
        }

        stage('increment version') {
            steps {
                script {
                    gvScript.incrementVersion()
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
                    buildJar()
                    // gvScript.buildJar()
                }
            }
        }
        stage('build and push image') {
            steps {
                script {
                    gvScript.buildImage()
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
                    gvScript.testApp()
                }
            }
        }
        stage('deploy') {
            steps {
                script {
                    gvScript.deployApp()
                    echo "Deploying to branch ${env.BRANCH_NAME}"
                    def dockerCmd = "docker run -d -p 3000:80 ${IMAGE_NAME}"
                    sshagent(['ec2-ssh-key']) {
                        // -o flag avoids SSH popup
                        sh "ssh -o StrictHostKeyChecking=no ${EC2_USER}@${EC2_ADDRESS} ${dockerCmd}"
                    }
                }
                withCredentials([
                    usernamePassword(credentialsId: 'my-creds', usernameVariable: 'USER', passwordVariable: 'PASSWORD')
                ]) {
                    echo 'Done'
                }
            }
        }
        stage('commit version update') {
            steps {
                script {
                    withCredentials([usernamePassword(credentialsId: 'github', passwordVariable: 'PASSWORD', usernameVariable: 'USER')]) {
                    sh 'git config --global user.email "jenkins@kook.work"'
                    sh 'git config --global user.name "jenkins"'
                    // sh 'git status'
                    // sh 'git branch'
                    // sh 'git config --list'
                    sh "git remote set-url origin https://${PASSWORD}@github.com/${USER}/devops-maven.git"
                    sh 'git add .'
                    sh 'git commit -m "CI: Version bump"'
                    sh 'git push origin HEAD:jenkins-jobs'
                    }
                }
            }
        }
    }

}
