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
                        unstash 'source'
                        try {
                            // Run Checkov with Podman
                            sh """
                            podman run --rm  \
                                -v \$(pwd):/workspace:Z \
                                docker.io/bridgecrew/checkov:latest \
                                -d /workspace --use-enforcement-rules \
                                -o cli -o junitxml --output-file-path console,results.xml \
                                --bc-api-key ${PRISMA_API_KEY} \
                                --prisma-api-url ${PRISMA_API_URL} \
                                --repo-id ${REPO_ID} \
                                --branch ${BRANCH} \
                            """
                            // Process the junit report
                            junit skipPublishingChecks: true, testResults: 'results.xml'
                        } catch (err) {
                            // Process the junit report even on failure
                            junit skipPublishingChecks: true, testResults: 'results.xml'
                            throw err
                        }
                    }
                }
            }

            stage('Build Image') {
                steps {
                    script {
                        sh "podman build -t ${DOCKER_IMAGE}:${BUILD_NUMBER} ."
                    }
                }
            }

            stage('Scan Image by Prisma') {
                steps {
                    script {
                        prismaCloudScanImage(
                            ca: '',
                            cert: '',
                            dockerAddress: 'unix:///run/user/1000/podman/podman.sock',
                            image: "${DOCKER_IMAGE}:${BUILD_NUMBER}",
                            key: '',
                            logLevel: 'debug', // 'info'
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
