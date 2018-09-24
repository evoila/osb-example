#!/bin/bash
PRODUKT_NAME="osb-example"
om -k credential-references -p $PRODUKT_NAME -f json | jq .[] | xargs -L1  bash -c "echo \"\$0:\";om -k credentials -p $PRODUKT_NAME -t json -c \$0"
