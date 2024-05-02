#!/usr/bin/env bash

OUTPUT_DIR=$HOME/Desktop/tests

mkdir -p $OUTPUT_DIR

cat IPS | while read IP ; do
  URI=http://${IP}:8080
  VT=$( curl ${URI}/threads | jq  -r '.["spring.threads.virtual.enabled"]' )
  echo "the URI is $URI and $VT "
  for URL_PATH in jdbc memory-no-sleep memory-sleep ; do
    for i in {1..5}; do
      ab -c 100 -n 1000 ${URI}/${URL_PATH} > ${OUTPUT_DIR}/${URL_PATH}-${VT}
    done
  done
done