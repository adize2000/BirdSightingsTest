#!/bin/bash

# --- Configuration ---
PROJECT_NAME="BirdSightingsAPI"
COMPOSE_FILE="docker-compose.yml"
# ---------------------

echo "Starting fully automated Docker deployment for $PROJECT_NAME..."

# Check if Docker and Docker Compose are installed
if ! command -v docker &> /dev/null
then
    echo "Error: Docker is not installed. Please install Docker first."
    exit 1
fi

if ! command -v docker-compose &> /dev/null
then
    echo "Error: Docker Compose is not installed. Please install Docker Compose first."
    exit 1
fi

echo "✅ Docker and Docker Compose found."

# Stop and remove previous running containers and networks, if any
echo "Stopping and removing any previous containers..."
docker-compose -f $COMPOSE_FILE down --remove-orphans

# Build the Spring Boot application image
echo "Building the Spring Boot application image..."
# The 'build' instruction in docker-compose will handle the Maven build inside the container (if using the multi-stage Dockerfile)
if docker-compose -f $COMPOSE_FILE build
then
    echo "✅ Application image built successfully."
else
    echo "❌ Error during application image build. Exiting."
    exit 1
fi

# Start the services defined in docker-compose.yml
echo "Starting PostgreSQL database and Spring Boot API services..."
if docker-compose -f $COMPOSE_FILE up -d
then
    echo "✅ Services started successfully in detached mode."
else
    echo "❌ Error during service startup. Exiting."
    exit 1
fi

# Display status
echo -e "\n--- Deployment Status ---"
docker-compose -f $COMPOSE_FILE ps

# Give a helpful message
echo -e "\nDeployment complete. Your services are now running."
echo "PostgreSQL is available on host port 5432."
echo "$PROJECT_NAME API is available on http://localhost:8080."
echo "Run 'docker-compose logs -f' to view combined logs."