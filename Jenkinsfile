#!/usr/bin/env groovy

library identifier: 'jenkins-shared-library@main', retriever: modernSCM(
    [$class: 'GitSCMSource',
    remote: 'https://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@github.com/influous/jenkins-shared-library',
    credentialsId: ''
    ]
)

def gvScript

pipeline {
    parameters {
        choice(name: 'VERSION', choices: ['1.1.1', '1.2.0', '1.3.0'], description: '')
        booleanParam(name: 'executeTests', defaultValue: true, description: '')
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
                    env.BRANCH_NAME == 'jenkins-jobs'
                }
            }
            steps {
                script {
                    buildJar()
                }
            }
        }
        stage('build and push image') {
            steps {
                script {
                    buildImage('influous/infx-repo:dm1.1.1')
                    dockerLogin()
                    dockerPush 'influous/infx-repo:dm1.1.1'
                }
            }
        }
        stage('test') {
            when {
                expression {
                    env.BRANCH_NAME == 'jenkins-jobs'
                    params.executeTests = false // if true, this stage is executed
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
                    env.BRANCH_NAME == 'jenkins-shared-lib'
                }
            }
            steps {
                script {
                    gvScript.deployApp()
                    echo "Deploying to ${env.BRANCH_NAME}"
                }
                withCredentials([
                    usernamePassword(credentialsId: 'my-creds', usernameVariable: 'USER', passwordVariable: 'PASSWORD')
                ]) {
                    echo 'Done'
                }
            }
        }
    }

}
