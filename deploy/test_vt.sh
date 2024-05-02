#!/usr/bin/env zsh


kubectl get services -o json |  jq -r  '.items[].spec.selector.app' | while read svc; do
  IP="$( kubectl  get services/${svc}-service -o json | jq -r '.status.loadBalancer.ingress.[0].ip' )"
  URI=http://${IP}:8080
  VT=$( curl ${URI}/threads | jq  -r '.["spring.threads.virtual.enabled"]' )
  echo "the URI is $URI and $VT "
  OUTPUT_DIR=$HOME/Desktop/tests
  mkdir -p $OUTPUT_DIR
  for URL_PATH in jdbc memory-no-sleep memory-sleep ; do
    for i in {1..5}; do
      oha -c100 -n 1000 --no-tui ${URI}/${URL_PATH} > ${OUTPUT_DIR}/${URL_PATH}-${VT}
    done
  done
done