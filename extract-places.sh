#! /bin/sh

OSMOSIS=${HOME}/osmosis-0.43.1
STORAGE=${HOME}/Dropbox/osmdata
WEBDATA=/home/tomcat/osm-mosques/data

extract_data() {
    country=$1
    type=$2

    if [ ${type} == "all" ]
    then
	tag="*"
    else
	tag=${type}
    fi

    ${OSMOSIS}/bin/osmosis \
	--read-pbf file=${FILE} \
	--tag-filter accept-nodes place=${tag} \
	--tag-filter reject-ways \
	--tag-filter reject-relations \
	--write-xml ${HOME}/tmp/${country}-places-${type}.osm \
	> ${HOME}/tmp/${country}-places-${type}.stdout.txt \
	2> ${HOME}/tmp/${country}-places-${type}.stderr.txt

    mkdir -p ${STORAGE}/${country}/${MONTH}/${DAY}

    cp -f \
	${HOME}/tmp/${country}-places-${type}.osm \
	${STORAGE}/${country}/${MONTH}/${DAY}/${country}-places-${type}.osm

    cp -f \
	${HOME}/tmp/${country}-places-${type}.osm \
	${WEBDATA}/${country}-places-${type}.osm
}


for country in turkey cyprus
do
    :
    mkdir -p ${HOME}/tmp
    cd ${HOME}/tmp

    FILE=${HOME}/tmp/${country}-latest.osm.pbf

    rm -f ${FILE}
    # wget http://download.geofabrik.de/osm/europe/${country}.osm.pbf
    wget -q http://download.geofabrik.de/europe/${country}-latest.osm.pbf -O ${FILE}

    MONTH=$(date +%Y%m --reference ${FILE})
    DAY=$(date +%Y%m%d --reference ${FILE})

    extract_data ${country} all
    extract_data ${country} city
    extract_data ${country} town
    extract_data ${country} suburb
    extract_data ${country} village
    extract_data ${country} hamlet

    find ${STORAGE}/${country} -type f -a -mtime +14 | xargs --no-run-if-empty rm {} \;
    # find ${STORAGE}/${country} -type d -a -empty | xargs --no-run-if-empty rmdir {} \;
done

# TODO grep in property file to obtain username / password for webapp
country=turkey
curl \
    http://localhost:8888/osm-mosques-rest/osm/import \
    -o ${STORAGE}/${country}/${MONTH}/${DAY}/curl.data.txt \
    > ${STORAGE}/${country}/${MONTH}/${DAY}/curl.out \
    2> ${STORAGE}/${country}/${MONTH}/${DAY}/curl.err \
