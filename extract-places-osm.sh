#! /bin/sh

STORAGE=${HOME}/Dropbox/osmdata
TMPDIR=${HOME}/tmp/osm-mosques
WEBDATA=/home/tomcat/osm-mosques/data

COUNTIES=

COUNTRIES=
COUNTRIES="${COUNTRIES} austria"
COUNTRIES="${COUNTRIES} cyprus"
COUNTRIES="${COUNTRIES} germany"
COUNTRIES="${COUNTRIES} netherlands"
COUNTRIES="${COUNTRIES} switzerland"
COUNTRIES="${COUNTRIES} turkey"

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

    ~/bin/osmconvert ${WORLD_FILE} -B=${POLY_FILE} > ${EXTRACT}

    mkdir -p ${STORAGE}/${country}/${MONTH}/${DAY}

    cp -f \
 	${EXTRACT} \
 	${STORAGE}/${country}/${MONTH}/${DAY}/${country}-${county}-religion-${type}.osm

    cp -f \
 	${EXTRACT} \
 	${WEBDATA}/${country}-${county}-religion-${type}.osm
}


mkdir -p ${TMPDIR}
cd ${TMPDIR}
WORLD_FILE=${TMPDIR}/world-religion-muslim.osm

wget "http://www.overpass-api.de/api/xapi?node[religion=muslim]" -O ${WORLD_FILE} \
    > ${WORLD_FILE}.out 2> ${WORLD_FILE}.err

MONTH=$(date +%Y%m --reference ${WORLD_FILE})
DAY=$(date +%Y%m%d --reference ${WORLD_FILE})

mkdir -p ${STORAGE}/world/${MONTH}/${DAY}

cp ${WORLD_FILE} ${STORAGE}/world/${MONTH}/${DAY}

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
    curl \
	http://localhost:8888/osm-mosques/rest/osm/import \
	-o ${STORAGE}/${country}/${MONTH}/${DAY}/curl-osm-mosques-import.data.txt \
	> ${STORAGE}/${country}/${MONTH}/${DAY}/curl-osm-mosques-import.out \
	2> ${STORAGE}/${country}/${MONTH}/${DAY}/curl-osm-mosques-import.err
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
    
