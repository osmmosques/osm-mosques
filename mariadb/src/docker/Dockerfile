FROM mariadb:10.3

#
# Set arguments on build
#
ARG BUILD_DATE
ARG VCS_REF
ARG VCS_VERSION

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
# VOLUME ${APP_DATA}

#
# Expose necessary ports...
#
# EXPOSE 3306

#
# Add out database configuration
#
COPY src/docker/root /

