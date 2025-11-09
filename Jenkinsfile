pipeline {
	agent none
    environment {
        IMAGE_NAME = "loan-business"
        IMAGE_TAG = "v1.${BUILD_NUMBER}"
        REGISTRY = "localhost:5000"  // dùng local registry
        DEPLOY_REPO = "https://github.com/it-hieupq/AutoPayment.git"
    }
    stages {
		stage('Build and Tag Docker image') {
			agent {
				docker {
					image 'maven:3.9.6-eclipse-temurin-17'
					args '-v /var/run/docker.sock:/var/run/docker.sock'
				}
			}
			steps {
				// 1. Build Java (tạo ra file JAR)
				sh 'mvn clean package -DskipTests'

				// 2. Build Docker Image (sử dụng file JAR vừa tạo)
				sh "docker build -t ${REGISTRY}/${IMAGE_NAME}:${IMAGE_TAG} ."
			}
		}
        stage('Push Docker image') {
			agent {
				docker {
					image 'docker:latest'
					args '-v /var/run/docker.sock:/var/run/docker.sock'
				}
			}
            steps {
                sh "docker push ${REGISTRY}/${IMAGE_NAME}:${IMAGE_TAG}"
            }
        }
        stage('Update manifest for ArgoCD') {
            steps {
                sh """
                git clone ${DEPLOY_REPO} deploy
                cd deploy
                sed -i 's|image: .*|image: ${REGISTRY}/${IMAGE_NAME}:${IMAGE_TAG}|' loan-business-deploy.yaml
                git config user.name "jenkins"
                git config user.email "jenkins@local"
                git commit -am "update image to ${IMAGE_TAG}"
                git push
                """
            }
        }
    }
}
