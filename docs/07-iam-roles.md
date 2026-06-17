# IAM Roles

## Problem

AWS access key and secret key were stored in .env.

## Risks

- Credential leakage
- Secret exposure

## Solution

Created:

- ResearchPaperS3Policy
- ResearchPaperEC2Role

Attached role to EC2 instance.

AWS SDK now retrieves temporary credentials from IMDS.

## Benefits

- No hardcoded credentials
- Production grade security
- Automatic credential rotation
