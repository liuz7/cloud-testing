[ "$XTRACE" == "on" ] && set -x

BASE_DIR=$( dirname "$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )")

source ${BASE_DIR}/common.sh
source ${BASE_DIR}/function.sh

export ENABLE_BILLING=${ENABLE_BILLING:-true}
#gateway
$YAMLER set $WORK_DIR/dbaas.config.yml variables/gateway-billing-enable $ENABLE_BILLING
$YAMLER set $WORK_DIR/dbaas.config.yml variables/gateway-debug-mode true
$YAMLER set $WORK_DIR/dbaas.config.yml variables/gateway-backup-delete-period 15
$YAMLER set $WORK_DIR/dbaas.config.yml variables/gateway-snapshot-compact-period 10

# release yml
$YAMLER set $WORK_DIR/dbaas.release.yml services[name=gateway-ng]/properties/env[GATEWAY_NG_DEBUG_MODE=*] GATEWAY_NG_DEBUG_MODE=true
$YAMLER set $WORK_DIR/dbaas.release.yml services[name=gateway-ng]/properties/env[BILLING_SAMPLE_PERIOD=*] BILLING_SAMPLE_PERIOD=10
$YAMLER set $WORK_DIR/dbaas.release.yml services[name=gateway-housekeeper]/properties/env[BACKUP_GUARD_DEBUG_MODE_ENABLED=*] BACKUP_GUARD_DEBUG_MODE_ENABLED=true
$YAMLER set $WORK_DIR/dbaas.release.yml services[name=gateway-housekeeper]/properties/env[BACKUP_GUARD_PERIOD=*] BACKUP_GUARD_PERIOD=60
$YAMLER set $WORK_DIR/dbaas.release.yml services[name=gateway-housekeeper]/properties/env[COMPACT_SNAPSHOT_PERIOD=*] COMPACT_SNAPSHOT_PERIOD=10

NODE_NAME=node-2012
NODEBUDDY_NAME=nodebuddy
if [ $MSSQL_VERSION = "mssql_2008R2" ]; then
  NODE_NAME=node-2008r2
  NODEBUDDY_NAME=nodebuddy-2008
fi
if [ $EDITION != "enterprise" ]; then
  NODE_NAME=${NODE_NAME}-std
fi
if [ $LICENSETYPE = "BYOL" ]; then
  NODE_NAME=${NODE_NAME}-byol
fi
NODES_IN_POOL=${NODES_IN_POOL:-10}
$YAMLER set $WORK_DIR/dbaas.config.yml parameters/pool.idle.* 0
$YAMLER set $WORK_DIR/dbaas.config.yml parameters/pool.idle.max.${NODEBUDDY_NAME} 1
$YAMLER set $WORK_DIR/dbaas.config.yml parameters/pool.idle.min.${NODEBUDDY_NAME} 1
$YAMLER set $WORK_DIR/dbaas.config.yml parameters/pool.idle.max.${NODE_NAME} ${NODES_IN_POOL}
$YAMLER set $WORK_DIR/dbaas.config.yml parameters/pool.idle.min.${NODE_NAME} ${NODES_IN_POOL}