def buildApp() {
    echo 'Building the application...'
    echo "Building application version ${NEWEST_VERSION}..."
    // sh 'mvn install'
}

def testApp() {
    echo 'Testing application...'
}

def deployApp() {
    echo "Deploying application version ${params.VERSION}"
    echo "Deploying with ${SERVER_CREDENTIALS}"
    // sh script "${SERVER_CREDENTIALS}"
}

return this