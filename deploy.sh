#!/bin/bash

### === 설정 === ###
DOCKER_USERNAME="xioz19"
DOCKER_IMAGE="weathercodi"
EC2_USER="ubuntu"
EC2_HOST="13.209.81.109"
SSH_KEY="~/.ssh/woowa.pem"
REMOTE_APP_PATH="/home/ubuntu/weathercodi"

echo "========================================"
echo " 🚀 WeatherCodi Deploy Script Started"
echo "========================================"

### === 1) Gradle Build === ###
echo "🔨 Step 1: Building JAR..."
./gradlew clean build -x test

if [ $? -ne 0 ]; then
  echo "❌ Gradle Build Failed!"
  exit 1
fi

### === 2) Docker Build (AMD64) === ###
echo "🐳 Step 2: Building Docker Image..."
docker build --platform=linux/amd64 -t ${DOCKER_IMAGE} .

if [ $? -ne 0 ]; then
  echo "❌ Docker Build Failed!"
  exit 1
fi

### === 3) Tag Image === ###
echo "🏷️ Step 3: Tagging Docker Image..."
docker tag ${DOCKER_IMAGE} ${DOCKER_USERNAME}/${DOCKER_IMAGE}:latest

### === 4) Docker Push === ###
echo "📤 Step 4: Pushing Docker Image..."
docker push ${DOCKER_USERNAME}/${DOCKER_IMAGE}:latest

if [ $? -ne 0 ]; then
  echo "❌ Docker Push Failed!"
  exit 1
fi

### === 5) EC2 Deploy === ###
echo "📂 Step 5: Uploading docker-compose.yml to EC2..."
scp -i ${SSH_KEY} docker-compose.yml ${EC2_USER}@${EC2_HOST}:${REMOTE_APP_PATH}/docker-compose.yml

### === 6) EC2 서버에 접속하여 Compose 재배포 === ###
echo "🔗 Step 6: Connect to EC2 and Deploy with Compose..."

ssh -i ${SSH_KEY} ${EC2_USER}@${EC2_HOST} << EOF
  cd ${REMOTE_APP_PATH}

  echo "⬇️ Pulling latest app image..."
  docker-compose pull

  echo "🧹 Stopping old containers..."
  docker-compose down

  echo "🚀 Starting new containers..."
  docker-compose up -d

  echo "🎉 Deployment Done!"
EOF


echo "========================================"
echo " ✅ Deployment Completed Successfully!"
echo " 🌐 Access your API at: http://${EC2_HOST}:8080"
echo "========================================"

