def testApp() {
    echo 'Testing application...'
    echo "Executing pipeline for $env.BRANCH_NAME"
}

def deployApp() {
    echo "Deploying application version ${params.VERSION}"
    // sh script "${SERVER_CREDENTIALS}"
}

return this