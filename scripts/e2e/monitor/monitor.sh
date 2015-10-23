[ "$XTRACE" == "on" ] && set -x

BASE_DIR=$( dirname "$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )")

source ${BASE_DIR}/common.sh
source ${BASE_DIR}/function.sh
export CDS_DEPLOYMENT=$(get_deploy_name dbaas)
export CDS_URL="http://$CDS_SERVER/api"˙
while [ true ]
  if $(kill -s 0 $1); then
     if [ $? -ne 0 ]; then
        return 0
     fi
  else
     test_job=$(ps aux | grep "\-\-testGroup" | grep -v grep | awk '{print $2}')
     if [ -z "${test_job}" ]; then
         return 0
     fi
  fi
  do
    while read -r LINE
    do
       if [ ! -z $LINE ]; then
          echo "delete vapps $LINE"
          $CDS_CLI vapps delete $LINE --force
       fi
    done  < <(get_error_vapps_id | sed -e 's#"##g')
    sleep(60)
  done