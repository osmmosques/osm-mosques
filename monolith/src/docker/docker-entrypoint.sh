#!/bin/bash -x

#
# For now, we still are root...
#
if [ -z ${PUID} ]
then
    PUID=1000
fi

if [ -z ${GUID} ]
then
    GUID=1000
fi

USERNAME=osmmosques

groupadd \
    -g ${GUID} \
    ${USERNAME}

useradd \
    -u ${PUID} \
    -g ${GUID} \
    -d ${OSM_MOSQUES_HOME} \
    ${USERNAME}

chown    ${USERNAME}:${USERNAME} ${OSM_MOSQUES_HOME}
chown -R ${USERNAME}:${USERNAME} ${OSM_MOSQUES_HOME}/app
chown    ${USERNAME}:${USERNAME} ${OSM_MOSQUES_DATA}

MAIN_CLASS=$(grep Start-Class ${OSM_MOSQUES_HOME}/app/META-INF/MANIFEST.MF | sed -e 's|Start-Class: ||' | sed -e 's|\r||')

env | sort

su ${USERNAME} -c "java ${JAVA_OPTS} -Dspring.profiles.active=${SPRING_PROFILES} -cp app:app/lib/* ${MAIN_CLASS}"
