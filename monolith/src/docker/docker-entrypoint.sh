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
    -d ${APP_HOME} \
    ${USERNAME}

chown    ${USERNAME}:${USERNAME} ${APP_HOME}
chown -R ${USERNAME}:${USERNAME} ${APP_HOME}/app
chown    ${USERNAME}:${USERNAME} ${APP_DATA}

if [ -r ${APP_HOME}/app/META-INF/MANIFEST.MF ]
then
    MAIN_CLASS=$(grep Start-Class ${APP_HOME}/app/META-INF/MANIFEST.MF | sed -e 's|Start-Class: ||' | sed -e 's|\r||')
else
    MAIN_CLASS=com.gurkensalat.osm.mosques.Monolith
fi

env | sort

su ${USERNAME} -c "java ${JAVA_OPTS} -Dspring.profiles.active=${SPRING_PROFILES} -cp app:app/lib/* ${MAIN_CLASS}"
