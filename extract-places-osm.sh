#! /bin/sh

set -e

export HERE=$(dirname $(readlink -f $0))
. ${HERE}/settings.sh


# TODO grep in property file to obtain username / password for webapp
for country in world # ${COUNTRIES}
do
    :
    mkdir -p ${STORAGE}/${country}/${MONTH}/${DAY}
    curl -X POST \
        "http://localhost:8888/rest/osm/import" \
        -o ${LOGDIR}/curl-osm-mosques-import.data.txt \
        > ${LOGDIR}/curl-osm-mosques-import.out \
        2> ${LOGDIR}/curl-osm-mosques-import.err

    for x in curl-osm-mosques-import.data.txt curl-osm-mosques-import.out curl-osm-mosques-import.err
    do
        mv ${LOGDIR}/${x} ${STORAGE}/${country}/${MONTH}/${DAY}/${x}
    done
    
done

# FINI
