FROM adoptopenjdk:11-jre-openj9

#
# Set arguments on build
#
ARG BUILD_DATE
ARG VCS_REF
ARG VCS_VERSION

#
# Variables and locales...
#
ENV \
    APP_HOME="/home/osm_mosques" \
    APP_DATA="/home/osm_mosques/osmdata" \
    USER_ID="1000"

#
# LABEL can occur multiple times resulting in only one layer...
#
LABEL org.label-schema.docker.dockerfile="/Dockerfile"
LABEL org.label-schema.license="Apache-2.0"
LABEL org.opencontainers.image.authors="Hakan Tandogan <hakan@tandogan.com>"
LABEL org.opencontainers.image.created=${BUILD_DATE}
LABEL org.opencontainers.image.revision=${VCS_REF}
LABEL org.opencontainers.image.source="https://github.com/osmmosques/osm-mosques.git"
LABEL org.opencontainers.image.vendor="nobody : )"
LABEL org.opencontainers.image.version=${VCS_VERSION}

#
# Expose some volumes
#
VOLUME ${APP_DATA}

#
# Expose necessary ports...
#
EXPOSE 8080

#
# Set working directory and entrypoint
#
WORKDIR ${APP_HOME}
COPY src/docker/docker-entrypoint.sh /
RUN chmod +x /docker-entrypoint.sh
ENTRYPOINT ["/docker-entrypoint.sh"]

#
# Finally, install our own software...
#
COPY target/dependency       ${APP_HOME}/app/lib
COPY target/classes/META-INF ${APP_HOME}/app/META-INF
COPY target/classes          ${APP_HOME}/app
