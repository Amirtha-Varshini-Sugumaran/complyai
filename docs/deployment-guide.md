# Deployment Guide

Recommended AWS direction:

- Backend on ECS or EC2 running the Spring Boot container
- PostgreSQL on Amazon RDS
- Frontend served via Nginx container or static hosting through S3/CloudFront
- Future file storage on S3 for attachment metadata expansion

Operational notes:

- Store JWT secrets in AWS Secrets Manager or Parameter Store
- Restrict network access so the backend reaches RDS over private networking
- Use CI to build and validate images before promotion
