# Prisma Cloud Demo: SCA Scan & Image Scan

This repository is a demo project to showcase the **Software Composition Analysis (SCA)** and **Container Image Scanning** features of **Prisma Cloud** by Palo Alto Networks. These scans help identify vulnerabilities, misconfigurations, and compliance issues within open-source dependencies (SCA) and Docker container images (Image Scanning) before they reach production.

## Table of Contents

- [Prerequisites](#prerequisites)
- [Setup](#setup)
  - [1. Configure Prisma Cloud API Access](#1-configure-prisma-cloud-api-access)
  - [2. Build Docker Image for Image Scanning](#2-build-docker-image-for-image-scanning)
- [Running SCA Scan](#running-sca-scan)
- [Running Image Scan](#running-image-scan)
- [Automating Scans in CI/CD](#automating-scans-in-cicd)
- [Results](#results)
  - [Viewing Scan Reports](#viewing-scan-reports)
  - [Remediation](#remediation)

## Prerequisites

Before getting started, ensure you have the following:

- A **Prisma Cloud** account with access to the Compute module.
- Access to **Prisma Cloud Compute API** for programmatic scanning.
- **Docker** installed on your local machine for building and scanning container images.
- **Node.js** and **npm** (or similar package manager) installed for running SCA scans.
- Your preferred CI/CD platform configured (optional).

## Setup

### 1. Configure Prisma Cloud API Access

To enable the Prisma Cloud SCA and Image Scanning features, you will need to:

1. **Obtain Prisma Cloud API Credentials**:
   - Login to Prisma Cloud and navigate to **Compute > Manage > System > Utilities**.
   - Download the `twistcli` tool, which is used for image scanning, and generate an **access key** under **Runtime Security > Settings > Access Control > Access Keys**.

2. **Configure Prisma Cloud API** in your environment:
   - Set the following environment variables using the API credentials:
     ```bash
     export PRISMA_CLOUD_API="<Prisma Cloud API URL>"
     export PRISMA_CLOUD_ACCESS_KEY="<Your Access Key>"
     export PRISMA_CLOUD_SECRET_KEY="<Your Secret Key>"
     ```

### 2. Build Docker Image for Image Scanning

For the purpose of this demo, we will use a sample Node.js application, which can be found in this repository.

1. Clone this repository:
   ```bash
   git clone https://github.com/hieupvtsdv/prisma-demo.git
   cd prisma-demo
   ```

2. Build the Docker image:
   ```bash
   docker build -t sample-node-app .
   ```

## Running SCA Scan

Prisma Cloud supports **Software Composition Analysis (SCA)** for identifying vulnerabilities in open-source dependencies used in your application.

1. Run the SCA scan using Prisma Cloud CLI:
   ```bash
   ...
   ```

   This will scan the current directory for dependency files (like `package.json`, `requirements.txt`, `pom.xml`, etc.), and detect vulnerabilities in your application's libraries and frameworks.

2. Once complete, the results will be available in Prisma Cloud's **Application Security** under the **Code** tab.

## Running Image Scan

Prisma Cloud’s **Image Scanning** checks Docker container images for vulnerabilities, malware, and compliance issues.

1. Run the image scan using the `twistcli` tool:
   ```bash
   twistcli images scan --address $PRISMA_CLOUD_API --user $PRISMA_CLOUD_ACCESS_KEY --password $PRISMA_CLOUD_SECRET_KEY --project "Image-Scan-Demo" sample-node-app:latest
   ```

2. The scan will analyze the image layers for vulnerabilities, misconfigurations, and embedded secrets.

3. Results can be viewed under **Monitor > Vulnerabilities > Images** in Prisma Cloud Console.

## Automating Scans in CI/CD

Prisma Cloud can be integrated into CI/CD pipelines to automate the scanning process.

1. **Add SCA Scan**: Incorporate the SCA scan into your build step by adding the following to your CI pipeline:
   ```bash
   ...
   ```

2. **Add Image Scan**: Ensure your CI system scans Docker images before deployment:
   ```bash
   docker build -t sample-node-app .
   twistcli images scan --address $PRISMA_CLOUD_API --user $PRISMA_CLOUD_ACCESS_KEY --password $PRISMA_CLOUD_SECRET_KEY --project "CI-Image-Scan" sample-node-app:latest
   ```

3. If any high or critical vulnerabilities are found, you can set up your CI/CD pipeline to fail the build or alert the DevOps/security team for further action.

## Results

### Viewing Scan Reports

Once scans are completed, the reports can be accessed via the Prisma Cloud Console:

- **Image Scan Results**: Go to **Monitor > Vulnerabilities > Images** to see detailed reports of the scanned Docker images.

Each report includes:
- Vulnerability severity levels (Critical, High, Medium, Low)
- CVE (Common Vulnerabilities and Exposures) IDs
- Affected libraries and versions
- Remediation guidance

### Remediation

Prisma Cloud provides actionable insights on how to resolve the detected vulnerabilities:
- **For SCA**: Update the affected libraries to recommended versions.
- **For Image Scanning**: Apply necessary patches or rebuild the image using updated base images and dependencies.
