#!/bin/bash
function svc_ip()
{
 name=${1:-etcd}
 id=${2:-0}
 ip=$($cds_cli svc --json | jq ".[]|select(.name==\"$name\")|.interfaces[$id].ip")
 echo ${ip//\"/}
}

function setup_cds_cli()
{
  DEP_ROOT=${1:-.}
  echo "Download latest cds cli and put it to:$DEP_ROOT"
  [ -f $DEP_ROOT/cds ] && rm -fr $DEP_ROOT/cds
  if [ "$(uname)" == "Linux" ]
  then
      wget -q  http://tempest-ci.eng.vmware.com/view/5-CDS/job/CDS_Build/lastSuccessfulBuild/artifact/out/cds-cli/linux_amd64/cds  -O $DEP_ROOT/cds
  fi

  if [ "$(uname)" == "Darwin" ]
  then
      wget -q  http://tempest-ci.eng.vmware.com/view/5-CDS/job/CDS_Build/lastSuccessfulBuild/artifact/out/cds-cli/darwin_amd64/cds -O $DEP_ROOT/cds
  fi
  chmod a+x $DEP_ROOT/cds
  export cds_cli=$DEP_ROOT/cds
}

# Usage info
function show_help() {
cat << EOF
Usage: ${0##*/} [-h] [-c CDSSERVER] [-r runningAgent]  [-a apiHeadIp] [-l testClass] [-t testCase] [-g testGroup] [-p PlanName] [-d sqlVersion] [-f]
run end to end test from specified cds server
    -h          display this help and exit
    -c          cds server IP address
    -r          the IP address of the agent that end to end test will run on, if not specified, then cds server will be used
    -a          api head IP address, could be a public IP that customer could access. Default is api heade's private IP address
    -l          test class name
    -t          single test case name
    -g          test group name, such as sanitytest/alphatest/fulltest
    -p          plan name for instance, such as Small/Large/Medium
    -d          mssql version, such as mssql_2008R2/mssql_2012
    -s          data disk size in mega for node vm, if not specified, then default 153600 will be used
    -f          stop on first test failure
    -o          allowed ip for customer vdc
An example for this:
./rune2e.sh -c 10.156.75.25 -r 10.156.75.26 -g sanityTest -p Small -f
In the above example, tes will run on test agent 10.156.75.26, with the DBaaS vCD's cds server as 10.156.75.25. The selected test group is sanitytest,
and it will run Small plan size. The test run will exit on the first test failure
EOF
}

set -x
set +e

snapshot=false
stop_onfailure=false
disk_size=153600
OPTIND=1 # Reset is necessary if getopts was used previously in the script.  It is a good idea to make this local in a function.
while getopts ":l:t:v:g:p:d:s:c:r:a:n:o:u:w:U:W:hf" opt; do
    case "$opt" in
        h)
            show_help
            exit 0
            ;;
        f)  stop_onfailure=true
            ;;
        c)  cds_server_ip=$OPTARG
            ;;
        r)  test_agent_ip=$OPTARG
            ;;
        a)  api_head_ip=$OPTARG
            ;;
        l)  test_class=$OPTARG
            ;;
        t)  test_case=$OPTARG
            ;;
        v)  parameter=$OPTARG
            ;;
        g)  test_group=$OPTARG
            ;;
        p)  test_plan=$OPTARG
            ;;
        d)  db_engine=$OPTARG
            ;;
        n)  snapshot=true
            ;;
        o)  allowed_ip=$OPTARG
            ;;
        u)  praxis_user=$OPTARG
            ;;
        w)  praxis_pwd=$OPTARG
            ;;
        s)  disk_size=$OPTARG
            ;;
        U)  praxis_enduser=$OPTARG
            ;;
        W)  praxis_endpwd=$OPTARG
            ;;
    esac
done
git_root=$(git rev-parse --show-toplevel)
[ -z "$git_root" ] && {
 echo "You need to run the script under cloud-testing repo"
 exit
}
echo "Check required variables are provided"
if [ -z "$cds_server_ip" ]
then
  echo "cds server IP is needed"
  show_help
  exit 1
fi
[ -n "$test_plan" ] || test_plan="Small"
[ -n "$test_group" ] || test_group="fulltest"

