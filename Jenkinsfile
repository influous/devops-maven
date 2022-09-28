#!/usr/bin/env groovy

@Library('jenkins-shared-library') _

def gvScript

pipeline {
    parameters {
        choice(name: 'VERSION', choices: ['1.1.0', '1.2.0', '1.3.0'], description: '')
        booleanParam(name: 'executeTests', defaultValue: true, description: '')
    }

    environment {
        // Usually: Extract the version from the code
        NEWEST_VERSION = '1.3.0'
        SERVER_CREDENTIALS = credentials('my-creds')
    }

    tools {
        maven 'maven-3.8.6'
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
        stage('build jar') {
            when {
                expression {
                    env.BRANCH_NAME == 'main'
                }
            }
            steps {
                script {
                    buildJar()
                }
            }
        }
        stage('build image') {
            steps {
                script {
                    buildImage('influous/infx-repo:dm1.2')
                }
            }
        }
        stage('test') {
            when {
                expression {
                    env.BRANCH_NAME == 'main'
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
            when {
                expression {
                    env.BRANCH_NAME == 'main'
                }
            }
            steps {
                script {
                    env.ENV = input message: 'Select the environment to deploy to:', ok: 'Done', parameters: [choice(name: 'ENV', choices: ['dev', 'staging', 'prod'], description: '')]
                    gvScript.deployApp()
                    echo "Deploying to ${ENV}"
                }
                withCredentials([
                    usernamePassword(credentialsId: 'my-creds', usernameVariable: 'USER', passwordVariable: 'PASSWORD')
                ]) {
                    echo 'Done'
                }
            }
        }
    }

    // post {
    //     always {
    //         //Sending a notifcation (mail)
    //     }
    //     success {
    //     }
    //     failure {
    //     }
    // }
}
