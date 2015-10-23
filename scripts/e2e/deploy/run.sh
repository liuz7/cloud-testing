#!/usr/bin/env bash

function wait_for_deployment_done() {
  echo "Waiting for $CDS_DEPLOYMENT deployment done"

  local idle_count=0
  local active_tasks
  local starting_vms
  local failed_vapp
  local failed_svc
  local redeploy_svc_count=0
  while [ true ]
  do
    let idle_count=idle_count+1
    [ $($CDS_CLI tasks --json | jq ".[]|.operation.state" | grep -c WAITING) -gt 0 ] && idle_count=0
    [ $($CDS_CLI vms --json | jq ".[]|.state" | grep -v -c OK) -gt 0 ] && idle_count=0
    [ $idle_count -eq 12 ] && break
    failed_vapp=$($CDS_CLI vapps --json | jq '.[]|select(.state == "ERROR")|.id' | head -1 |tr -d '"')
    [ -n "$failed_vapp" ] && $CDS_CLI vapps rebuild $failed_vapp &
    failed_svc=$($CDS_CLI svc show --all  --json | jq '.[]|select(.state == "ERROR")|select(.["vm-affined"] != true)|.id' | head -1 |tr -d '"')
    [ -n "$failed_svc" ] && {
      echo "WARNING:Redeploy failed svc:$failed_svc"
      $CDS_CLI svc redeploy $failed_svc &
      let redeploy_svc_count=redeploy_svc_count+1
      if [ $redeploy_svc_count -eq 30 ]; then
          echo "ERROR:Fail to deploy all services after 30 redeployment"
          exit 1
      fi
    }
    echo "Waiting for 5 seconds to check cds tasks status (idle count=$idle_count)"
    # should ignore tasks that have not been updated for more than 30 minutes
    let expired_updated_time=$((($(date +%s)-30*60)*1000))
    active_task=$($CDS_CLI tasks --json | jq ".[]|select(.operation.state != \"FINISHED\")|select(.[\"updated-at\"] > $expired_updated_time)|.[\"object-id\"]+\" > \"+.action")
    starting_vms=$($CDS_CLI vms --json | jq '.[]|select(.state != "OK")|.template.name+" > "+.state')
    [ -n "$active_task" ] && echo "active tasks: $active_task"
    [ -n "$starting_vms" ] && echo "starting vms: $starting_vms"
    sleep 5
  done
  local failed_svc=$($CDS_CLI svc --json | jq '.[]|select(.state == "ERROR")|.name')
    if [ -n "$failed_svc" ]
    then
        echo "The following svc failed to deploy:$failed_svc"
        exit -1
    fi
    let expired_updated_time=$((($(date +%s)-30*60)*1000))
    expired_task=$($CDS_CLI tasks --json | jq '.[]|select(.operation.state != "FINISHED")|select(.["updated-at"] < $expired_updated_time) | .["object-id"]+" "+.["object-type"]' | sed -e 's#"##g')
    while read -r line; do
        read id type <<<$line
        if [ $type = "vapps" ]; then
            $CDS_CLI vapps delete $id --force
        elif [ $type = "services" ]; then
            $CDS_CLI svc delete $id
        fi
    done <<< "$expired_task"


    $CDS_CLI svc --json | jq '.[]|.name+":"+.interfaces[0].ip' | tee -a "$WORK_DIR/${WDC}_dev_${VDC_NUMBER}.${CDS_DEPLOYMENT}.log"

}

function wait_for_deployment_destroy() {
    while [ $($CDS_CLI vms --json | jq "length") -gt 0 ]
    do
      $CDS_CLI vms --json | grep "Not Found" > /dev/null &&
      {
          echo "Deployment $dep is destoyed"
          break;
      }
      echo "There are still $($CDS_CLI vms --json | jq "length") vms to be destroyed"
      sleep 10
    done
}

function clean() {
  PROD=$1
  export CDS_DEPLOYMENT=$(get_deploy_name $PROD)
  [ -n "$CDS_DEPLOYMENT" ] && {
    echo "Delete deployemnt $CDS_DEPLOYMENT"
    $CDS_CLI deployment delete --force
    wait_for_deployment_destroy
  }
}

function deploy() {
  [ -z "$1" ] && return 1

  PROD=$1
  export CDS_DEPLOYMENT=$(get_deploy_name $PROD)
  [ -z "$CDS_DEPLOYMENT" ] && export CDS_DEPLOYMENT="$PROD-$(date '+%Y%m%d_%H%M%S')"

  echo "Starting deploy $PROD:$CDS_DEPLOYMENT"
  RELEASE_DIR="/var/www/lighttpd/releases/${PROD}"
  $SSH root@$CDS_SERVER "mkdir -p $RELEASE_DIR"
  $SCP $WORK_DIR/${PROD}.release.yml root@$CDS_SERVER:$RELEASE_DIR/release.yml
  $SSH root@$CDS_SERVER "ln -sf $RELEASE_DIR/release.yml ~/${PROD}.release.yml"
  $SCP $WORK_DIR/${PROD}.config.yml root@$CDS_SERVER:

  $CDS_CLI tune --sys sys.recovervm.disable=1
  $CDS_CLI deployment sync $WORK_DIR/${PROD}.config.yml http://$CDS_SERVER/releases/${PROD} || exit 1
  wait_for_deployment_done
}

function post_deploy_dsp() {
  export CDS_DEPLOYMENT=$(get_deploy_name dsp)
  DSP_NATS_IP=$(svc_ip gnatsd)
  DM_SERVER_IP=$(svc_ip dmserver)
  echo "dsp nats ip: $DSP_NATS_IP"
  echo "dm server ip: $DM_SERVER_IP"

  if [ -z "$$DSP_NATS_IP" ] || [ -z "$DM_SERVER_IP" ]; then
    echo "dsp deployment failed"
    exit 1
  fi
}

function post_deploy_dbaas() {
  export CDS_DEPLOYMENT=$(get_deploy_name dbaas)
  for param in $($CDS_CLI tune --json | jq '.[]|select(.value!="0")|.param' | sed 's#"##g' | grep min | grep -v nodebuddy); do
    $CDS_CLI tune $param=0 > /dev/null
  done
}

set -o | grep xtrace | grep on && XTRACE=on || XTRACE=off
export XTRACE

BASE_DIR=$( dirname "$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )")

source ${BASE_DIR}/common.sh
source ${BASE_DIR}/function.sh

case "$1" in
  clean )
    clean $2
    ;;
  deploy )
    deploy $2
    post_deploy_$2
    ;;
  yaml )
    bash $BASE_DIR/deploy/prepare_${2}_yml.sh
    ;;
  dallas-yaml )
    bash $BASE_DIR/deploy/prepare_dallas_${2}_yml.sh
    ;;
  config-for-debug )
    bash $BASE_DIR/deploy/config_debug_parameters.sh
    ;;
  clean-all )
    clean dbaas
    clean dsp
    ;;
  deploy-all )
    bash $BASE_DIR/deploy/prepare_dsp_yml.sh
    deploy dsp
    post_deploy_dsp

    bash $BASE_DIR/deploy/prepare_dbaas_yml.sh
    deploy dbaas
    post_deploy_dbaas
    ;;
  init-env )
    prepare_cds
    ;;
  * )
    echo "Usage:"
    echo "  clean dsp|dbaas"
    echo "  deploy dsp|dbaas"
    echo "  yaml dsp|dbaas"
    echo "  clean-all"
    echo "  deploy-all"
    echo "  init-env"
    ;;
esac
