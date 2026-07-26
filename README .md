# CI/CD Pipeline with Automated Docker Security Scanning and Kubernetes Deployment

## 1. Project Overview

This project demonstrates a secure CI/CD pipeline for a Java application
using Jenkins, Docker, Trivy, Docker Hub, and Kubernetes.

The main security requirement is:

> A Docker image must be scanned for vulnerabilities before it is
> allowed to reach Kubernetes production deployment.

The pipeline automatically:

1.  Pulls source code from GitHub.
2.  Builds the Java application into a Docker image.
3.  Scans the Docker image using Trivy.
4.  Fails the pipeline when HIGH or CRITICAL vulnerabilities are
    detected.
5.  Pushes only approved images to Docker Hub.
6.  Deploys the approved image to Kubernetes.
7.  Verifies that the Kubernetes rollout completes successfully.

This demonstrates a security-gated CI/CD workflow where vulnerability
scanning is part of the deployment process rather than a manual
activity.

------------------------------------------------------------------------

## 2. Project Objective

The objective is to prevent vulnerable container images from being
deployed to Kubernetes.

The security team identified vulnerabilities in production containers.
Therefore, the CI/CD process was designed so that:

-   Every application image is scanned before deployment.
-   HIGH and CRITICAL vulnerabilities are treated as deployment
    blockers.
-   A failed security scan stops later stages.
-   Kubernetes deployment occurs only after the security stage succeeds.

------------------------------------------------------------------------


## 3. Technologies Used

  -----------------------------------------------------------------------
  Technology                          Purpose
  ----------------------------------- -----------------------------------
  Java 21                             Application development

  Docker                              Application containerization

  Jenkins                             CI/CD automation

  Trivy                               Container vulnerability scanning

  GitHub                              Source code management

  Docker Hub                          Container image registry

  Kubernetes                          Container orchestration

  Minikube                            Local Kubernetes cluster

  Kubernetes Deployment               Application deployment and replica
                                      management

  Kubernetes Service                  Network access to the application
  -----------------------------------------------------------------------


## 4. Architecture


                    Developer
                       |
                       | git push
                       v
                    GitHub
                       |
                       | Webhook
                       v
                    Jenkins
                       |
                       v
                +---------------+
                | Checkout Code |
                +---------------+
                       |
                       v
                +---------------+
                | Docker Build  |
                +---------------+
                       |
                       v
                +---------------+
                | Trivy Scan    |
                | HIGH/CRITICAL |
                +---------------+
                    /       \
                 FAIL        PASS
                  |           |
                  v           v
              STOP PIPELINE  Docker Hub
                              |
                              v
                         Kubernetes
                              |
                              v
                         Deployment
                              |
                              v
                           Service
                              |
                              v
                         Application


------------------------------------------------------------------------
## 5. Project Structure

cicd-security-project/
│
├── src/
│   └── Main.java
│
├── k8s/
│   ├── deployment.yaml
│   └── service.yaml
│
├── Dockerfile
├── Jenkinsfile
├── README.md
└── trivy-scan-report.txt

------------------------------------------------------------------------
## 6. Security Gating Logic

The security gate is implemented in the Jenkins pipeline using Trivy.

Example:

bash
trivy image --severity HIGH,CRITICAL --exit-code 1 cicd-security-app:1.1


### Meaning of the options

-   trivy image scans a Docker image.
-   --severity HIGH,CRITICAL` checks HIGH and CRITICAL severity
    vulnerabilities.
-   --exit-code 1` makes Trivy return a non-zero exit code when
    matching vulnerabilities are found.

Jenkins treats a non-zero exit code as a failed pipeline step.

Therefore:

