#! /bin/sh

STORAGE=${HOME}/Dropbox/osmdata
TMPDIR=${HOME}/tmp/osm-mosques
WEBDATA=/home/tomcat/osm-mosques/data
LOGDIR=${HOME}/logs

COUNTIES=

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
    county=$2
    type=$3

    if [ ${type} == "all" ]
    then
        tag="*"
    else
        tag=${type}
    fi

    EXTRACT=${TMPDIR}/${country}-${county}-religion-${type}.osm

    /home/osm-mosques/bin/osmconvert ${WORLD_FILE} -B=${POLY_FILE} > ${EXTRACT}

    mkdir -p ${STORAGE}/${country}/${MONTH}/${DAY}

    cp -f \
         ${EXTRACT} \
         ${STORAGE}/${country}/${MONTH}/${DAY}/${country}-${county}-religion-${type}.osm

    cp -f \
         ${EXTRACT} \
         ${WEBDATA}/${country}-${county}-religion-${type}.osm
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


for country in ${COUNTRIES}
do

    COUNTIES=

    if [ "germany" == "${country}" ]
    then
        COUNTIES="${COUNTIES} baden-wuerttemberg"
        COUNTIES="${COUNTIES} bayern"
        COUNTIES="${COUNTIES} berlin"
        COUNTIES="${COUNTIES} brandenburg"
        COUNTIES="${COUNTIES} bremen"
        COUNTIES="${COUNTIES} hamburg"
        COUNTIES="${COUNTIES} hessen"
        COUNTIES="${COUNTIES} mecklenburg-vorpommern"
        COUNTIES="${COUNTIES} niedersachsen"
        COUNTIES="${COUNTIES} nordrhein-westfalen"
        COUNTIES="${COUNTIES} rheinland-pfalz"
        COUNTIES="${COUNTIES} saarland"
        COUNTIES="${COUNTIES} sachsen"
        COUNTIES="${COUNTIES} sachsen-anhalt"
        COUNTIES="${COUNTIES} schleswig-holstein"
        COUNTIES="${COUNTIES} thueringen"
    else
        COUNTIES="${COUNTIES} all"
    fi

    for county in ${COUNTIES}
    do
        :
        mkdir -p ${TMPDIR}
        cd ${TMPDIR}


        if [ "${county}" == "all" ]
        then
            POLY_URL=http://download.geofabrik.de/europe/${country}.poly
            POLY_FILE=${TMPDIR}/${country}.poly
        else
            POLY_URL=http://download.geofabrik.de/europe/${country}/${county}.poly
            POLY_FILE=${TMPDIR}/${country}-${county}.poly
        fi

        wget "${POLY_URL}" -O ${POLY_FILE} \
            > ${POLY_FILE}.out 2> ${POLY_FILE}.err

        MONTH=$(date +%Y%m --reference ${POLY_FILE})
        DAY=$(date +%Y%m%d --reference ${POLY_FILE})

        mkdir -p ${STORAGE}/${country}/${MONTH}/${DAY}

        cp ${POLY_FILE} ${STORAGE}/${country}/${MONTH}/${DAY}

        if [ -a ${WORLD_FILE} ] 
        then
            if [ -s ${WORLD_FILE} ]
            then
                :
                extract_data ${country} ${county} muslim
            fi
        fi
    done

    find ${STORAGE}/${country} -type f -a -mtime +14 | xargs --no-run-if-empty rm
    find ${STORAGE}/${country} -type d -a -empty | xargs --no-run-if-empty rmdir
done

# TODO grep in property file to obtain username / password for webapp
for country in germany # ${COUNTRIES}
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

db=osm_mosques

DB_DIR=${STORAGE}/database/${MONTH}/${DAY}

mkdir -p ${DB_DIR}

mysqldump -uroot -p$(cat ${HOME}/.my.pass) --skip-extended-insert ${db} \
    > ${DB_DIR}/${db}-dump.sql

mysql -uroot -p$(cat ${HOME}/.my.pass) ${db} \
    -e "select d_key, lat, lon, name, id, addr_country, addr_city, addr_street, addr_housenumber from osm_places order by name limit 9999;" \
    > ${DB_DIR}/${db}-osm_places.sql

mysql -uroot -p$(cat ${HOME}/.my.pass) ${db} \
    -e "select p_table, p_id, d_key, d_val, id, version from osm_tags order by p_table, p_id, d_key;" \
    > ${DB_DIR}/${db}-osm_tags.sql
    
