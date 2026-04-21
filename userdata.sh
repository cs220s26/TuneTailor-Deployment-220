#!/bin/bash
set -e

sudo yum update -y
sudo yum install -y git maven-amazon-corretto21 tree redis6

sudo systemctl enable redis6
sudo systemctl start redis6

cd /home/ec2-user

if [ ! -d "/home/ec2-user/TuneTailor-Deployment-220" ]; then
  git clone https://github.com/cs220s26/TuneTailor-Deployment-220.git
fi

cd /home/ec2-user/TuneTailor-Deployment-220
chmod +x redeploy.sh

sudo cp tunetailorbot.service /etc/systemd/system/tunetailorbot.service
sudo systemctl daemon-reload
sudo systemctl enable tunetailorbot

./redeploy.sh