which cds > /dev/null && cds_cli=cds
[ -n "$cds_cli" ] || setup_cds_cli
export CDS_URL="http://$cds_server_ip/api"
dbaas_dep=$($cds_cli deployment --json| jq '.[]|select(.state=="OK")|.name' | grep dbaas| head -1| tr -d '"')
if [ -z "$dbaas_dep" ]; then
    echo "No dbaas deployment is found"
    exit 1
fi
echo "The current dbaas deployment is named:$dbaas_dep"
export CDS_DEPLOYMENT=$dbaas_dep

# get private ip address for rabbitmq and api head if not specified
[ -z "$api_head_ip" ] && api_head_ip=$(svc_ip apihead) && api_head_ip="http://"$api_head_ip":8085"
if [ -z "$api_head_ip" ]; then
    echo "No api head svc is found in the dbaas deployment"
    exit 1
fi

# check whether iam is enabled on the target deployment
iam_enabled=$($cds_cli releases --json | jq '.[]|select(.current)|.state.variables|.["iam-enabled"]'|tr '"' '#')
# if iam is enabled,then we need to get praxis access credential and update iam configuraiton file per this deployment
if [ "$iam_enabled" == "#true#" ];then
    echo "IAM is enabled"
    praxis_url_raw=$($cds_cli releases --json | jq '.[]|select(.current)|.state.variables|.["praxis-login-host"]')
    praxis_url="${praxis_url_raw//\"/}"
    case "$praxis_url_raw" in 
      #Praxis 0
      *d1p500tlm*)
        praxis_user=stu@praxis0.com
        praxis_pwd=Welcome@123
      ;;
      # Praxis 6
      *d1p360tlm*)
        praxis_user=stu@aas-dbaas.com
        praxis_pwd=Pass@123
      ;;
      #Praxis INT
      *d4p4*)
        praxis_user=admin1@dbaas-int.com
        praxis_pwd=Pass@123
        praxis_enduser=admin2@dbaas-int.com
        praxis_endpwd=Pass@123
      ;;
      *iam.vchs*)
        praxis_user=cshan@vmware.com
        praxis_pwd=870411Abc_
        praxis_enduser=fanzhang.jeffrey@gmail.com
        praxis_endpwd=Pass@123
      ;;
    esac
    echo "PRAXIS url: $praxis_url"
    echo "PRAXIS user name: $praxis_user"
    echo "PRAXIS password: $praxis_pwd"
    echo "PRAXIS endUser name: $praxis_enduser"
    echo "PRAXIS endUser password: $praxis_endpwd"
    echo "render $auth_file_path with provided praxis information"
    [ -z "$praxis_url" ] && {
     echo "ERROR: praxis url is not provided when IAM is enabled!"
     exit 1
    }
    [ -z "$praxis_user" ] && {
     echo "ERROR: praxis user name is not provided when IAM is enabled!"
     exit 1
    }
    [ -z "$praxis_pwd" ] && {
     echo "ERROR: praxis password is not provided when IAM is enabled!"
     exit 1
    }
    test_resource_root="$git_root/end-to-end-testing/src/test/resources"
    iam_file_path="$test_resource_root/config/application.yml"
    key_file_path="$test_resource_root/keys/apihead.aas.vmware.com.crt"
    sed -i -e "s/praxisUrl/${praxis_url}/g" $iam_file_path
    sed -i -e "s/praxisName/${praxis_user}/g" $iam_file_path
    sed -i -e "s/praxisPassword/${praxis_pwd}/g" $iam_file_path
    sed -i -e "s/praxisEndName/${praxis_enduser}/g" $iam_file_path
    sed -i -e "s/praxisEndPassword/${praxis_endpwd}/g" $iam_file_path
    echo "resulted iam configuration file:"
    echo "================================="
    cat $iam_file_path
    echo "================================="
    sudo keytool -import -file $key_file_path -keystore /usr/java/jdk1.8.0_45/jre/lib/security/cacerts -alias server -noprompt -storepass changeit
