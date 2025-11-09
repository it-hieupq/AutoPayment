pipeline {
    agent {
        docker {
            image 'maven:3-openjdk-17'
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
		// Stage này sử dụng Agent Docker để build code
		stage('Build Java') {
			agent {
				docker {
					// Sử dụng Tag chính xác đã hoạt động
					image 'maven:3-openjdk-17'
				}
			}
			steps {
				sh 'mvn clean package -DskipTests'
			}
		}
        stage('Build Docker image') {
			// Chạy trong Docker Agent khác có Docker CLI
			agent {
				docker {
					image 'docker:latest'
					args '-v /var/run/docker.sock:/var/run/docker.sock'
				}
			}
			steps {
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
