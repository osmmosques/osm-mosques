#! /bin/sh

set -e

export HERE=$(dirname $(readlink -f $0))
. ${HERE}/settings.sh


cd ${TMPDIR}

for type in node way relation
do
    WORLD_FILE=${TMPDIR}/world-religion-muslim-${type}.osm

    wget "http://www.overpass-api.de/api/interpreter" \
	--post-file=${HERE}/data/query-${type}-religion-muslim.xml \
	-O ${WORLD_FILE}_temp \
	> ${WORLD_FILE}.out 2> ${WORLD_FILE}.err

    # Successfully got the file, now rename it to its proper place
    if [ -a ${WORLD_FILE}_temp ]
    then
        if [ -s ${WORLD_FILE}_temp ]
        then
	    mv ${WORLD_FILE}_temp ${WORLD_FILE}
	fi
    fi

    MONTH=$(date +%Y%m --reference ${WORLD_FILE})
    DAY=$(date +%Y%m%d --reference ${WORLD_FILE})

    mkdir -p ${STORAGE}/world/${MONTH}/${DAY}

    cp ${WORLD_FILE} ${STORAGE}/world/${MONTH}/${DAY}
done

# FINI
