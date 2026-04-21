#!/bin/bash
set -e

APP_DIR="/home/ec2-user/TuneTailor-Deployment-220"
REPO_URL="https://github.com/cs220s26/TuneTailor-Deployment-220.git"
SERVICE_NAME="tunetailorbot"

cd /home/ec2-user

if [ ! -d "$APP_DIR" ]; then
  git clone "$REPO_URL" "$APP_DIR"
fi

cd "$APP_DIR"

git pull origin main
mvn clean package -DskipTests

sudo systemctl restart "$SERVICE_NAME"
sudo systemctl status "$SERVICE_NAME" --no-pager

echo "Redeployment complete."