#!/bin/sh -ex

DOCKER_IMAGE_NAME=$(mvn -o org.apache.maven.plugins:maven-help-plugin:3.1.0:evaluate -Dexpression=project.artifactId -q -DforceStdout)

VERSION=$(mvn -o org.apache.maven.plugins:maven-help-plugin:3.1.0:evaluate -Dexpression=project.version -q -DforceStdout)

TAG_FROM_BRANCH=$(git symbolic-ref -q HEAD | sed -e 's|refs/heads/||')

unzip -q -o -d target/dependencies target/monolith-0.0.1-SNAPSHOT-executable.jar

docker build \
       -f src/docker/Dockerfile \
       --build-arg BUILD_DATE=$(date -u +"%Y-%m-%dT%H:%M:%SZ") \
       --build-arg VCS_REF=$(git rev-parse HEAD) \
       --build-arg VERSION=${VERSION} \
       -t ${DOCKER_IMAGE_NAME}:${TAG_FROM_BRANCH} \
       -t ${DOCKER_IMAGE_NAME}:latest \
       .
