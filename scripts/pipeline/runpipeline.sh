set -o | grep xtrace | grep on && XTRACE=on || XTRACE=off
export XTRACE

export API_HEAD=http://192.168.70.20:8085
export CDS_SERVER=10.158.14.26   
                        
source pipelinetest_env.sh
###
[ "$XTRACE" == "on" ] && set -x

[ -n ${1} ] && { 
	TEST_GROUP=${1} 
	echo $TEST_GROUP
} || {
	TEST_GROUP=populatetest

}

BASE_DIR=$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )

export WORK_DIR=${WORK_DIR:-.work}

export SSH="sshpass -p password ssh -o StrictHostKeyChecking=no -o UserKnownHostsFile=/dev/null"
export SCP="sshpass -p password scp -o StrictHostKeyChecking=no -o UserKnownHostsFile=/dev/null"

export CDS_URL="http://$CDS_SERVER/api"
export CDS_CLI=$WORK_DIR/cds
###

cd /tmp/scripts

TEST_DIR＝${TEST_DIR:-~/end2end}

rm -rf $TEST_DIR
mkdir -p $TEST_DIR

[ -e "application.yml" ] && cp application.yml $TEST_DIR/.
E2E_JAR=$(get_file "end-to-end-testing-*.jar")
[ -n "$E2E_JAR" ] && {
cp "$E2E_JAR" $IPELINE_TEST_DIR/.
n -sf "$TEST_DIR/$(basename "$E2E_JAR")" $EST_DIR/end-to-end-testing.jar
}

pushd $TEST_DIR

bash $BASE_DIR/../install_java.sh || exit 1
[ -e "end-to-end-testing.jar" ] || bash $BASE_DIR/update_e2e_jar.sh

SPRING_PROFILE=${SPRING_PROFILE:-$(get_spring_profile)}
cd $TEST_DIR
cp ~/runtest.sh .

#java -jar end-to-end-testing.jar --testGroup=$TEST_GROUP 

# configured jar parameters
java -jar end-to-end-testing.jar --rest.baseUrl=$API_HEAD  --praxisServerConnection.dbadminPassword=testBeta1#123  --edition=standard --vdcNumber=13  --sns=true --licenseType=LI --planName=Small --dbEngineVersion=mssql_2012 --exitOnFail=false --authentication=true --diskSize=153600  --praxisServerConnection.praxisConnectUrl=iam.vchs.vmware.com --praxisServerConnection.dbadminUsername=sre3.dbaas.automation@gmail.com  --praxisServerConnection.endUserPasswd=Pass@123 --praxisServerConnection.iamUserName=cshan@vmware.com --praxisServerConnection.iamUserPasswd=870411Abc_ --praxisServerConnection.endUsername=fanzhang.jeffrey@gmail.com --testGroup=$TEST_GROUP 

popd


