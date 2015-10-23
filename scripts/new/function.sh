
BASE_DIR=$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )
source $BASE_DIR/test_env.sh

if [ -z "$CDS_SERVER" ]; then
  echo "CDS_SERVER is not configured."
  exit 1
fi



export SSH="sshpass -p password ssh -o StrictHostKeyChecking=no -o UserKnownHostsFile=/dev/null"
export SCP="sshpass -p password scp -o StrictHostKeyChecking=no -o UserKnownHostsFile=/dev/null"

export CDS_URL=http://${CDS_SERVER}/api
export WORK_DIR=${WORK_DIR:-.}
export CDS_DIR=${CDS_DIR:-$WORK_DIR}
export APP_YML=${APP_YML:-$WORK_DIR/application.yml}



function init() {
  if [ -f /tmp/.modifiedTime ]; then
     todate=$(date "+%Y-%m-%d" -d "-1 days")
     lastModified=$(sudo cat /tmp/.modifiedTime)
     if [[ $lastModified > $todate ]]; then
         echo 'do not need to get cds';
         return 0
     fi
  fi
  mkdir -p $CDS_DIR
  if [ "$(uname)" == "Linux" ]
  then
      which yum >/dev/null || sudo apt-get -y install yum
      which jq >/dev/null || yum -y install jq
      which zip >/dev/null || yum -y install zip
      which wget > /dev/null || yum -y install wget
      which sshpass > /dev/null || yum -y install sshpass
      wget -q  http://tempest-ci.eng.vmware.com/view/5-CDS/job/CDS_Build/lastSuccessfulBuild/artifact/out/cds-cli/linux_amd64/cds  -O $CDS_DIR/cds
  fi

  if [ "$(uname)" == "Darwin" ]
  then
      which jq >/dev/null || brew install -y jq
      which wget >/dev/null || brew install -y wget
      wget -q  http://tempest-ci.eng.vmware.com/view/5-CDS/job/CDS_Build/lastSuccessfulBuild/artifact/out/cds-cli/darwin_amd64/cds -O $CDS_DIR/cds
  fi

  chmod a+x $CDS_DIR/cds

  export CDS_CLI=$CDS_DIR/cds
  export CDS_DEPLOYMENT=$($CDS_CLI deployment --json  | jq '.[] | select(.state=="OK") | .name' | sed 's#"##g'|grep dbaas)
  echo $todate > /tmp/.modifiedTime
}

