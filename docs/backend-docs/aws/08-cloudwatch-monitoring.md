# CloudWatch Monitoring Setup Documentation

## Overview

CloudWatch AWS ki monitoring aur observability service hai.

Is project mein CloudWatch ko configure kiya gaya taake:

* EC2 health monitor ki ja sake
* CPU utilization dekhi ja sake
* Memory usage monitor ki ja sake
* Disk utilization monitor ki ja sake
* Docker container logs collect kiye ja saken
* Spring Boot application logs monitor kiye ja saken
* Future mein alarms aur notifications create kiye ja saken

---

# Why CloudWatch?

Production environment mein sirf application deploy kar dena enough nahi hota.

Humein continuously monitor karna hota hai:

* Server healthy hai ya nahi
* Memory leak to nahi ho rahi
* Disk full to nahi ho rahi
* CPU overload to nahi ho raha
* Application crash to nahi hui
* Errors aur exceptions aa to nahi rahe

CloudWatch centralized monitoring provide karta hai.

---

# Architecture

User
↓
Spring Boot Application
↓
Docker Container
↓
EC2 Instance
↓
CloudWatch Agent
↓
CloudWatch Metrics + Logs

---

# IAM Configuration

## Created IAM Role

ResearchPaperEC2Role

Purpose:

EC2 ko AWS services access karne ki permission dena without hardcoded credentials.

---

## Attached Policies

### ResearchPaperS3Policy

Permissions:

* s3
* s3
* s3
* s3

Purpose:

Application ko S3 bucket access dena.

---

### CloudWatchAgentServerPolicy

Purpose:

CloudWatch Agent ko metrics aur logs AWS CloudWatch service ko bhejne ki permission dena.

---

# Verification of IAM Role

EC2 ke andar command run ki:

```bash
curl http://169.254.169.254/latest/meta-data/iam/security-credentials/
```

Output:

```text
ResearchPaperEC2Role
```

Ye verify karta hai ke EC2 instance successfully IAM Role use kar raha hai.

---

# CloudWatch Agent Installation

Package download:

```bash
wget https://s3.amazonaws.com/amazoncloudwatch-agent/ubuntu/amd64/latest/amazon-cloudwatch-agent.deb
```

Purpose:

Latest CloudWatch Agent package download karna.

---

Package install:

```bash
sudo dpkg -i amazon-cloudwatch-agent.deb
```

Purpose:

Agent install karna.

---

Agent status check:

```bash
sudo /opt/aws/amazon-cloudwatch-agent/bin/amazon-cloudwatch-agent-ctl -a status
```

Purpose:

Verify karna ke CloudWatch Agent installed hai.

---

# CloudWatch Configuration Wizard

Command:

```bash
sudo /opt/aws/amazon-cloudwatch-agent/bin/amazon-cloudwatch-agent-config-wizard
```

Purpose:

Interactive configuration generate karna.

---

# Selected Configuration

## Run As User

Selected:

```text
cwagent
```

Reason:

Dedicated low-privileged service account.

Security best practice.

---

## Host Metrics

Enabled:

```text
Yes
```

Collected Metrics:

* CPU
* Memory
* Disk
* Network
* Swap

---

## CPU Metrics Per Core

Selected:

```text
No
```

Reason:

Single EC2 instance.

Overall CPU utilization sufficient hai.

---

## Append EC2 Dimensions

Selected:

```text
Yes
```

Included:

* InstanceId
* InstanceType
* ImageId
* AutoScalingGroupName

Benefit:

Metrics specific EC2 instance se link ho jati hain.

---

## Aggregation

Selected:

```text
No
```

Reason:

Single EC2 instance use ho rahi hai.

Aggregation unnecessary hai.

---

## Collection Interval

Selected:

```text
60 seconds
```

Reason:

Monitoring aur cost ke darmiyan balanced choice.

---

## Metrics Configuration

Selected:

```text
Standard
```

Includes:

* CPU Metrics
* Memory Metrics
* Disk Metrics
* DiskIO Metrics
* Network Metrics
* Swap Metrics

---

# Metrics Being Collected

## CPU

Examples:

* cpu_usage_idle
* cpu_usage_user
* cpu_usage_system

Purpose:

CPU load monitor karna.

---

## Memory

Metric:

```text
mem_used_percent
```

Purpose:

RAM utilization monitor karna.

---

## Disk

Metrics:

```text
used_percent
```

Purpose:

Disk fill hone ki monitoring.

---

## DiskIO

Metric:

```text
io_time
```

Purpose:

Disk activity aur bottlenecks identify karna.

---

## Swap

Metric:

```text
swap_used_percent
```

Purpose:

RAM pressure detect karna.

---

# Docker Log Collection

Docker container inspect command:

```bash
docker inspect $(docker ps -q) | grep LogPath
docker inspect springboot-app | grep LogPath
Used SUDO before docker command to give permissions from the EC2 server
```

Purpose:

Container log location identify karna.

---

Returned Log Path:

```text
/var/lib/docker/containers/<container-id>/<container-id>-json.log
```

CloudWatch Agent ko ye file monitor karne ke liye configure kiya gaya.

---

# Log Group

Created:

```text
ResearchPaperBackend
```

Purpose:

Application logs ko centralize karna.

---

# Storage Class

Selected:

```text
STANDARD
```

Reason:

Frequently accessed logs ke liye suitable.

---

# SSM Parameter Store

Configuration save karne ki attempt ki gayi.

Error:

```text
AccessDeniedException
```

Reason:

ResearchPaperEC2Role ke paas:

```text
ssm:PutParameter
```

permission nahi thi.

Observation:

Ye prove karta hai ke EC2 successfully IAM Role ke through AWS APIs call kar rahi thi.

---

# Security Benefits

Before:

AWS_ACCESS_KEY
AWS_SECRET_KEY

stored in .env

Risk:

**Credential leakage**

---

After:

EC2
↓
IAM Role
↓
Temporary Credentials
↓
AWS Services

Benefits:

* No hardcoded secrets
* Automatic credential rotation
* Better security
* Production standard architecture

---

# Future Improvements

## CloudWatch Alarms

Planned:

* CPU > 80%
* Memory > 85%
* Disk > 90%

---

## SNS Notifications

Email alerts on failures.

---

## Spring Boot Structured Logging

JSON logs.

---

## Prometheus + Grafana Integration

Advanced monitoring dashboard.

---

# Final Outcome

Successfully configured:

* IAM Role Based Authentication
* CloudWatch Agent
* EC2 Monitoring
* Memory Monitoring
* Disk Monitoring
* Docker Log Collection

Result:

Production-grade observability foundation established for the Research Paper Management System.




# Commands Cheat Sheet

## Download Agent

wget https://s3.amazonaws.com/amazoncloudwatch-agent/ubuntu/amd64/latest/amazon-cloudwatch-agent.deb

Purpose:
Download latest CloudWatch Agent package.

---

## Install Agent

sudo dpkg -i amazon-cloudwatch-agent.deb

Purpose:
Install CloudWatch Agent.

---

## Run Wizard

sudo /opt/aws/amazon-cloudwatch-agent/bin/amazon-cloudwatch-agent-config-wizard

Purpose:
Generate configuration.

---

## Agent Status

sudo /opt/aws/amazon-cloudwatch-agent/bin/amazon-cloudwatch-agent-ctl -a status

Purpose:
Verify installation.

---

## Check Docker Logs Path

docker inspect $(docker ps -q) | grep LogPath

Purpose:
Find container logs.

---

## Verify IAM Role

curl http://169.254.169.254/latest/meta-data/iam/security-credentials/

Purpose:
Verify attached IAM role.
