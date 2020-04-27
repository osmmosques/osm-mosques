FROM azul/zulu-openjdk:11

#
# Set arguments on build
#
ARG BUILD_DATE
ARG VCS_REF
ARG VERSION

#
# Variables and locales...
#
ENV \
    OSM_MOSQUES_HOME="/home/osm_mosques" \
    OSM_MOSQUES_DATA="/home/osm_mosques/osmdata" \
    USER_ID="1000"   

#
# Basic build-time metadata as defined at http://label-schema.org
#
LABEL org.label-schema.build-date=${BUILD_DATE} \
    org.label-schema.docker.dockerfile="/Dockerfile" \
    org.label-schema.license="EPL-2.0" \
    org.label-schema.name="osmmosques" \
    org.label-schema.vendor="osmmosques" \
    org.label-schema.version=${VERSION} \
    org.label-schema.description="yadda yadda yadda" \
    org.label-schema.url="https://www.osmmosques.org/" \
    org.label-schema.vcs-ref=$VCS_REF \
    org.label-schema.vcs-type="Git" \
    org.label-schema.vcs-url="https://github.com/osmmosques/osm-mosques.git" \
    maintainer="Hakan Tandogan <hakan@tandogan.com>"

#
# Expose some volumes
#
VOLUME ${OSM_MOSQUES_DATA}

#
# Expose necessary ports...
#
EXPOSE 8080

#
# Set working directory and entrypoint
#
WORKDIR ${OSM_MOSQUES_HOME}
COPY src/docker/docker-entrypoint.sh /
RUN chmod +x /docker-entrypoint.sh
ENTRYPOINT ["/docker-entrypoint.sh"]

#
# Finally, install our own software...
#
COPY target/dependencies/BOOT-INF/lib     ${OSM_MOSQUES_HOME}/app/lib
COPY target/dependencies/META-INF         ${OSM_MOSQUES_HOME}/app/META-INF
COPY target/dependencies/BOOT-INF/classes ${OSM_MOSQUES_HOME}/app
