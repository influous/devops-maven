def incrementVersion() {
    echo 'Incrementing app version'
    sh "mvn build-helper:parse-version versions:set \
    -DnewVersion=\\\${parsedVersion.majorVersion}.\\\${parsedVersion.minorVersion}.\\\${parsedVersion.nextIncrementalVersion} \
    versions:commit"
    def matchedFile = readFile('pom.xml') =~ '<version>(.+)</version>'
    def version = matchedFile[0][1] // text inside <version>
    env.IMAGE_NAME = "$version-$BUILD_NUMBER"
}

def buildJar() {
    echo "Building the application (JAR) v${params.VERSION}..."
    sh 'mvn clean package'
}

def buildImage() {
    echo 'Building Docker image'
    withCredentials([usernamePassword(credentialsId: 'docker-hub-repo', usernameVariable: 'USER', passwordVariable: 'PASSWORD')]) {
        sh "docker build -t ${IMAGE_NAME} ."
        sh "echo '${PASSWORD}' | docker login -u $USER --password-stdin"
        sh "docker push ${IMAGE_NAME}"
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