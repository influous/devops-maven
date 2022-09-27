// CODE_CHANGES = getGitChanges()
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

    // tools {
    //     maven 'Maven'
    // }

    agent any
    stages {
        stage('init') {
            steps {
                script {
                    gvScript = load "script.groovy"
                }
            }
        }
        stage('build') {
            // when {
            //     expression {
            //         // env.BRANCH_NAME == 'dev' && CODE_CHANGES == true
            //     }
            // }
            steps {
                script {
                    gvScript.buildApp()
                }
            }
        }
        stage('test') {
            when {
                expression {
                    env.BRANCH_NAME == 'jenkins-jobs' || env.BRANCH_NAME == 'main'
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
                }
                withCredentials([
                    usernamePassword(credentialsId: 'my-creds', usernameVariable: 'USER', passwordVariable: 'PASSWORD')
                ]) {
                    echo 'OK'
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
