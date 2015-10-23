[ "$XTRACE" == "on" ] && set -x

BASE_DIR=$( dirname "$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )")

source ${BASE_DIR}/common.sh
source ${BASE_DIR}/function.sh
source ${BASE_DIR}/wdc/wdc_env.sh

check_env CDS_SERVER

export ENABLE_SNS=${ENABLE_SNS:-true}
export ENABLE_IAM=${ENABLE_IAM:-true}
export ENABLE_BILLING=${ENABLE_BILLING:-true}
export PRAXIS=${PRAXIS:-PRAXISPROD}

ARTIFACTS_URL=${ARTIFACTS_URL:-http://tempest-ci.eng.vmware.com/job/devtest_integration_env/job/dbaas-release/lastSuccessfulBuild/artifact/artifacts}

if [ -z $DBAAS_RELEASE_DIR ]; then
    # download from jenkins job
    pushd $WORK_DIR
    wget -q $ARTIFACTS_URL/config.yml -O dbaas.config.yml
    wget -q $ARTIFACTS_URL/release.yml -O dbaas.release.yml
    popd
else
    # copy from $DBAAS_RELEASE_DIR
    rm -rf $WORK_DIR/dbaas.*.yml
    cp $DBAAS_RELEASE_DIR/dbaas.*.yml $WORK_DIR/.
fi

# release yml
$YAMLER set $WORK_DIR/dbaas.release.yml services[name=gateway-ng]/properties/env[GATEWAY_NG_DEBUG_MODE=*] GATEWAY_NG_DEBUG_MODE=true
$YAMLER set $WORK_DIR/dbaas.release.yml services[name=gateway-ng]/properties/env[BILLING_SAMPLE_PERIOD=*] BILLING_SAMPLE_PERIOD=10
$YAMLER set $WORK_DIR/dbaas.release.yml services[name=gateway-housekeeper]/properties/env[BACKUP_GUARD_DEBUG_MODE_ENABLED=*] BACKUP_GUARD_DEBUG_MODE_ENABLED=true
$YAMLER set $WORK_DIR/dbaas.release.yml services[name=gateway-housekeeper]/properties/env[BACKUP_GUARD_PERIOD=*] BACKUP_GUARD_PERIOD=60
$YAMLER set $WORK_DIR/dbaas.release.yml services[name=gateway-housekeeper]/properties/env[COMPACT_SNAPSHOT_PERIOD=*] COMPACT_SNAPSHOT_PERIOD=10

# currently this values should not be configured on wdc1 & wdc3
#if [ "$ENABLE_BILLING" = true ]; then
#  $YAMLER set $WORK_DIR/dsp.release.yml services[name=billingservice]/properties/env[PRAXIS_USER=*] PRAXIS_USER=cds-prod-od@vmware.com
#  $YAMLER set $WORK_DIR/dsp.release.yml services[name=billingservice]/properties/env[PRAXIS_PASSWORD=*] PRAXIS_PASSWORD='9aCd2B5$my'
#  $YAMLER set $WORK_DIR/dsp.release.yml services[name=billingservice]/properties/env[DBAAS_SERVICE_TYPE=*] DBAAS_SERVICE_TYPE=com.vmware.vchs.dbaas
#fi

echo "dbaas release is:"
cat $WORK_DIR/dbaas.release.yml

# config yml
case "$PRAXIS" in
  "PRAXIS6" )
    IAM_URL="https://d1p361tlm-mgmt-vcimup-vip.vchslabs.vmware.com"
    PRAXIS="d1p360tlm-iamup-pmpapache-a.vchslabs.vmware.com"
    IAM_USER='stu@aas-dbaas.com'
    IAM_PWD='Pass@123'
    ;;
  "PRAXISPROD" )
    IAM_URL="https://us-texas-1-14.vchs.vmware.com"
    PRAXIS="iam.vchs.vmware.com"
    IAM_USER='hhu@vmware.com'
    IAM_PWD='Ca$hc0w'
  ;;
esac

[ "$ENABLE_SNS" = "true" ] && SNS_STUBBED=false || SNS_STUBBED=true

