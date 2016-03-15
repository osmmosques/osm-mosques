#! /bin/sh

set -e

export HERE=$(dirname $(readlink -f $0))
. ${HERE}/settings.sh


STATES=

COUNTRIES=
COUNTRIES="${COUNTRIES} albania"
COUNTRIES="${COUNTRIES} andorra"
COUNTRIES="${COUNTRIES} austria"
COUNTRIES="${COUNTRIES} azores"
COUNTRIES="${COUNTRIES} belarus"
COUNTRIES="${COUNTRIES} belgium"
COUNTRIES="${COUNTRIES} bosnia-herzegovina"
COUNTRIES="${COUNTRIES} bulgaria"
COUNTRIES="${COUNTRIES} croatia"
COUNTRIES="${COUNTRIES} cyprus"
COUNTRIES="${COUNTRIES} czech-republic"
COUNTRIES="${COUNTRIES} denmark"
COUNTRIES="${COUNTRIES} estonia"
COUNTRIES="${COUNTRIES} faroe-islands"
COUNTRIES="${COUNTRIES} finland"
COUNTRIES="${COUNTRIES} france"
COUNTRIES="${COUNTRIES} georgia"
COUNTRIES="${COUNTRIES} germany"
COUNTRIES="${COUNTRIES} great-britain"
COUNTRIES="${COUNTRIES} greece"
COUNTRIES="${COUNTRIES} hungary"
COUNTRIES="${COUNTRIES} iceland"
COUNTRIES="${COUNTRIES} ireland-and-northern-ireland"
COUNTRIES="${COUNTRIES} isle-of-man"
COUNTRIES="${COUNTRIES} italy"
COUNTRIES="${COUNTRIES} kosovo"
COUNTRIES="${COUNTRIES} latvia"
COUNTRIES="${COUNTRIES} liechtenstein"
COUNTRIES="${COUNTRIES} lithuania"
COUNTRIES="${COUNTRIES} luxembourg"
COUNTRIES="${COUNTRIES} macedonia"
COUNTRIES="${COUNTRIES} malta"
COUNTRIES="${COUNTRIES} moldova"
COUNTRIES="${COUNTRIES} monaco"
COUNTRIES="${COUNTRIES} montenegro"
COUNTRIES="${COUNTRIES} netherlands"
COUNTRIES="${COUNTRIES} norway"
COUNTRIES="${COUNTRIES} poland"
COUNTRIES="${COUNTRIES} portugal"
COUNTRIES="${COUNTRIES} romania"
COUNTRIES="${COUNTRIES} serbia"
COUNTRIES="${COUNTRIES} slovakia"
COUNTRIES="${COUNTRIES} slovenia"
COUNTRIES="${COUNTRIES} spain"
COUNTRIES="${COUNTRIES} sweden"
COUNTRIES="${COUNTRIES} switzerland"
COUNTRIES="${COUNTRIES} turkey"
COUNTRIES="${COUNTRIES} ukraine"

extract_data() {

    country=$1
    state=$2
    type=$3
    religion=$4

    # if [ ${type} == "all" ]
    # then
    #     tag="*"
    # else
    #     tag=${type}
    # fi

    EXTRACT=${TMPDIR}/${country}-${state}-religion-${religion}-${type}.osm

    if [ "${country}" == "world" ]
    then
	/bin/cp ${WORLD_FILE} ${EXTRACT}
    else
	${HOME}/bin/osmconvert ${WORLD_FILE} -B=${POLY_FILE} -o=${EXTRACT}
    fi

    mkdir -p ${STORAGE}/${country}/${MONTH}/${DAY}

    cp -f \
         ${EXTRACT} \
         ${STORAGE}/${country}/${MONTH}/${DAY}/${country}-${state}-religion-${religion}-${type}.osm

    cp -f \
         ${EXTRACT} \
         ${WEBDATA}/${country}-${state}-religion-${religion}-${type}.osm
}


# HERE=${PWD}/$(dirname $0)
HERE=$(dirname $0)