fi
# Need to put single-quote around EOF or EOL to avoid variable substitution
cat << 'EOF' > ./.test.sh
set -x
set +e
current_time=$(date "+%Y.%m.%d-%H.%M.%S")
test_base=/root/end2end
test_root=/root/end2end/run/$current_time
echo "test parameters: $*"
snapshot=false
stop_onfailure=false
iam_enable=false
disk_size=153600
while getopts ":a:e:c:n:t:g:p:d:s:x:u:w:o:b:U:W:fi" opt; do
    case "$opt" in
        a)  api_head_ip=$OPTARG
            ;;
        e)  parameter=$OPTARG
            ;;
        c)  test_class=$OPTARG
            ;;
        n)  snapshot=true
            ;;
        t)  test_case=$OPTARG
            ;;
        g)  test_group=$OPTARG
            ;;
        p)  test_plan=$OPTARG
            ;;
        d)  db_engine=$OPTARG
            ;;
        s)  disk_size=$OPTARG
            ;;
        x)  praxis_url=$OPTARG
            ;;
        u)  praxis_user=$OPTARG
            ;;
        w)  praxis_pwd=$OPTARG
            ;;
        i)  iam_enable=true
            ;;
        o)  allowed_ip=$OPTARG
            ;;
        b)  property=$OPTARG
            ;;
        f)  stop_onfailure=true
            ;;
        U)  praxis_enduser=$OPTARG
            ;;
        W)  praxis_endpwd=$OPTARG
            ;;
    esac
done

[ -z "$api_head_ip" ] && {
  echo "ERROR: api head Ip is not provided"
  exit 1
}
[ -z "$test_group" ] && {
  test_group='sanitytest'
  echo "Use test group default: $test_group"
}
[ -z "$test_plan" ] && {
  test_plan='Small'
  echo "Use plan size default: $test_plan"
}
[ -z "$db_engine" ] && {
  db_engine='mssql_2008R2 '
  echo "Use mssql version default: $db_engine"
}
if [ -d $test_root ]; then
  echo "clean up test root:$test_root"
  rm -fr  $test_root
else
  echo "create test root:$test_root"
  mkdir -p $test_root
fi

rpm -qa | grep epel > /dev/null || {
  rpm -Uvh http://dl.fedoraproject.org/pub/epel/6/x86_64/epel-release-6-8.noarch.rpm
}
#install jq if not exist
which jq > /dev/null || yum -y install jq

rpm -qa | grep "jdk1.8" > /dev/null || {
  echo "Need to install jdk 8 first"
  wget -q  http://10.158.10.44/packages/jdk-8u45-linux-x64.rpm -O jdk8.rpm
  rpm -i jdk8.rpm
  rm -fr jdk8.rpm
}
java8_bin=$(dirname "$(/usr/sbin/alternatives --list | grep jdk1.8 | grep javac| awk '{ print $3}')")
java8="$java8_bin/java"
test_jar=$test_root/end-to-end-testing.jar
which jq > /dev/null || yum -y install jq
if [ "$snapshot" = true ]; then
  cloud_path="http://tempest-ci.eng.vmware.com/view/6-SRE/job/e2e-cloudtesting-snapshot/lastSuccessfulBuild/artifact/end-to-end-testing/build/libs/end-to-end-testing-1.0.0-SNAPSHOT.jar"
