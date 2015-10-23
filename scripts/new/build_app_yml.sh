BASE_DIR=$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )
source $BASE_DIR/function.sh
init
cp ../../end-to-end-testing/src/test/resources/config/*.yml .
if [ ! -f /jar/ymlhelper-1.0.jar ]; then
   echo "/jar/ymlhelper-1.0.jar doesn't exist"
   exit 1
fi
write_auth_yml="java -jar /jar/ymlhelper-1.0.jar --f auth.yml --w"
write_env_yml="java -jar /jar/ymlhelper-1.0.jar --f env.yml --w"
write_test_yml="java -jar /jar/ymlhelper-1.0.jar --f test.yml --w"


# configure port forwarding
if [ ! "$CDS_SERVER" = "$TEST_AGENT" ]; then
    MYSQL_HOST=${CDS_SERVER}
    MYSQL_PORT=13306
    NATS_URL=http://${CDS_SERVER}:14222
    ETCD_URL=http://${CDS_SERVER}:14001
else
    etcdip=$(svc_ip etcd)
    etcdip=$(echo $etcdip | awk '{print $1}')
    property+=" --etcd.baseUrl=$etcdip"
    natsip=$(svc_ip gnatsd)
    natsip=$(echo $natsip | awk '{print $1}')
    MYSQL_HOST=$(svc_ip mysql)
    MYSQL_PORT=3306
    NATS_URL=http://${natsip//\"/}:4222
    ETCD_URL=http://${etcdip//\"/}:4001
fi

MYSQL_USER=${MYSQL_USER:-dbaas}
MYSQL_PWD=${MYSQL_PWD:-dbaas}
# test env data
DB_ENGINE_VERSION=${DB_ENGINE_VERSION:-mssql_2008R2}
PLAN_NAME=${PLAN_NAME:Small}
EDITION=${EDITION:-enterprise}
LICENSE_TYPE=${LICENSE_TYPE:-BYOL}
TEST_GROUP=${TEST_GROUP:-fulltest}
RETRYTIMES=${RETRYTIMES:-46}
RETRYTIMETOWAIT=${RETRYTIMETOWAIT:-4,8,16}
DISK_SIZE=${DISK_SIZE:-153600}
EXIT_ON_FAIL=${EXIT_ON_FAIL:-false}

# iam info
config_iam

# sns info
config_sns

$write_env_yml rest/baseUrl ${API_HEAD}
$write_env_yml nats/baseUrl ${NATS_URL}
$write_env_yml etcd/baseUrl ${ETCD_URL}
$write_env_yml mysql/host ${MYSQL_HOST}
$write_env_yml mysql/port ${MYSQL_PORT}
$write_env_yml mysql/user ${MYSQL_USER}
$write_env_yml mysql/password ${MYSQL_PWD}
$write_test_yml testGroup ${TEST_GROUP}
$write_test_yml testExcludeGroup ${EXCLUDE_GROUP}
$write_test_yml planName ${PLAN_NAME}
$write_test_yml dbEngineVersion ${DB_ENGINE_VERSION}
$write_test_yml allowedIP ${ALLOWED_IP}
$write_test_yml diskSize ${DISK_SIZE}
$write_test_yml exitOnFail ${EXIT_ON_FAIL}
$write_test_yml edition ${EDITION}
$write_test_yml licenseType ${LICENSE_TYPE}
$write_test_yml sns ${SNS}
$write_env_yml cdsServer/baseUrl ${CDS_SERVER}
$write_env_yml vdcNumber ${VDC_NUMBER}
$write_auth_yml authentication ${AUTHENTICATION}
$write_auth_yml praxisServerConnection/praxisConnectUrl ${PRAXIS_CONNECT_URL}
$write_auth_yml praxisServerConnection/dbadminUsername ${DBADMIN_USERNAME}
$write_auth_yml praxisServerConnection/dbadminPassword ${DBADMIN_PASSWORD}
$write_auth_yml praxisServerConnection/instanceOwner ${INSTANCE_OWNER_USERNAME}
$write_auth_yml praxisServerConnection/instanceOwnerPassword ${INSTANCE_OWNER_PASSWORD}
$write_auth_yml praxisServerConnection/endUsername ${ENDUSER_USERNAME}
$write_auth_yml praxisServerConnection/endUserPasswd ${ENDUSER_PASSWORD}
$write_auth_yml praxisServerConnection/iamUserName ${IAMUSER_USERNAME}
$write_auth_yml praxisServerConnection/iamUserPasswd ${IAMUSER_PASSWORD}
$write_test_yml retryTimes ${RETRYTIMES}
$write_test_yml retryTimeToWait ${RETRYTIMETOWAIT}
$write_test_yml billing/datasource/url jdbc:mysql://${MYSQL_HOST}:${MYSQL_PORT}/billingdb
$write_test_yml billing/datasource/username ${MYSQL_USER}
$write_test_yml billing/datasource/password ${MYSQL_PWD}
$write_test_yml gateway/datasource/url jdbc:mysql://${MYSQL_HOST}:${MYSQL_PORT}/dbaas
$write_test_yml gateway/datasource/username ${MYSQL_USER}
$write_test_yml gateway/datasource/password ${MYSQL_PWD}