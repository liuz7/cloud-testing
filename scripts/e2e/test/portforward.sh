#!/usr/bin/env bash

BASE_DIR=$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )

export CDS_DEPLOYMENT=$(cds deployment --json | jq '.[]|select(.state=="OK")|"\(.release .name)=\(.name)"' | grep "dbaas" | sed -e "s#dbaas=##" -e 's#"##g')

TMP_FILE=$BASE_DIR/tmpsvc.json
cds svc --json >$TMP_FILE

function forward_svc_ip()
{
  LOCAL_PORT=$1
  SVC=$2
  TARGET_PORT=$3
  TARGET_HOST=$(cat $TMP_FILE | jq "map(select(.name==\"$SVC\") | .interfaces[] | select(.network==\"mgmt\") | .ip ) | .[0]" | sed 's#"##g')

  if [ "$TARGET_HOST" == "null" ]; then
    echo "Service $SVC is not deployed."
  else
    echo "forward $LOCAL_PORT to $TARGET_HOST:$TARGET_PORT"
    iptables -t nat -A PREROUTING -p tcp --dport $LOCAL_PORT -j DNAT --to $TARGET_HOST:$TARGET_PORT
    iptables -t nat -A POSTROUTING -d $TARGET_HOST -j MASQUERADE
  fi
}

iptables -F
iptables -t nat -F
forward_svc_ip 13306 mysql 3306
forward_svc_ip 14001 etcd 4001
forward_svc_ip 14222 gnatsd 4222
forward_svc_ip 8085 apihead 8085
forward_svc_ip 8086 apihead 8086
forward_svc_ip 33306 galera-proxy 33306
