[ "$XTRACE" == "on" ] && set -x

BASE_DIR=$( dirname "$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )")
source $BASE_DIR/common.sh
source $BASE_DIR/function.sh

check_env API_HEAD
check_env MSSQL_VERSION
check_env EDITION
check_env LICENSETYPE
check_env PLAN_NAME

if [ -n "$CDS_SERVER" ]; then
  export CDS_DEPLOYMENT=$(get_deploy_name dbaas)

  echo "Generate test configuration for Dbaas release $(get_release_version)"

  [ "$(get_release_variable sns-stubbed)" == "true" ] && SNS=false || SNS=true
  AUTHENTICATION=$(get_release_variable iam-enabled)
  PRAXIS_HOST=$(get_release_variable praxis-login-host)
  BILLING=$(get_release_variable gateway-billing-enable)

  # configure services
  MYSQL_HOST=${CDS_SERVER}
  MYSQL_PORT=13306
  MYSQL_USER=${MYSQL_USER:-dbaas}
  MYSQL_PWD=${MYSQL_PWD:-dbaas}
  NATS_URL=http://${CDS_SERVER}:14222
  ETCD_URL=http://${CDS_SERVER}:14001

  # workaround for sql datasource initializaitons
  [ "$BILLING" == "false" ] && MYSQL_HOST=0.0.0.0

else

  check_env SNS #=true
  check_env AUTHENTICATION #=true
  check_env PRAXIS_HOST #iam.vchs.vmware.com
  check_env BILLING #true

  [ "$BILLING" == "true" ] && {
    check_env MYSQL_HOST "Billing is enabled, MYSQL_HOST & MYSQL_PORT for dbaas db are required."
    check_env MYSQL_PORT "Billing is enabled, MYSQL_HOST & MYSQL_PORT for dbaas db are required."
  } || {
    MYSQL_HOST=0.0.0.0
    MYSQL_PORT=13306
  }
  MYSQL_USER=${MYSQL_USER:-dbaas}
  MYSQL_PWD=${MYSQL_PWD:-dbaas}

  NATS_URL=http://0.0.0.0:14222
  ETCD_URL=http://0.0.0.0:14001
fi

[ "$SNS" == "true" ] && check_env ALLOWED_IP

case "$PRAXIS_HOST" in
  # Praxis 6
  *d1p360tlm*)
    DBADMIN_USERNAME=stu@aas-dbaas.com
    DBADMIN_PASSWORD=Pass@123
    IAMUSER_USERNAME=stu@aas-dbaas.com
    IAMUSER_PASSWORD=Pass@123
    ENDUSER_USERNAME=leu@aas-dbaas.com
    ENDUSER_PASSWORD=Pass@123
  ;;
  #Praxis Prod
  *iam.vchs*)
    DBADMIN_USERNAME=sre3.dbaas.automation@gmail.com
    DBADMIN_PASSWORD=testBeta1#123
    IAMUSER_USERNAME=sre3.dbaas.automation@gmail.com
    IAMUSER_PASSWORD=testBeta1#123
    ENDUSER_USERNAME=sre6.dbaas.automation@gmail.com
    ENDUSER_PASSWORD=testEnduser#123
  ;;
  *d4p4*)
   DBADMIN_USERNAME=admin1@dbaas-int.com
   DBADMIN_PASSWORD=Pass@123
   IAMUSER_USERNAME=admin2@dbaas-int.com
   IAMUSER_PASSWORD=Pass@123
   ENDUSER_USERNAME=admin3@dbaas-int.com
   ENDUSER_PASSWORD=Pass@123
 ;;
  *)
    echo "Unknown PRAXIS_HOST $PRAXIS_HOST"
    exit 1
  ;;
esac
#PROXYIP=$(svc_ip apihead)
GATEWAY_DEBUG=$(get_release_variable gateway-debug-mode)
DEPLOY_ENV=$(get_release_variable deploy-env)

cat > application.yml <<EOF
rest:
  baseUrl: ${API_HEAD}
nats:
  baseUrl: ${NATS_URL}
etcd:
  baseUrl: ${ETCD_URL}
mysql:
  host: ${MYSQL_HOST}
  port: ${MYSQL_PORT}
  user: ${MYSQL_USER}
  password: ${MYSQL_PWD}
#proxyip: ${PROXYIP}
authentication: ${AUTHENTICATION}

praxisServerConnection:
    praxisConnectUrl:   https://${PRAXIS_HOST}
    praxisOrg1:         PEPSI
    dbadminUsername:    ${DBADMIN_USERNAME}
    dbadminPassword:    ${DBADMIN_PASSWORD}
    endUsername:        ${ENDUSER_USERNAME}
    endUserPasswd:      ${ENDUSER_PASSWORD}
    iamUserName:        ${IAMUSER_USERNAME}
    iamUserPasswd:      ${IAMUSER_PASSWORD}

dbEngineVersion: ${MSSQL_VERSION}
planName: ${PLAN_NAME}
edition: ${EDITION}
licenseType: ${LICENSETYPE}
allowedIP: ${ALLOWED_IP}
sns: ${SNS}
gateway_debug: ${GATEWAY_DEBUG}
deployEnv: ${DEPLOY_ENV}
EOF