mkdir -p ${TMPDIR}
cd ${TMPDIR}

for type in node way relation
do
    WORLD_FILE=${TMPDIR}/world-religion-muslim-${type}.osm

    wget "http://www.overpass-api.de/api/interpreter" \
	--post-file=${HERE}/data/query-${type}-religion-muslim.xml \
	-O ${WORLD_FILE} \
	> ${WORLD_FILE}.out 2> ${WORLD_FILE}.err

    MONTH=$(date +%Y%m --reference ${WORLD_FILE})
    DAY=$(date +%Y%m%d --reference ${WORLD_FILE})

    mkdir -p ${STORAGE}/world/${MONTH}/${DAY}

    cp ${WORLD_FILE} ${STORAGE}/world/${MONTH}/${DAY}
done

WORLD_FILE=${TMPDIR}/world-religion-muslim-node.osm


# Copy WEBDATA back to Dropbox
mkdir -p ${STORAGE}/_data-osm/
rsync -ar ${WEBDATA}/ ${STORAGE}/_data-osm/


for country in ${COUNTRIES} world
do

    STATES=

    if [ "germany" == "${country}" ]
    then
        STATES="${STATES} baden-wuerttemberg"
        STATES="${STATES} bayern"
        STATES="${STATES} berlin"
        STATES="${STATES} brandenburg"
        STATES="${STATES} bremen"
        STATES="${STATES} hamburg"
        STATES="${STATES} hessen"
        STATES="${STATES} mecklenburg-vorpommern"
        STATES="${STATES} niedersachsen"
        STATES="${STATES} nordrhein-westfalen"
        STATES="${STATES} rheinland-pfalz"
        STATES="${STATES} saarland"
        STATES="${STATES} sachsen"
        STATES="${STATES} sachsen-anhalt"
        STATES="${STATES} schleswig-holstein"
        STATES="${STATES} thueringen"
    else
        STATES="${STATES} all"
    fi

    for state in ${STATES}
    do
        :
        mkdir -p ${TMPDIR}
        cd ${TMPDIR}


	if [ "${country}" != "world" ]
	then
            if [ "${state}" == "all" ]
            then
		POLY_URL=http://download.geofabrik.de/europe/${country}.poly
		POLY_FILE=${TMPDIR}/${country}.poly
            else
		POLY_URL=http://download.geofabrik.de/europe/${country}/${state}.poly
		POLY_FILE=${TMPDIR}/${country}-${state}.poly
            fi

            wget "${POLY_URL}" -O ${POLY_FILE} \
		> ${POLY_FILE}.out 2> ${POLY_FILE}.err

            MONTH=$(date +%Y%m --reference ${POLY_FILE})
            DAY=$(date +%Y%m%d --reference ${POLY_FILE})

            mkdir -p ${STORAGE}/${country}/${MONTH}/${DAY}

            cp ${POLY_FILE} ${STORAGE}/${country}/${MONTH}/${DAY}
	fi

        for type in node way relation
        do
            WORLD_FILE=${TMPDIR}/world-religion-muslim-${type}.osm
            if [ -a ${WORLD_FILE} ]
            then
                if [ -s ${WORLD_FILE} ]
                then
                    :
                    extract_data ${country} ${state} ${type} muslim
                fi
            fi
        done
    done

    find ${STORAGE}/${country} -type f -a -mtime +14 | xargs --no-run-if-empty rm
    find ${STORAGE}/${country} -type d -a -empty | xargs --no-run-if-empty rmdir
done

# Copy WEBDATA back to Dropbox
# mkdir -p ${STORAGE}/_data-osm/${MONTH}/${DAY}
# rsync -ar ${WEBDATA}/ ${STORAGE}/_data-osm/${MONTH}/${DAY}/

# TODO grep in property file to obtain username / password for webapp
for country in world # ${COUNTRIES}
do
    :
    mkdir -p ${STORAGE}/${country}/${MONTH}/${DAY}
    curl \
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
