#!/bin/bash
source ./docker.properties

unset COMPOSE_PROFILES
export PROFILE=docker
export PREFIX="${IMAGE_PREFIX}"
export ARCH=$(uname -m)

echo "### Selected browser: $BROWSER ($selenoid_image) ###"
export BROWSER

echo "### Stopping existing containers ###"
docker compose down

docker_containers=$(docker ps -a -q)
if [ -n "$docker_containers" ]; then
  echo "### Stopping and removing containers: $docker_containers ###"
  docker stop $docker_containers
  docker rm $docker_containers
fi

echo "### Java version ###"
java --version

echo "### Clean build ###"
bash ./gradlew clean

echo "### Checking for missing Docker images ###"
docker_images=(
  "$PREFIX/rangiffler-auth-docker:latest"
  "$PREFIX/rangiffler-country-docker:latest"
  "$PREFIX/rangiffler-gateway-docker:latest"
  "$PREFIX/rangiffler-photo-docker:latest"
  "$PREFIX/rangiffler-userdata-docker:latest"
  "$PREFIX/rangiffler-logs-docker:latest"
)

image_exists() {
  docker images --format '{{.Repository}}:{{.Tag}}' | grep -q "^$1$"
}

missing_tasks=()
for image in "${docker_images[@]}"; do
  if image_exists "$image"; then
    echo "image exists: $image"
  else
    echo "image not found: $image"
    project=$(echo "$image" | sed -E "s#^$PREFIX/(.*)-docker:.*#\1#")
    missing_tasks+=(":$project:jibDockerBuild")
  fi
done

if [ ${#missing_tasks[@]} -gt 0 ]; then
  echo "### Building missing service images via Jib ###"
  bash ./gradlew "${missing_tasks[@]}"
else
  echo "### All service images are present. Skipping build ###"
fi

echo "### Starting environment ###"
docker compose up -d
docker ps -a