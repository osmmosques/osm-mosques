#! /bin/sh

OSMOSIS=${HOME}/osmosis-0.43.1
STORAGE=${HOME}/Dropbox/osmdata
WEBDATA=/home/tomcat/osm-mosques/data

COUNTIES=
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

COUNTRY=germany

extract_data() {

    country=${COUNTRY}

    county=$1
    type=$2

    if [ ${type} == "all" ]
    then
	tag="*"
    else
	tag=${type}
    fi

    ${OSMOSIS}/bin/osmosis \
	--read-pbf file=${FILE} \
	--tag-filter accept-nodes religion=${type} \
	--tag-filter reject-ways \
	--tag-filter reject-relations \
	--write-xml ${HOME}/tmp/${country}-${county}-religion-${type}.osm \
	> ${HOME}/tmp/${country}-${county}-religion-${type}.stdout.txt \
	2> ${HOME}/tmp/${country}-${county}-religion-${type}.stderr.txt

    mkdir -p ${STORAGE}/${country}/${county}/${MONTH}/${DAY}

    cp -f \
	${HOME}/tmp/${country}-${county}-religion-${type}.osm \
	${STORAGE}/${country}/${county}/${MONTH}/${DAY}/${country}-${county}-religion-${type}.osm

    cp -f \
	${HOME}/tmp/${country}-${county}-religion-${type}.osm \
	${WEBDATA}/${country}-${county}-religion-${type}.osm
}


country=${COUNTRY}
for county in ${COUNTIES}
do
    :
    mkdir -p ${HOME}/tmp
    cd ${HOME}/tmp

    FILE=${HOME}/tmp/${country}-${county}-latest.osm.pbf

    rm -f ${FILE}
    # wget http://download.geofabrik.de/osm/europe/${country}-${county}.osm.pbf
    wget -q http://download.geofabrik.de/europe/${country}/${county}-latest.osm.pbf -O ${FILE}

    MONTH=$(date +%Y%m --reference ${FILE})
    DAY=$(date +%Y%m%d --reference ${FILE})

    extract_data ${county} muslim

    # extract_data ${county} all
    # extract_data ${county} city
    # extract_data ${county} town
    # extract_data ${county} suburb
    # extract_data ${county} village
    # extract_data ${county} hamlet

    find ${STORAGE}/${country}/${county} -type f -a -mtime +14 | xargs --no-run-if-empty rm {} \;
    # find ${STORAGE}/${country}-${county} -type d -a -empty | xargs --no-run-if-empty rmdir {} \;
done

# TODO grep in property file to obtain username / password for webapp
country=${COUNTRY}
for county in ${COUNTIES}
do
    :
    # curl \
    # http://localhost:8888/osm-mosques-rest/osm/import \
    # -o ${STORAGE}/${country}-${county}/${MONTH}/${DAY}/curl.data.txt \
    # > ${STORAGE}/${country}-${county}/${MONTH}/${DAY}/curl.out \
    # 2> ${STORAGE}/${country}-${county}/${MONTH}/${DAY}/curl.err
done
