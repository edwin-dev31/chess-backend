#!/bin/bash
set -euo pipefail

COMMIT_HASH=$(git rev-parse --short HEAD)
IMAGE_NAME="edwindev31/chess-game"

echo "Building and pushing: ${IMAGE_NAME}:latest, ${IMAGE_NAME}:${COMMIT_HASH}"

docker buildx build \
  --platform linux/amd64,linux/arm64 \
  -t "${IMAGE_NAME}:latest" \
  -t "${IMAGE_NAME}:${COMMIT_HASH}" \
  --push \
  .
