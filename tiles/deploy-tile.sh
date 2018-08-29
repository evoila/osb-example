#!/bin/bash
main() {

[ "$(om -v)" != "$(curl -s "https://raw.githubusercontent.com/pivotal-cf/om/master/version")" ] && echo  -e "-----------------------\nnew om version https://github.com/pivotal-cf/om/releases/download/$(curl -s "https://raw.githubusercontent.com/pivotal-cf/om/master/version")/om-$(uname | tr '[:upper:]' '[:lower:]')\n-----------------------" 1>&2

  [ -f "$TILE_NETWORK" ] && cp "$TILE_NETWORK"  ./network_object.yml || echo "${TILE_NETWORK:""}" >  ./network_object.yml
  # convert network YML into JSON
  # RESOURCES
  [ -f "$TILE_RESOURCES" ] && cp  "$TILE_RESOURCES" ./resources_object.yml || echo "${TILE_RESOURCES:""}" > ./resources_object.yml
  # PROPERTIES
  [ -f "$TILE_PROPERTIES" ] &&  cp "$TILE_PROPERTIES" ./properties_object.yml ||  echo "${TILE_PROPERTIES:""}" > ./properties_object.yml

  # upload product
  om \
    -k \
  upload-product \
    --product "$(ls $PRODUCT_PATH | head -n1)"

  om \
    -k \
   stage-product \
    -p "$TILE_PRODUCT_NAME" \
   --product-version "$(  ls $PRODUCT_PATH | grep -o -e "[0-9]*\.[0-9]*\.[0-9]*")"
  cat ./network_object.yml ./resources_object.yml ./properties_object.yml > config.tmp.yml

sleep 20

  # updates properties and resources parameters for tile in Ops Mgr
  om \
    --skip-ssl-validation \
    configure-product \
    --product-name "$TILE_PRODUCT_NAME" \
    -c config.tmp.yml

  rm ./network_object.yml ./resources_object.yml ./properties_object.yml config.tmp.yml
  produkt="$(om \
      -t $OM_TARGET \
      -k \
    curl \
      -p "/api/v0/staged/products" \
      -x GET | \
    jq  "[.[].installation_name]" | \
    grep -o -e "$TILE_PRODUCT_NAME-[a-f0-9]*")"

sleep 20

while  om \
    -k \
  curl \
    -p "/api/v0/installations" \
    -x POST \
    -d "{\"deploy_products\": [\"$produkt\"]}" 2>&1  | grep -q "progress" 2>/dev/null 1>/dev/null; do
	echo "wait running job is finish"
	sleep 60
    om -t $OM_TARGET -k curl -p "/api/v0/installations/current_log" 2>/dev/null 1>/dev/null
	done
	om -t $OM_TARGET -k curl -p "/api/v0/installations/current_log" | tee log.txt && \
	[ "$(tail -n5 log.txt| grep -o -e "[0-9]*")" -eq 0 ]

}
PRODUCT_PATH=$PWD/product/*.pivotal
TILE_PRODUCT_NAME="$( ls $PRODUCT_PATH | xargs basename | grep -o -e "[a-Z]*\(-\?[a-Z]\+\)\+" | head -n1 )"

whereis om >/dev/null && [ ! -z "$OM_USERNAME" ] && [ ! -z "$OM_PASSWORD" ] &&  [ ! -z "$OM_TARGET" ] &&  main