#uncomment some node configurations
sed -i -e "s/#pool.idle/pool.idle/" $WORK_DIR/dbaas.config.yml

#vcd
$YAMLER set $WORK_DIR/dbaas.config.yml vcd_template/vcloud-host $VCLOUD_HOST
$YAMLER set $WORK_DIR/dbaas.config.yml vcd_template/username $VDC_USER
$YAMLER set $WORK_DIR/dbaas.config.yml vcd_template/vdc-name $VDC_NAME
$YAMLER set $WORK_DIR/dbaas.config.yml vcd_template/password $VDC_PASSWORD
$YAMLER set $WORK_DIR/dbaas.config.yml vcd_template/catalog $STORAGE_CATALOG_NAME

#network
$YAMLER set $WORK_DIR/dbaas.config.yml network_template/properties/network pri_net_$VDC_NUMBER
$YAMLER set $WORK_DIR/dbaas.config.yml targets[name=mgmt]/networks[name=ext]/properties/network pub_net_$VDC_NUMBER
$YAMLER set $WORK_DIR/dbaas.config.yml targets[name=mssql]/networks[name=mssqldata]/properties/network data_net_$VDC_NUMBER
#bugfix pipeline
$YAMLER set $WORK_DIR/dbaas.config.yml targets[name=mgmt]/networks[name=mgmt]/properties/network pri_net_$VDC_NUMBER
$YAMLER set $WORK_DIR/dbaas.config.yml targets[name=mssql]/networks[name=mssqlmgmt]/properties/network pri_net_$VDC_NUMBER
$YAMLER set $WORK_DIR/dbaas.config.yml targets[*]/properties/username $VDC_USER
$YAMLER set $WORK_DIR/dbaas.config.yml targets[*]/properties/vdc-name $VDC_NAME
$YAMLER set $WORK_DIR/dbaas.config.yml targets[*]/properties/catalog $STORAGE_CATALOG_NAME
$YAMLER set $WORK_DIR/dbaas.config.yml targets[*]/properties/vcloud-host $VCLOUD_HOST
$YAMLER set $WORK_DIR/dbaas.config.yml targets[*]/properties/password $VDC_PASSWORD
$YAMLER set $WORK_DIR/dbaas.config.yml targets[*]/storages[*]/properties/storage-profile $STORAGE_NAME


#storage
$YAMLER set $WORK_DIR/dbaas.config.yml storage_template/properties/storage-profile $STORAGE_NAME
$YAMLER set $WORK_DIR/dbaas.config.yml targets[*]/storages[*]/properties/storage-profile $STORAGE_NAME

#iam & praxis
$YAMLER set $WORK_DIR/dbaas.config.yml variables/sns-iam-enabled $ENABLE_IAM
$YAMLER set $WORK_DIR/dbaas.config.yml variables/iam-enabled $ENABLE_IAM
$YAMLER set $WORK_DIR/dbaas.config.yml variables/iam_url $IAM_URL
$YAMLER set $WORK_DIR/dbaas.config.yml variables/iam_user $IAM_USER
$YAMLER set $WORK_DIR/dbaas.config.yml variables/iam_pwd $IAM_PWD
$YAMLER set $WORK_DIR/dbaas.config.yml variables/iam-detach-praxis false
$YAMLER set $WORK_DIR/dbaas.config.yml variables/praxis-login-host $PRAXIS
$YAMLER set $WORK_DIR/dbaas.config.yml variables/praxis-signer-host $PRAXIS

#sns
$YAMLER set $WORK_DIR/dbaas.config.yml variables/portal-sns-enabled $ENABLE_SNS
$YAMLER set $WORK_DIR/dbaas.config.yml variables/sns-stubbed $SNS_STUBBED
$YAMLER set $WORK_DIR/dbaas.config.yml variables/sns-mock false
$YAMLER set $WORK_DIR/dbaas.config.yml variables/sns-vdc-list/vdcs/mssql/vcdUrl "https://$VCLOUD_HOST"
$YAMLER set $WORK_DIR/dbaas.config.yml variables/sns-vdc-list/vdcs/mssql/password $VDC_PASSWORD
$YAMLER set $WORK_DIR/dbaas.config.yml variables/sns-vdc-list/vdcs/mssql/userName $VDC_USER
$YAMLER set $WORK_DIR/dbaas.config.yml variables/sns-vdc-list/vdcs/mssql/vdcName $VDC_NAME
$YAMLER set $WORK_DIR/dbaas.config.yml variables/sns-vdc-list/vdcs/mssql/serviceNetworkName $SERVICE_NETWORK_NAME

