#!/bin/bash
source ./docker.properties
export COMPOSE_PROFILES=test
export PROFILE=docker
export PREFIX="${IMAGE_PREFIX}"

export ALLURE_DOCKER_API=http://allure:5050/
export HEAD_COMMIT_MESSAGE="local build"
export ARCH=$(uname -m)

BROWSER=${1:-chrome}
if [ "$BROWSER" == "firefox" ]; then
  selenoid_image="selenoid/vnc_firefox:125.0"
else
  selenoid_image="selenoid/vnc_chrome:128.0"
fi

echo "### Selected browser: $BROWSER ($selenoid_image) ###"
export BROWSER

docker compose down

docker_containers=$(docker ps -a -q)
if [ -n "$docker_containers" ]; then
  echo "### Stopping and removing containers: $docker_containers ###"
  docker stop $docker_containers
  docker rm $docker_containers
fi

echo "### Java version ###"
java --version
bash ./gradlew clean

echo "### Checking for missing Docker images ###"

docker_images=(
  "$PREFIX/rangiffler-auth-docker:latest"
  "$PREFIX/rangiffler-country-docker:latest"
  "$PREFIX/rangiffler-gateway-docker:latest"
  "$PREFIX/rangiffler-photo-docker:latest"
  "$PREFIX/rangiffler-userdata-docker:latest"
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
    task=":$project:jibDockerBuild"
    missing_tasks+=("$task")
  fi
done

if [ ${#missing_tasks[@]} -gt 0 ]; then
  echo "### Building missing service images via Jib ###"
  bash ./gradlew "${missing_tasks[@]}"
else
  echo "### All service images are present. Skipping service image build. ###"
fi

docker pull $selenoid_image
docker compose up -d
docker ps -a
