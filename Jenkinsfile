pipeline {
	agent none
    environment {
        IMAGE_NAME = "loan-business"
        IMAGE_TAG = "v1.${BUILD_NUMBER}"
        REGISTRY = "host.docker.internal:5000"  // dùng local registry
        DEPLOY_REPO = "https://github.com/it-hieupq/loan-business-deployment.git"
    }
    stages {
		stage('Build Java') {
			agent {
				docker {
					image 'maven:3.9.6-eclipse-temurin-17'
				}
			}
			steps {
				// Đảm bảo file JAR được tạo ra
				sh 'mvn clean package -DskipTests'
			}
		}

		// Stage Build Docker Image: Chạy trên Agent gốc (my-jenkins-with-docker:lts)

		stage('Build Docker image') {
			agent any
			steps {
				// HÀNH ĐỘNG MỚI: Copy file JAR lên thư mục gốc
				sh 'cp target/loan-business-0.0.1-SNAPSHOT.jar ./' // CHÚ Ý: Dùng tên file JAR chính xác!

				// Sửa Dockerfile để COPY file từ thư mục gốc
				sh """
                # Tạm thời sửa Dockerfile
                sed -i 's|COPY target/loan-business.jar loan-business.jar|COPY loan-business-0.0.1-SNAPSHOT.jar loan-business.jar|' Dockerfile

                # Thực hiện build
                docker build -t ${REGISTRY}/${IMAGE_NAME}:${IMAGE_TAG} .

                # Khôi phục Dockerfile (tùy chọn)
                sed -i 's|COPY loan-business-0.0.1-SNAPSHOT.jar loan-business.jar|COPY target/loan-business.jar loan-business.jar|' Dockerfile
                """
			}
		}

		// Stage Push Docker image: Chạy trên Agent gốc
		stage('Push Docker image') {
			agent any
			steps {
				sh "docker push ${REGISTRY}/${IMAGE_NAME}:${IMAGE_TAG}"
			}
		}
        stage('Update manifest for ArgoCD') {
			agent any
            steps {
                sh """
                # 1. THÊM LỆNH XÓA (Nếu thư mục tồn tại)
				rm -rf deploy
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
