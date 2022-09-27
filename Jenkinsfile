// CODE_CHANGES = getGitChanges()

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
        stage("build") {
            // when {
            //     expression {
            //         // env.BRANCH_NAME == 'dev' && CODE_CHANGES == true
            //     }
            // }
            steps {
                echo "Building application version ${NEWEST_VERSION}..."
                // sh 'mvn install'
            }
        }
        stage("test") {
            when {
                expression {
                    env.BRANCH_NAME == 'jenkins-jobs' || env.BRANCH_NAME == 'main'
                    params.executeTests // if true, this stage is executed
                }
            }
            steps {
                echo 'Testing application...'
            }
        }
        stage("deploy") {
            steps {
                echo "Deploying application version ${params.VERSION}"
                echo "Deploying with ${SERVER_CREDENTIALS}"
                // sh script "${SERVER_CREDENTIALS}"

                withCredentials([
                    usernamePassword(credentials: 'my-creds', usernameVariable: USER, passwordVariable: PASSWORD)
                ]) {
                    echo "${USER}:${PASSWORD}"
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