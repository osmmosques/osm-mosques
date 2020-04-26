#!/bin/sh -x

export DOCKER_NAME=osm_mosques_monolith

export DOCKER_IMAGE_NAME=monolith

export TAG=latest

case "$1" in
    -s|--shell)
        SHELL_MODE="-it --entrypoint /bin/bash"
        ;;

    *)
        SHELL_MODE=""
        ;;
esac

(
    HERE=$(pwd -P)
    [ "${HERE}" = "/" ] && HERE=$(dirname $0)
    [ "${HERE}" = "/" ] && HERE=/home/osmmosques/monolith

    TIMEZONE=Europe/Berlin

    JAVA_OPTS_TO_INJECT=
    JAVA_OPTS_TO_INJECT="${JAVA_OPTS_TO_INJECT} -Dfoo=bar"

    MARIADB_IP_ADDRESS=10.0.2.15

    OSMDATA_VOLUME="-v /home/hakan/src/osm/osmdata:/home/osm_mosques/osmdata"

    #
    # Overload any settings we might wish to
    #
    if [ -r ${HERE}/environment-overloads.sh ]
    then
        . ${HERE}/environment-overloads.sh
    fi

    mkdir -p ${HERE}/logs
    
    cd $(dirname $0) && \
        docker run \
               --rm \
               ${SHELL_MODE} \
               --name=${DOCKER_NAME} \
               --network=osm_mosques_backend \
               --link osm_mosques_mariadb:osm_mosques_mariadb \
               -p 8080:8080 \
               --add-host mariadb:${MARIADB_IP_ADDRESS} \
               --env JAVA_OPTS="${JAVA_OPTS_TO_INJECT}" \
               --env PUID=$(id -u) \
               --env PGID=$(id -g) \
               --env TZ=${TIMEZONE} \
               --env DEBUG_ENTRYPOINT=true \
               --env SPRING_PROFILES=docker \
               ${OSMDATA_VOLUME} \
               ${DOCKER_IMAGE_NAME}:${TAG} | tee ${HERE}/logs/docker-run.out
)

docker ps -a
