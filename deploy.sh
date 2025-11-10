#!/bin/bash

### === 설정 === ###
DOCKER_USERNAME="xioz19"
DOCKER_IMAGE="weathercodi"
EC2_USER="ubuntu"
EC2_HOST="13.209.81.109"
SSH_KEY="woowa.pem"

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
echo "🔗 Step 5: Connecting to EC2 and Restarting Container..."

ssh -i ${SSH_KEY} ${EC2_USER}@${EC2_HOST} << 'EOF'
  echo "⬇️ Pulling latest image..."
  docker pull xioz19/weathercodi:latest

  echo "🧹 Removing old container..."
  docker rm -f weathercodi-server || true

  echo "🚀 Starting new container..."
  docker run -d -p 8080:8080 --name weathercodi-server xioz19/weathercodi:latest
EOF

echo "✅ Deployment Completed Successfully!"
echo "🌐 Access your app at: http://${EC2_HOST}:8080"

