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
    }
}
