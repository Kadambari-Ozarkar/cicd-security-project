pipeline {
    agent any

    stages {

        stage('Checkout') {
            steps {
                git branch: 'main',
                    url: 'https://github.com/Kadambari-Ozarkar/cicd-security-project.git'
            }
        }

        stage('Build Docker Image') {
            steps {
                sh 'docker build -t cicd-security-app:1.0 .'
            }
        }

        stage('Security Scan') {
            steps {
                sh '''
                    TMPDIR=$HOME/trivy-tmp trivy image \
                    --severity HIGH,CRITICAL \
                    --exit-code 1 \
                    cicd-security-app:1.0
                '''
            }
        }

        stage('Push Docker Image') {
            steps {
                withCredentials([usernamePassword(
                    credentialsId: 'dockerhub-creds',
                    usernameVariable: 'DOCKER_USERNAME',
                    passwordVariable: 'DOCKER_PASSWORD'
                )]) {
                    sh '''
                        echo "$DOCKER_PASSWORD" | docker login -u "$DOCKER_USERNAME" --password-stdin
                        docker tag cicd-security-app:1.0 $DOCKER_USERNAME/cicd-security-app:1.0
                        docker push $DOCKER_USERNAME/cicd-security-app:1.0
                    '''
                }
            }
        }
    }
}
