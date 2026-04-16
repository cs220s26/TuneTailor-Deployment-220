#!/bin/bash
set -e

# Update system
yum update -y

# Install required packages
yum install -y git
yum install -y maven-amazon-corretto21
yum install -y tree
yum install -y redis6

# Enable and start Redis
systemctl enable redis6
systemctl start redis6

# Move to ec2-user home
cd /home/ec2-user

# Clone repo if missing
if [ ! -d "/home/ec2-user/TuneTailor-Deployment-220" ]; then
    git clone https://github.com/cs220s26/TuneTailor-Deployment-220.git
fi

# Enter repo
cd /home/ec2-user/TuneTailor-Deployment-220

# Build project
mvn clean package -DskipTests

# Copy service file into systemd directory
cp tunetailorbot.service /etc/systemd/system/tunetailorbot.service

# Reload systemd and start the service
systemctl daemon-reload
systemctl enable tunetailorbot.service
systemctl start tunetailorbot.service