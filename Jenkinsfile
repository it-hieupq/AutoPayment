pipeline {
    agent {
        docker {
            image 'maven:3.9.6-jdk-17'
            args '-v /var/run/docker.sock:/var/run/docker.sock'
        }
    }
    environment {
        IMAGE_NAME = "loan-business"
        IMAGE_TAG = "v1.${BUILD_NUMBER}"
        REGISTRY = "localhost:5000"  // dùng local registry
        DEPLOY_REPO = "https://github.com/it-hieupq/AutoPayment.git"
    }
    stages {
		stages {
			stage('Test Docker Access') {
				steps {
					// Lệnh này phải chạy thành công bên trong Agent
					sh 'docker --version'
				}
			}
		}
    }
}
