#!/bin/bash
#rm -r binaries -f 
mkdir -p binaries


SB_NAME="example"
SB_VERSION="0.0.5-rc.3"
MON_VERSION="0.0.1"

SERVICE="ftp://geile.cloud/de/evoila/cf/broker/osb-$SB_NAME/$SB_VERSION/osb-$SB_NAME-$SB_VERSION.jar"

function get(){
    n=$(basename $1)
    if [[ ! -f $n ]]; then
        wget $1 -P binaries -nc
    fi
}

wget --user="maven" --password="jOai055?" $SERVICE -O binaries/osb-$SB_NAME.jar
./translate.sh
tile build $@
./deploy-tile.sh
