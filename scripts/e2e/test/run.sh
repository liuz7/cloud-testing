set -o | grep xtrace | grep on && XTRACE=on || XTRACE=off
export XTRACE

BASE_DIR=$( dirname "$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )")
source $BASE_DIR/common.sh
source $BASE_DIR/function.sh

TEST_JOB=${TEST_JOB:-${JOB_NAME:-job}}
TEST_DIR=${TEST_DIR:-/end2end/$TEST_JOB/test}
SCRIPT_DIR=${SCRIPT_DIR:-/end2end/$TEST_JOB/scripts}

function get_file() {
  (find . -maxdepth 1 -name "$1" >/dev/null 2>&1) && find . -maxdepth 1 -name "$1" | tail -n 1
}

function config_portforward() {
  [ -n "$CDS_SERVER" ] && {
    $SCP $BASE_DIR/test/portforward.sh root@$CDS_SERVER:/tmp/portforward.sh
    $SSH root@$CDS_SERVER "bash /tmp/portforward.sh"
  }
}

function end2end_test_remote() {
  check_env TEST_AGENT
  $SSH root@$TEST_AGENT "rm -rf $SCRIPT_DIR; mkdir -p $SCRIPT_DIR"
  $SCP -r $BASE_DIR/* root@$TEST_AGENT:$SCRIPT_DIR/
  $SCP application.yml root@$TEST_AGENT:$SCRIPT_DIR/
  E2E_JAR=$(get_file "end-to-end-testing-*.jar")
  [ -n "$E2E_JAR" ] && {
    $SCP "$E2E_JAR" root@$TEST_AGENT:$SCRIPT_DIR/
  }

  $SSH root@$TEST_AGENT "cd $SCRIPT_DIR; TEST_DIR=${TEST_DIR} bash $SCRIPT_DIR/test/run.sh test $@"
}

function end2end_test() {
  rm -rf $TEST_DIR
  mkdir -p $TEST_DIR

  [ -e "application.yml" ] && cp application.yml $TEST_DIR/.
  E2E_JAR=$(get_file "end-to-end-testing-*.jar")
  [ -n "$E2E_JAR" ] && {
    cp "$E2E_JAR" $TEST_DIR/.
    ln -sf "$TEST_DIR/$(basename "$E2E_JAR")" $TEST_DIR/end-to-end-testing.jar
  }

  pushd $TEST_DIR

  bash $BASE_DIR/install_java.sh || exit 1
  [ -e "end-to-end-testing.jar" ] || bash $BASE_DIR/test/update_e2e_jar.sh

  java -jar end-to-end-testing.jar $@

  popd
}

function collect_result() {
  check_env TEST_AGENT
  rm -rf run
  mkdir -p run
  $SCP -r root@$TEST_AGENT:$TEST_DIR/log run
  $SCP -r root@$TEST_AGENT:$TEST_DIR/test-output run
  #$SCP root@$TEST_AGENT:$TEST_DIR/deploymentInfo.properties run
  DEPLOYMENT_INFO_FILE=run/deploymentInfo.properties
  if [ -n "$CDS_SERVER" ] && [ -f "$CDS_CLI" ]; then
    echo "#deployment information for dbaas" >> $DEPLOYMENT_INFO_FILE
    export CDS_DEPLOYMENT=$(get_deploy_name dbaas)
    [ -n "CDS_DEPLOYMENT" ] && get_release_service_versions >> $DEPLOYMENT_INFO_FILE

    echo "#deployment information for dsp" >> $DEPLOYMENT_INFO_FILE
    export CDS_DEPLOYMENT=$(get_deploy_name dsp)
    [ -n "CDS_DEPLOYMENT" ] && get_release_service_versions >> $DEPLOYMENT_INFO_FILE
  fi
  return 0 # ensure the function always return 0
}

function prepare_nodes() {
  if [ -n "$CDS_SERVER" ] && [ -f "$CDS_CLI" ]; then
    NODES_IN_POOL=${1:-4}

    [ $MSSQL_VERSION = "mssql_2008R2" ] && NODE_NAME=node-2008r2 || NODE_NAME=node-2012
    [ $EDITION != "enterprise" ] && NODE_NAME=${NODE_NAME}-std
    [ $LICENSETYPE = "BYOL" ] && NODE_NAME=${NODE_NAME}-byol

    export CDS_DEPLOYMENT=$(get_deploy_name dbaas)
    CREATED_NODES=$($CDS_CLI vapps --json | jq ".[] .template | select(.name==\"${NODE_NAME}\") .name" | wc -l)
    $CDS_CLI tune pool.idle.max.${NODE_NAME}=
    let NODES=${NODES_IN_POOL}-${CREATED_NODES}
    while [ "${NODES}" -gt 0 ]; do
      $CDS_CLI vapps create ${NODE_NAME}
      let NODES=${NODES}-1
    done
  fi
  return 0 # ensure the function always return 0
}

case "$1" in
  install-cds )
    prepare_cds
    ;;
  config-portforward )
    config_portforward
    ;;
  generate-yaml )
    bash $BASE_DIR/test/prepare_app_yml.sh
    ;;
  test )
    shift
    end2end_test $@
    ;;
  test-remote )
    shift
    end2end_test_remote $@
    ;;
  collect-result )
    collect_result
    ;;
  prepare-nodes )
    shift
    prepare_nodes $@
    ;;
  * )
    echo "Usages:"
    echo "  install-cds"
    echo "  config-portforward"
    echo "  generate-yaml"
    echo "  test"
    echo "  test-remote"
    echo "  collect-result"
    echo "  prepare-nodes"
    ;;
esac