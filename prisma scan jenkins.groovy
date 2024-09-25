pipeline {
        agent any

        environment {
            // Prisma Cloud env
            PRISMA_API_URL = 'https://api.prismacloud.io'
            PRISMA_API_KEY = '12345-xxxxx'

            // Source code repository
            CODE_REPO = 'https://gitlab.com/hieupvtsdv/prisma-demo'

            // Docker image
            DOCKER_IMAGE = 'prisma-demo'
        }

        stages {
            stage('Checkout') {
                steps {
                    git branch: 'master', url: "${env.CODE_REPO}"
                    stash includes: '**/*', name: 'source'
                }
            }

            stage('SCA by Prisma') {
                steps {
                    script {
                        docker.image('bridgecrew/checkov:latest').inside("--entrypoint=''") {
                            unstash 'source'
                            try {
                                sh 'checkov -d . --use-enforcement-rules -o cli -o junitxml --output-file-path console,results.xml --bc-api-key $PRISMA_API_KEY --repo-id  $CODE_REPO --branch master'
                                junit skipPublishingChecks: true, testResults: 'results.xml'
                            } catch (err) {
                                junit skipPublishingChecks: true, testResults: 'results.xml'
                                throw err
                            }
                        }
                    }
                }
            }

            stage('Build Image') {
                sh "docker build -t ${DOCKER_IMAGE}:${BUILD_NUMBER} ."
            }

            stage('Scan Image by Prisma') {
                prismaCloudScanImage ca: '', cert: '', dockerAddress: 'unix:///var/run/docker.sock', image:
            '$DOCKER_IMAGE:$BUILD_NUMBER', key: '', logLevel: 'info', podmanPath: '', project: '', resultsFile:
            'prisma-cloud-scan-results.json', ignoreImageBuildTime:true
            }

            stage('Publish Scan result') {
                prismaCloudPublish resultsFilePattern: 'prisma-cloud-scan-results.json'
            }
        }

        options {
            preserveStashes()
            timestamps()
        }
}
