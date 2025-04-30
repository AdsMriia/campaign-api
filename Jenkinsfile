pipeline {
    agent any

    tools {
        maven 'Maven3'
    }

    environment {
        NETWORK_NAME = 'micro-network'
        DOCKER_IMAGE = 'campaign-api:latest'
        CONTAINER_NAME = 'campaign-app'
        DOCKER_LOG = 'Empty'
    }

    stages {
        stage('Build JAR') {
            steps {
                withMaven {
                    sh 'mvn clean package -Dspring.profiles.active=test'
                }
            }
        }

        stage('Build Docker Image') {
            steps {
                sh 'mkdir -p ${WORKSPACE}/tools/ffmpeg'
                sh 'cp /var/jenkins/tools/ffmpeg/ffmpeg ${WORKSPACE}/tools/ffmpeg/ffmpeg'
                sh 'cp /var/jenkins/tools/ffmpeg/ffprobe ${WORKSPACE}/tools/ffmpeg/ffprobe'
                sh 'docker build -t $DOCKER_IMAGE .'
            }
        }

        stage('Run TEST Docker Container') {
            steps {
                script {

                    def containerName = "${CONTAINER_NAME}_test"

                    withCredentials([file(credentialsId: 'campaign-app-env', variable: 'ENV_FILE')]) {

                        def containerId = sh(script: """
                            docker run -d --network ${NETWORK_NAME} \
                            --name ${containerName} \
                            --env-file $ENV_FILE \
                            $DOCKER_IMAGE
                        """, returnStdout: true).trim()

                        while (true) {
                            sleep(5)
                            def containerRunning = sh(script: "docker ps -q -f name=${containerName}", returnStdout: true).trim()

                            if (!containerRunning) {
                                def logs = sh(script: "docker logs ${containerName}" , returnStdout: true).trim()
                                sh "docker rm ${containerName}"
                                error """Тестовий контейнер потерпів краху:

${logs}"""
                            }

                            def logs = sh(script: "docker logs ${containerName}", returnStdout: true).trim()

                            if (logs.contains("Started Main in ") || logs.contains("Started Application in ")) {
                                sh "docker stop ${containerName}"
                                sh "docker rm ${containerName}"
                                sh 'docker image prune -f'
                                break
                            }
                        }
                    }
                }
            }
        }

        stage('Stop and Remove Existing Container') {
            steps {
                script {
                    def containerRunning = sh(script: "docker ps -a -q -f name=${CONTAINER_NAME}", returnStdout: true).trim()

                    if (containerRunning) {
                        echo "Stopping and removing existing container: ${CONTAINER_NAME}"
                        sh "docker stop ${CONTAINER_NAME}"
                        sh "docker rm ${CONTAINER_NAME}"
                    } else {
                        echo "No running container found with the name: ${CONTAINER_NAME}"
                    }
                }
            }
        }

        stage('Run Docker Container with Environment Check') {
            steps {
                withCredentials([file(credentialsId: 'campaign-app-env', variable: 'ENV_FILE')]) {
                    sh """
                    docker run -d --network ${NETWORK_NAME} \
                    --name ${CONTAINER_NAME} \
                    -p 8083:8080 \
                    --env-file $ENV_FILE \
                    -v /opt/data/mriya/static/:/uploads \
                    $DOCKER_IMAGE
                    """
                }
                sh "curl -X POST localhost:1000/notification/${CONTAINER_NAME}?message=deployed+successful"
            }
        }
    }

    post {
        always {
            cleanWs()
        }
        failure {
            script {
                writeFile file: "/opt/data/jenkins/static/${CONTAINER_NAME}.log", text: currentBuild.rawBuild.logFile.text

                sh """sed -ri "s/\\x1b\\[8m.*?\\x1b\\[0m//g" /opt/data/jenkins/static/${CONTAINER_NAME}.log"""
                sh """curl -X POST 'localhost:1000/notification/file/${CONTAINER_NAME}?message=deployment+failed&fileName=${CONTAINER_NAME}.log'"""
            }
        }
    }
}
