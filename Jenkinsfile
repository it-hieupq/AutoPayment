pipeline {
	agent none
	options {
		// Chỉ giữ lại 10 bản build gần nhất
		buildDiscarder(logRotator(numToKeepStr: '10'))
		// Bạn cũng có thể kết hợp với giới hạn thời gian (ví dụ: giữ tối đa 30 ngày VÀ 10 bản gần nhất)
		// buildDiscarder(logRotator(daysToKeepStr: '30', numToKeepStr: '10'))
	}
	environment {
		IMAGE_NAME = "loan-business"
		IMAGE_TAG = "${BUILD_NUMBER}"
		REGISTRY = "host.docker.internal:5000"  // dùng local registry
		DEPLOY_REPO = "https://github.com/it-hieupq/loan-business-deployment.git"
		PAT_CREDENTIALS_ID = "it-hieupq"
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
				// Tải Credentials và Ánh xạ thành biến
				withCredentials([
					// Lấy credentials có ID là PAT_CREDENTIALS_ID
					usernamePassword(
						credentialsId: env.PAT_CREDENTIALS_ID,
						// Ánh xạ Username thành biến tên là GIT_USER
						usernameVariable: 'GIT_USER',
						// Ánh xạ Password/Token thành biến tên là GIT_PAT
						passwordVariable: 'GIT_PAT'
					)
				]) {
					sh """
                    # 1. THÊM LỆNH XÓA (Nếu thư mục tồn tại)
                    rm -rf deploy

                    # 2. Clone Repository Manifests vào thư mục 'deploy' (VẪN CẦN TOKEN ĐỂ CLONE REPO PRIVATE)
                    git clone https://${GIT_USER}:${GIT_PAT}@github.com/it-hieupq/loan-business-deployment.git deploy

                    cd deploy

                    # 3. Sửa Manifest
                    sed -i "s#image: .*#image: ${REGISTRY}/${IMAGE_NAME}:${IMAGE_TAG}#" loan-business-deploy.yaml

                    # 4. Commit (Giữ nguyên)
                    git config user.name "jenkins"
                    git config user.email "jenkins@local"
                    git commit -am "update image to ${IMAGE_TAG}"

                    # 5. SỬA LỖI GIT PUSH: Truyền Token vào URL Push
                    git push https://${GIT_USER}:${GIT_PAT}@github.com/it-hieupq/loan-business-deployment.git master
                    """

				}
			}
		}
	}
}
