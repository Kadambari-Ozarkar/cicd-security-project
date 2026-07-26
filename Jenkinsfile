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
                sh 'sudo docker build -t cicd-security-app:1.0 .'
            }
        }
    }
}