``` text
Vulnerabilities >= configured threshold
              |
              v
        Trivy exit code 1
              |
              v
       Jenkins stage fails
              |
              v
      Deployment is skipped


When the scan passes:

 text
No blocking vulnerabilities
              |
              v
        Trivy exit code 0
              |
              v
       Jenkins continues
              |
              v
      Push image + Deploy


This is the core security control of the project.

------------------------------------------------------------------------

## 7. Application

The application is a simple Java HTTP server.

File:

text
src/Main.java


The application listens on port `8080`.

Example response:

 text
CI/CD Security Project is running!


The application is intentionally simple because the main purpose of this
project is to demonstrate CI/CD security automation.

------------------------------------------------------------------------

## 8. Dockerfile

The application is packaged into a Docker image using the Eclipse
Temurin Java 21 image.

 dockerfile
FROM eclipse-temurin:21-jdk-jammy

WORKDIR /app

COPY src/Main.java .

RUN javac Main.java

CMD ["java", "Main"]


### Docker build

The Jenkins pipeline builds:

 bash
docker build -t cicd-security-app:1.1 .


The resulting image is:

 text
cicd-security-app:1.1


------------------------------------------------------------------------

## 9. Jenkins Pipeline

The Jenkins pipeline contains the following major stages:

 text
Checkout
   |
Build Docker Image
   |
Security Scan
   |
Push and Deploy


### Stage 1: Checkout

Jenkins obtains the source code from GitHub.

The repository contains:

-   Java source code
-   Dockerfile
-   Jenkinsfile
-   Kubernetes YAML files

------------------------------------------------------------------------

### Stage 2: Build Docker Image

Jenkins creates the Docker image.

Example:

 bash
docker build -t cicd-security-app:1.1 .


If the Docker build fails, the pipeline stops and the security scan is
not executed.

------------------------------------------------------------------------

### Stage 3: Security Scan

Trivy scans the locally built image.

Example:

 bash
trivy image --severity HIGH,CRITICAL --exit-code 1 cicd-security-app:1.1


This is the security gate.

If blocking vulnerabilities are found:

 text
Security Scan FAILED
       |
       v
Push and Deploy SKIPPED


If the scan passes:

text
Security Scan PASSED
       |
       v
Push and Deploy CONTINUES


------------------------------------------------------------------------

### Stage 4: Push and Deploy

After the image passes the security gate, Jenkins pushes the image to
Docker Hub and updates Kubernetes.

Example image:

 text
kadambari0809/cicd-security-app:1.1


Kubernetes deployment is then updated:

 bash
kubectl set image deployment/cicd-security-app \
cicd-security-app=kadambari0809/cicd-security-app:1.1

The pipeline verifies the rollout:

bash
kubectl rollout status deployment/cicd-security-app

A successful deployment produces:

text
deployment "cicd-security-app" successfully rolled out


------------------------------------------------------------------------

## 10. Kubernetes Deployment

The application runs inside a Kubernetes Deployment.

Example:

 yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: cicd-security-app
spec:
  replicas: 1
  selector:
    matchLabels:
      app: cicd-security-app
  template:
    metadata:
      labels:
        app: cicd-security-app
    spec:
      containers:
        - name: cicd-security-app
          image: kadambari0809/cicd-security-app:1.1
          ports:
            - containerPort: 8080


The Deployment manages the application Pod and ensures the desired
number of replicas are running.

------------------------------------------------------------------------

## 11. Kubernetes Service

A Service provides network access to the application.

Example:

 yaml
apiVersion: v1
kind: Service
metadata:
  name: cicd-security-service
spec:
  type: NodePort
  selector:
    app: cicd-security-app
  ports:
    - port: 8080
      targetPort: 8080


The Service selects Pods using:

 yaml
selector:
  app: cicd-security-app


The application container listens on:
 text
8080
The Service forwards traffic to the application's container port.

------------------------------------------------------------------------

## 12. Deployment Verification

After Jenkins deploys the image, Kubernetes can be verified using:

 bash
kubectl get pods


Expected example:

 text
NAME                                  READY   STATUS    RESTARTS   AGE
cicd-security-app-xxxxxxxxxx-xxxxx   1/1     Running   0          ...


Check the Deployment:

 bash
kubectl get deployment


Check the Service:

 bash
kubectl get svc


Check detailed Pod information:

 bash
kubectl describe pod <pod-name>


Check Deployment rollout:

 bash
kubectl rollout status deployment/cicd-security-app


------------------------------------------------------------------------

## 13. GitHub Webhook

A GitHub webhook is used to automatically trigger Jenkins after code
changes are pushed.

Workflow:

text
Developer
    |
    | git push
    v
GitHub
    |
    | webhook
    v
Jenkins
    |
    v
Pipeline starts automatically


This removes the need to manually start every pipeline execution.

------------------------------------------------------------------------

## 14. Vulnerability Failure Test

One of the project requirements is to intentionally introduce a
vulnerable package and verify that the security gate blocks deployment.

For the test, a deliberately outdated base image or package can be used
in a temporary test image.

Example test Dockerfile:

 dockerfile
FROM ubuntu:20.04

RUN apt-get update && apt-get install -y openssl

CMD ["sleep", "3600"]


The test image is then scanned by Trivy.

If HIGH or CRITICAL vulnerabilities meet the configured threshold, Trivy
returns a non-zero exit code.

The Jenkins result should be:

 text
Build Docker Image       SUCCESS
Security Scan            FAILURE
Push and Deploy          SKIPPED
Pipeline                 FAILURE


This proves that the security gate is working.

After the demonstration, the secure application Dockerfile should be
restored.

------------------------------------------------------------------------

## 15. Secure Pipeline Test

The normal application image is scanned before deployment.

Successful workflow:

text
Docker Build
     |
     v
Trivy Scan
     |
     | PASS
     v
Push Image
     |
     v
Kubernetes Deployment
     |
     v
Rollout Successful


The Jenkins pipeline should finish with:

``` text
Finished: SUCCESS
```

------------------------------------------------------------------------

## 16. Important Security Principle

The most important rule implemented by this project is:

> Build success does not automatically mean deployment is allowed.

The image must first pass the security gate.

``` text
Build
  |
  v