function svc_ip(){
 name=${1:-etcd}
 id=${2:-0}
 ip=$($CDS_CLI svc --json | jq "map(select(.name==\"$name\"))|.[$id].interfaces[].ip")
 echo ${ip//\"/}
}

function findValueFromCds(){
  $CDS_CLI releases current | grep -q "$1"
}

function download_jar() {
  pushd ${WORK_DIR}
  rm -rf archive*
  wget http://tempest-ci.eng.vmware.com/view/6-SRE/job/e2e-cloudtesting-stable/lastSuccessfulBuild/artifact/*zip*/archive.zip -O archive.zip
  unzip archive.zip
  rm -f end-to-end-testing*.jar
  mv archive/end-to-end-testing/build/libs/end-to-end-testing-*.jar ${WORK_DIR}/
  ln -sf end-to-end-testing-*.jar end-to-end-testing.jar
  rm -rf archive*
  popd
}

function config_sns() {
  if ( findValueFromCds "sns-stubbed: true" ); then
    SNS=false
  else
    SNS=true
  fi
}

function config_forward() {
    # Configure ip forwarding to internal services
    MYSQL_HOST=$(svc_ip mysql)
    ETCD_HOST=$(svc_ip etcd)
    GNATS_HOST=$(svc_ip gnatsd)
    echo "forward $CDS_SERVER:13306 to $MYSQL_HOST:3306"
    echo "forward $CDS_SERVER:14001 to $ETCD_HOST:4001"
    echo "forward $CDS_SERVER:14222 to $GNATS_HOST:4222"
    cat << EOF > ./.portforward.sh
    sudo iptables -F
    sudo iptables -t nat -F
    sudo iptables -t nat -A PREROUTING -p tcp --dport 13306 -j DNAT --to $MYSQL_HOST:3306
    sudo iptables -t nat -A POSTROUTING -d $MYSQL_HOST -j MASQUERADE
    sudo iptables -t nat -A PREROUTING -p tcp --dport 14001 -j DNAT --to $ETCD_HOST:4001
    sudo iptables -t nat -A POSTROUTING -d $ETCD_HOST -j MASQUERADE
    sudo iptables -t nat -A PREROUTING -p tcp --dport 14222 -j DNAT --to $GNATS_HOST:4222
    sudo iptables -t nat -A POSTROUTING -d $GNATS_HOST -j MASQUERADE
EOF

    $SCP ./.portforward.sh root@$CDS_SERVER:/tmp/portforward.sh
    $SSH -tt root@$CDS_SERVER bash /tmp/portforward.sh
}

function config_iam() {
# check whether iam is enabled on the target deployment
iam_enabled=$($CDS_CLI releases --json | jq '.[]|select(.current)|.state.variables|.["iam-enabled"]'|tr '"' '#')
# if iam is enabled,then we need to get praxis access credential and update iam configuraiton file per this deployment
if [ "$iam_enabled" == "#true#" ];then
    AUTHENTICATION=true
    echo "IAM is enabled"
    praxis_url_raw=$($CDS_CLI releases --json | jq '.[]|select(.current)|.state.variables|.["praxis-login-host"]')
    PRAXIS_CONNECT_URL="${praxis_url_raw//\"/}"
    case "$praxis_url_raw" in
      #Praxis 0
      *d1p500tlm*)
        DBADMIN_USERNAME=stu@praxis0.com
        DBADMIN_PASSWORD=Welcome@123
        ENDUSER_USERNAME=
        ENDUSER_PASSWORD=
      ;;
      # Praxis 6
      *d1p360tlm*)
        DBADMIN_USERNAME=stu@aas-dbaas.com
        DBADMIN_PASSWORD=Pass@123
        IAMUSER_USERNAME=stu@aas-dbaas.com
        IAMUSER_PASSWORD=Pass@123
        ENDUSER_USERNAME=leu@aas-dbaas.com
        ENDUSER_PASSWORD=Pass@123
      ;;
      #Praxis INT
      *d4p4*)
        DBADMIN_USERNAME=admin1@dbaas-int.com
        DBADMIN_PASSWORD=Pass@123
        ENDUSER_USERNAME=admin2@dbaas-int.com
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
    esac
    echo "PRAXIS url: $PRAXIS_CONNECT_URL"
    echo "PRAXIS user name: $DBADMIN_USERNAME"
    echo "PRAXIS password: $DBADMIN_PASSWORD"
    echo "PRAXIS enduser name: $ENDUSER_USERNAME"
    echo "PRAXIS endpassword: $ENDUSER_PASSWORD"
    echo "PRAXIS iam user name: $IAMUSER_PASSWORD"
    echo "PRAXIS iam password: $IAMUSER_PASSWORD"
    echo "render $auth_file_path with provided praxis information"
    [ -z "$PRAXIS_CONNECT_URL" ] && {
     echo "ERROR: praxis url is not provided when IAM is enabled!"
     exit 1
    }
    [ -z "$DBADMIN_USERNAME" ] && {
     echo "ERROR: praxis user name is not provided when IAM is enabled!"
     exit 1
    }
    [ -z "$DBADMIN_PASSWORD" ] && {
     echo "ERROR: praxis password is not provided when IAM is enabled!"
     exit 1
    }
    [ -z "$ENDUSER_USERNAME" ] && {
     echo "ERROR: praxis End user name (end user for iamtest) is not provided when IAM is enabled!"
     exit 1
    }
    [ -z "$ENDUSER_PASSWORD" ] && {
      echo "ERROR: praxis End user password (end user for iamtest) is not provided when IAM is enabled!"
      exit 1
    }
    [ -z "$IAMUSER_PASSWORD" ] && {
     echo "ERROR: praxis iam user name (admin user for iamtest) is not provided when IAM is enabled!"
     exit 1
    }
    [ -z "$IAMUSER_PASSWORD" ] && {
      echo "ERROR: praxis iam user password (admin user for iamtest) is not provided when IAM is enabled!"
      exit 1
    }
else
    AUTHENTICATION=false
fi
}
