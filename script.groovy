def buildJar() {
    echo "Building the application (JAR) v${params.VERSION}..."
    sh 'mvn package'
}

def buildImage() {
    echo 'Building Docker image'
    withCredentials([usernamePassword(credentialsId: 'docker-hub-repo', usernameVariable: 'USER', passwordVariable: 'PASSWORD')]) {
        sh 'docker build -t influous/infx-repo:dm1.1 .'
        sh "echo '${PASSWORD}' | docker login -u $USER --password-stdin"
        sh 'docker push influous/infx-repo:dm1.1'
    }
}

def testApp() {
    echo 'Testing application...'
    echo "Executing pipeline for $env.BRANCH_NAME"
}

def deployApp() {
    echo "Deploying application version ${params.VERSION}"
    // sh script "${SERVER_CREDENTIALS}"
}

return this