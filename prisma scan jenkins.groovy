pipeline {
        agent any

        environment {
            // Prisma Cloud env
            PRISMA_API_URL = 'https://api.prismacloud.io'
            PRISMA_API_KEY = 'ACCESS_KEY::SECRET_KEY'

            // Source code repository demo
            CODE_REPO = 'https://github.com/hieupvtsdv/prisma-demo'
            REPO_ID = 'hieupvtsdv/prisma-demo'
            BRANCH = 'main'

            // Docker image demo
            DOCKER_IMAGE = 'prisma-demo'
        }

        stages {
            stage('Checkout') {
                steps {
                    git branch: "${env.BRANCH}", url: "${env.CODE_REPO}"
                    stash includes: '**/*', name: 'source'
                }
            }

            stage('SCA by Prisma') {
                steps {
                    script {
                        docker.image('bridgecrew/checkov:latest').inside("--entrypoint=''") {
                            unstash 'source'
                            try {
                                sh "checkov -d . --use-enforcement-rules -o cli -o junitxml --output-file-path console,results.xml --bc-api-key ${env.PRISMA_API_KEY} --repo-id  ${env.REPO_ID} --branch ${env.BRANCH}"
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
                steps {
                    script {
                        sh 'docker build -t ${DOCKER_IMAGE}:${BUILD_NUMBER} .'
                    }
                }
            }

            stage('Scan Image by Prisma') {
                steps {
                    script {
                        prismaCloudScanImage(
                            ca: '', 
                            cert: '', 
                            dockerAddress: 'unix:///var/run/docker.sock', // unix:///run/user/1000/podman/podman.sock
                            image: "${DOCKER_IMAGE}:${BUILD_NUMBER}", 
                            key: '', 
                            logLevel: 'info', // 'debug'
                            podmanPath: '', 
                            project: '', 
                            resultsFile: 'prisma-cloud-scan-results.json',
                            ignoreImageBuildTime: true
                        )
                    }
                }
            }

            stage('Publish Scan result') {
                steps {
                    script {
                        prismaCloudPublish resultsFilePattern: 'prisma-cloud-scan-results.json'
                    }
                }
            }
        }

        options {
            preserveStashes()
            timestamps()
        }
}
