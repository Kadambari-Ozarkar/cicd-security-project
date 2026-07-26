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
                sh 'docker build -t cicd-security-app:1.1 .'
            }
        }

        stage('Security Scan') {
            steps {
                sh '''
                    TMPDIR=$HOME/trivy-tmp trivy image \
                    --severity HIGH,CRITICAL \
                    --exit-code 1 \
                    cicd-security-app:1.1
                '''
            }
        }

        stage('Push and Deploy') {
            steps {
                withCredentials([usernamePassword(
                    credentialsId: 'dockerhub-creds',
                    usernameVariable: 'DOCKER_USERNAME',
                    passwordVariable: 'DOCKER_PASSWORD'
                )]) {
                    sh '''
                        echo "$DOCKER_PASSWORD" | docker login \
                            -u "$DOCKER_USERNAME" \
                            --password-stdin

                        docker tag cicd-security-app:1.1 \
                            $DOCKER_USERNAME/cicd-security-app:1.1

                        docker push \
                            $DOCKER_USERNAME/cicd-security-app:1.1

                        kubectl apply -f k8s/deployment.yaml
                        kubectl apply -f k8s/service.yaml

                        kubectl set image deployment/cicd-security-app \
                            cicd-security-app=$DOCKER_USERNAME/cicd-security-app:1.1

                        kubectl rollout status deployment/cicd-security-app
                    '''
                }
            }
        }
    }
}
