#!/bin/bash
set -e

APP_DIR="/home/ec2-user/TuneTailor-Deployment-220"
SERVICE_NAME="tunetailorbot"
cd "$APP_DIR"
git pull origin main
mvn clean package -DskipTests
sudo systemctl daemon-reload
sudo systemctl restart "$SERVICE_NAME"
sudo systemctl status "$SERVICE_NAME" --no-pager

echo "Deployment complete."