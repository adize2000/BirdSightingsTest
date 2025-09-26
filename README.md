

# 🐦 BirdSightings Monorepo

## Overview

Welcome to the **BirdSightings** project\! This repository serves as a **monorepo**, housing a suite of interconnected applications and services designed for tracking and managing bird sightings. At its core is a **Spring Boot REST API** that's ready to run with a **PostgreSQL database** using a streamlined Docker setup.

The repository includes four key projects:

| Project Name | Technology / Role | Description |
| :--- | :--- | :--- |
| **`BirdSightingsAPI`** | **Spring Boot (Java 11)**, Maven | The central RESTful API for all data persistence and business logic. |
| **`BirdApiClient`** | **Java SE 11** | A client utility library for other Java components to easily interact with the API. |
| **`BirdApiUi`** | **Java/UI** (e.g., Swing, JavaFX) | A desktop or web UI for users to input and view sightings data. |
| **`BirdsPluginFeature`** | **Java Plugin** | A feature module designed for integration into a larger host application. |

-----

## 🛠️ Prerequisites

To get started, ensure you have the following installed on your machine:

  - **Java Development Kit (JDK) 11**
  - **Maven** (for building projects)
  - **Git**
  - **Docker Desktop** (with **WSL Integration** enabled)
  - **Windows Subsystem for Linux (WSL 2)**

-----

## 🚀 Quick Start (Automated Deployment)

The fastest way to launch the core API and its database is with the provided automation script.

1.  **Open your WSL Terminal** (e.g., Ubuntu).
2.  **Navigate to the monorepo root** directory.
3.  **Run the installation script**:
    ```bash
    ./install_script.sh
    ```

### Expected Output

The script will automatically build the `BirdSightingsAPI` Docker image and start both the application and the database. Upon completion, you'll have two services running:

| Service | Container | Host Port | Internal Port | Access URL |
| :--- | :--- | :--- | :--- | :--- |
| **PostgreSQL DB** | `birdsightings_db` | `5432` | `5432` | N/A |
| **BirdSightingsAPI** | `birdsightings_api` | `8080` | `8080` | `http://localhost:8080` |

You can now use a tool like cURL or a browser to test your API endpoints, for example: `http://localhost:8080/sightings`.

-----

## ⚙️ Docker and Service Management

The `docker-compose.yml` file defines our infrastructure and makes it easy to manage.

| Service | Docker Image | Description |
| :--- | :--- | :--- |
| **`db`** | `postgres:13` | The database service. It uses a **volume** to persist data, so your sightings won't be lost. |
| **`app`** | Built from local `Dockerfile` | The Spring Boot application service. It depends on the `db` service and connects to it using the service name as the host. |

### Essential Docker Commands

| Command | Description |
| :--- | :--- |
| `docker compose up -d` | Builds and starts both services in the background. |
| `docker compose down` | Stops and removes the containers and network. |
| `docker compose down --volumes` | Stops and removes containers, network, and **deletes all persistent DB data** in the volume. |
| `docker compose logs -f` | Displays combined real-time logs for all services. |
| `docker ps` | Lists all running containers. |

-----

## 💻 Development with Eclipse

All projects in this repository are designed to be imported into an Eclipse workspace.

1.  Open Eclipse and go to **File \> Import...**.
2.  Select **Git \> Projects from Git (with smart import)**.
3.  Choose **Existing Local Repository**.
4.  Click **Add...** and locate the `.git` folder in this monorepo's root directory (`D:/NewProjects/.git`).
5.  Eclipse will automatically detect all four projects and import them into your workspace, maintaining their connection to the shared repository.

-----

## 🗑️ Full Cleanup

To remove all traces of the Docker deployment and start fresh, run the following commands from the monorepo root:

1.  **Stop and remove containers, networks, and volumes:**
    ```bash
    docker compose down --volumes
    ```
2.  **Remove all project-related images:**
    ```bash
    docker image rm birdsightingsapi-app maven:3-openjdk-11 openjdk:11-jre-slim postgres:13 2>/dev/null
    ```