#gateway
$YAMLER set $WORK_DIR/dbaas.config.yml variables/gateway-billing-enable $ENABLE_BILLING
$YAMLER set $WORK_DIR/dbaas.config.yml variables/gateway-debug-mode true
$YAMLER set $WORK_DIR/dbaas.config.yml variables/gateway-backup-delete-period 15
$YAMLER set $WORK_DIR/dbaas.config.yml variables/gateway-snapshot-compact-period 10

#others
$YAMLER set $WORK_DIR/dbaas.config.yml variables/wsus-server ""

# currently this values should not be configured on wdc1 & wdc3
#if [ "$ENABLE_BILLING" = true ]; then
#  $YAMLER set $WORK_DIR/dbaas.config.yml variables/gateway-praxis-enable true
#  $YAMLER set $WORK_DIR/dbaas.config.yml variables/service_location us-texas-1-14.vchs.vmware.com
#  $YAMLER set $WORK_DIR/dbaas.config.yml variables/plan_scope PRIVATE
#  $YAMLER set $WORK_DIR/dbaas.config.yml variables/portal_ui_url https://us-texas-1-14.vchs.vmware.com/appsrv/
#  $YAMLER set $WORK_DIR/dbaas.config.yml variables/xaas_url https://us-texas-1-14.vchs.vmware.com
#  $YAMLER set $WORK_DIR/dbaas.config.yml variables/sc_url https://us-texas-1-14.vchs.vmware.com/
#  $YAMLER set $WORK_DIR/dbaas.config.yml variables/apihead_url https://us-texas-1-14-appsvc.vca.vmware.com/appsrv/servicebroker/v1/dbaas/mssql
#  $YAMLER set $WORK_DIR/dbaas.config.yml variables/basic_auth_token b2QucHJvZEBjb21wZWF1Lm9yZzpWY2ExMDI5MzgjKg==
#  $YAMLER set $WORK_DIR/dbaas.config.yml variables/billing_xaas_user cds-prod-od@vmware.com
#  $YAMLER set $WORK_DIR/dbaas.config.yml variables/billing_xaas_password '9aCd2B5$my'
#fi

#prod mode
[ "$PROD_MODE" == "true" ] && {
  $YAMLER set $WORK_DIR/dbaas.config.yml variables/deployment_env prod
  $YAMLER set $WORK_DIR/dbaas.config.yml variables/deploy-env PROD
} || {
  $YAMLER set $WORK_DIR/dbaas.config.yml variables/deployment_env dev
  $YAMLER set $WORK_DIR/dbaas.config.yml variables/deploy-env DEV
}

#dsp releated
export CDS_DEPLOYMENT=$(get_deploy_name dsp)
[ -n "$CDS_DEPLOYMENT" ] && {
  DSP_NATS_IP=$(svc_ip gnatsd)
  DM_SERVER_IP=$(svc_ip dmserver)

  echo "set proper dsp nats ip and dm server ip in config.yml"
  $YAMLER set $WORK_DIR/dbaas.config.yml variables/dsp-nats-url "nats://$DSP_NATS_IP:4222"
  $YAMLER set $WORK_DIR/dbaas.config.yml variables/dmserver-ip $DM_SERVER_IP
}

#nodes
$YAMLER set $WORK_DIR/dbaas.config.yml parameters/pool.idle.min.node-* 0
$YAMLER set $WORK_DIR/dbaas.config.yml parameters/pool.idle.max.node-* ''
$YAMLER set $WORK_DIR/dbaas.config.yml parameters/pool.idle.*.nodebuddy* 1

echo "dbaas config is:"
cat $WORK_DIR/dbaas.config.yml
