function check_env() {
  if [[ -z "${!1}" ]]; then
    echo "Error: $1 is not configured."
    [[ -n "$2" ]] && echo $2
    exit 1
  fi
}

function get_deploy_name() {
  $CDS_CLI deployment --json | jq '.[]|select(.state=="OK")|"\(.release .name)=\(.name)"' | grep "$1" | sed -e "s#$1=##" -e "s#null=##" -e 's#"##g'
}

function svc_ip() #mgmt path ip for svc
{
  id=${2:-0}
  $CDS_CLI svc --json | jq "map(select(.name==\"$1\") | .interfaces[] | select(.network==\"mgmt\") | .ip ) | .[$id]" | sed 's#"##g'
}

function prepare_cds() {
[ -d $WORK_DIR ] || mkdir -p $WORK_DIR

UNAME=$(uname)
if [ "$UNAME" == "Linux" ]; then
    which yum >/dev/null || sudo apt-get -y install yum
    which jq >/dev/null || yum -y install jq
    which zip >/dev/null || yum -y install zip
    which wget > /dev/null || yum -y install wget
    which sshpass > /dev/null || yum -y install sshpass
    wget -q  http://tempest-ci.eng.vmware.com/job/CDS_Build/lastSuccessfulBuild/artifact/out/cds-cli/linux_amd64/cds  -O $WORK_DIR/cds
fi

if [ "$UNAME" == "Darwin" ]; then
    which jq >/dev/null || brew install -y jq
    which wget >/dev/null || brew install -y wget
    wget -q  http://tempest-ci.eng.vmware.com/job/CDS_Build/lastSuccessfulBuild/artifact/out/cds-cli/darwin_amd64/cds -O $WORK_DIR/cds
fi

chmod +x $WORK_DIR/cds
}

function get_release_variable(){
  $CDS_CLI releases current --json | jq ".state.variables|.[\"$1\"]" | sed 's#"##g'
}

function get_release_version(){
  $CDS_CLI releases current --json | jq ".version" | sed 's#"##g'
}

function get_release_service_versions(){
  $CDS_CLI release current --json | jq '"\(.name)=\(.version)", (.manifest .services[] | "\(.name)=\(.version)")' |sed 's#"##g'
}

function get_error_vapps_id(){
  $CDS_CLI vapps --json | jq '.[]|select(.state == "CRITICAL" or .state == "ERROR" and .target.name == "mssql")|.id'
}