Security Scan
  |
  +---- FAIL ----> Stop
  |
  +---- PASS ----> Deploy
```

This follows a shift-left security approach by detecting container
vulnerabilities during CI/CD before they reach the Kubernetes
environment.

------------------------------------------------------------------------

## 17. Project Structure

cicd-security-project/
│
├── src/
│   └── Main.java
│
├── k8s/
│   ├── deployment.yaml
│   └── service.yaml
│
├── Dockerfile
├── Jenkinsfile
├── README.md
└── trivy-scan-report.txt


------------------------------------------------------------------------


## 18. End-to-End Workflow

The complete project workflow is:

``` text
1. Developer writes Java application
                |
                v
2. Push code to GitHub
                |
                v
3. GitHub webhook triggers Jenkins
                |
                v
4. Jenkins checks out source code
                |
                v
5. Jenkins builds Docker image
                |
                v
6. Trivy scans Docker image
                |
         +------+------+
         |             |
       FAIL           PASS
         |             |
         v             v
   Stop pipeline   Push image
                       |
                       v
                Deploy Kubernetes
                       |
                       v
                 Rollout check
                       |
                       v
                  Application
                    Running
```

------------------------------------------------------------------------

## 19. Project Outcome

This project demonstrates how security can be integrated directly into a
CI/CD pipeline.

The pipeline ensures that:

-   Application code is automatically built.
-   Docker images are automatically created.
-   Container vulnerabilities are automatically scanned.
-   HIGH and CRITICAL vulnerabilities can block the pipeline.
-   Only images that pass the security gate proceed toward deployment.
-   Kubernetes deployment is automated.
-   Deployment success is verified using Kubernetes rollout status.
-   GitHub webhooks enable automated pipeline execution.

The project therefore implements a practical **DevSecOps-style CI/CD
workflow** for containerized applications.

------------------------------------------------------------------------

## 20. Conclusion

The project successfully demonstrates an automated and security-aware
deployment workflow:

``` text
GitHub
  ↓
Jenkins
  ↓
Docker Build
  ↓
Trivy Security Gate
  ↓
Docker Hub
  ↓
Kubernetes
  ↓
Application
```

The key security feature is the Trivy gate between image creation and
deployment. If the image contains vulnerabilities that meet the
configured HIGH/CRITICAL threshold, the pipeline fails and deployment
stages are not executed.

This ensures that container security is checked before the application
reaches Kubernetes.
