#!/bin/bash
set -e

APP_DIR="/home/ec2-user/TuneTailor-Deployment-220"
REPO_URL="https://github.com/cs220s26/TuneTailor-Deployment-220.git"
SERVICE_NAME="tunetailorbot"

cd /home/ec2-user

if [ ! -d "$APP_DIR" ]; then
  git clone "$REPO_URL"
fi

cd "$APP_DIR"

git pull origin main
mvn clean package -DskipTests

sudo systemctl daemon-reload
sudo systemctl restart "$SERVICE_NAME"
sudo systemctl status "$SERVICE_NAME" --no-pager

echo "ReDeployment complete."