else
  jar_version=$(curl -s http://tempest-ci.eng.vmware.com/view/6-SRE/job/e2e-cloudtesting-stable/lastSuccessfulBuild/api/json | jq '.number')
  echo "Downloading latest e2e test build:$jar_version"
  cloud_path="http://tempest-ci.eng.vmware.com/view/6-SRE/job/e2e-cloudtesting-stable/lastSuccessfulBuild/artifact/end-to-end-testing/build/libs/end-to-end-testing-1.0.0-b$jar_version.jar"
fi
wget -q $cloud_path -O $test_jar
chmod a+x $test_jar
test_cmd="$java8 -jar $test_jar --rest.baseUrl=$api_head_ip $property --planName=$test_plan --dbEngineVersion=$db_engine --exitOnFail=$stop_onfailure --authentication=$iam_enable --diskSize=$disk_size --edition=standard --licenseType=LI"
[ -n "$test_class" ] && test_cmd+=" --testClass=$test_class"
[ -n "$test_case" ] && test_cmd+=" --testMethod=$test_case"
[ -n "$allowed_ip" ] && test_cmd+=" --allowedIP=$allowed_ip"
if [ -z "$test_case" ]; then
  if [ -z "$test_class" ]; then
    [ -n "$test_group" ] && test_cmd+=" --testGroup=$test_group"
  fi
fi
if [ "$iam_enable" = true ]; then
  test_cmd+=" --praxisServerConnection.praxisConnectUrl=$praxis_url"
  test_cmd+=" --praxisServerConnection.dbadminUsername=$praxis_user"
  test_cmd+=" --praxisServerConnection.dbadminPassword=$praxis_pwd"
  test_cmd+=" --praxisServerConnection.endUsername=$praxis_enduser"
  test_cmd+=" --praxisServerConnection.endUserPasswd=$praxis_endpwd"
fi
test_cmd+=" $parameter"
echo "====================================="
echo "Will start end to end test with the following parameters under test base:$test_base"
echo "api head ip: $api_head_ip"
[ -z "$test_class" ] || echo "test class: $test_class"
[ -z "$test_case" ] || echo "test case: $test_case"
[ -z "$test_group" ] || echo "test group: $test_group"
[ -z "$test_plan" ] || echo "plan size: $test_plan"
echo "mssql version: $db_engine"
echo "enable IAM: $iam_enable"
echo "exit on the first test failure: $stop_onfailure"
echo "Java at:$java8"
echo "End to end test jar version:$jar_version"
echo "Test command line:"
echo "$test_cmd"
echo "====================================="

cd $test_root && eval $test_cmd
EOF
chmod a+x ./.test.sh
test_base=/root/end2end
test_run="$test_base/rune2e.sh -a $api_head_ip -g $test_group -p $test_plan"
[ -n "$test_class" ] && test_run+=" -c $test_class"
[ -n "$test_case" ] && test_run+=" -t $test_case"
[ -n "$db_engine" ] && test_run+=" -d $db_engine"
[ "$stop_onfailure" = true ] && test_run+=" -f"
[ "$snapshot" = "true" ] && test_run+=" -n 100"

if [ "$iam_enabled" == "#true#" ]; then
  test_run+=" -i"
  test_run+=" -x $praxis_url"
  test_run+=" -u $praxis_user"
  test_run+=" -w $praxis_pwd"
  test_run+=" -U $praxis_enduser"
  test_run+=" -W $praxis_endpwd"
fi
[ -n "$parameter" ] && test_run+=" -e $parameter"
[ -z "$test_agent_ip" ] && test_agent_ip=$cds_server_ip
[ -n "$allowed_ip" ] && test_run+=" -o $allowed_ip"
if [ -n "$allowed_ip" ]; then
  echo "have allowed_ip"
  tmp_api=$(echo $api_head_ip | awk -F":" '{print $2}' | awk -F"/" '{print $3}')
  echo "tmp_api is:"$tmp_api
  property+=" --mysql.host=$tmp_api"
  property+=" --mysql.port=3306"
else
  echo "don't have allowed_ip"
  echo "mysql is"$(svc_ip mysql)
  property+=" --mysql.host=$(svc_ip mysql)"
  property+=" --mysql.port=3306"
fi
test_run+=" -b '$property'"
echo "====================================="
echo "About to run the following command on test agent: $test_agent_ip"
echo "$test_run"
echo "====================================="
sleep 10

ssh -o StrictHostKeyChecking=no root@$test_agent_ip '[ -d /root/end2end ] || mkdir /root/end2end'
ssh -o StrictHostKeyChecking=no root@$test_agent_ip 'rm -fr /root/end2end/run/*'
echo "copy test script to test agent:$test_agent_ip"
scp ./.test.sh root@$test_agent_ip:$test_base/rune2e.sh
if [ "$iam_enabled" == "#true#" ]; then
  echo "copy test resources to test agent because IAM is enabled"
  scp -r $test_resource_root root@$test_agent_ip:$test_base/
fi
#\rm ./.test.sh
ssh -o StrictHostKeyChecking=no -tt root@$test_agent_ip $test_run
