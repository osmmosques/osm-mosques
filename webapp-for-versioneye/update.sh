#!/bin/bash -x

VERSIONEYE_SERVER=https://www.versioneye.com

. ${HOME}/.m2/versioneye.properties

API_KEY=${api_key}
PROJECT_ID=$(grep versioneye.project.id $(dirname $0)/pom.xml | sed -e 's|versioneye.project.id||g' | sed -e 's|[<>/ ]||g')
PROJECT_NAME=$(grep artifactId $(dirname $0)/pom.xml | head -n 2 | tail -n 1 | sed -e 's|artifactId||g' | sed -e 's|[<>/ ]||g')
PROJECT_FILE=$(dirname $0)/target/classes/package.json

json=$( curl -F name=${PROJECT_NAME} -F project_file=${PROJECT_FILE} "${VERSIONEYE_SERVER}/api/v2/projects/${PROJECT_ID}?api_key=${API_KEY}" )

echo ${json} | jq "." > target/versioneye.answer

project_id=$(echo $json | jq '.id' | sed 's/"//g' )
dep_number=$(echo $json | jq '.dep_number')
out_number=$(echo $json | jq '.out_number')
violations=$(echo $json | jq '.licenses_red')

echo ""
echo "Dependencies: $dep_number"
echo "Outdated: $out_number"
echo "License violations: $violations"
echo ""

if [ violations == 0 ]; then
    echo "exit with status code 0"
    exit 0
else
    echo "exit with status code 2"
    exit 2
fi

echo "Never ever!"
