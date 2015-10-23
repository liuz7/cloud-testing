[ "$XTRACE" == "on" ] && set -x

BASE_DIR=$( dirname "$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )")

source ${BASE_DIR}/common.sh
source ${BASE_DIR}/function.sh

check_env CONFIG_YML "config-qe1.yml or config-qe2.yml for Dallas env configuration."

ARTIFACTS_URL=${ARTIFACTS_URL:-http://tempest-ci.eng.vmware.com/job/devtest_integration_env/job/dbaas-bugfix-validation/lastSuccessfulBuild/artifact/artifacts/}

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

echo "dbaas release is:"
cat $WORK_DIR/dbaas.release.yml

mv $WORK_DIR/dbaas.config.yml $WORK_DIR/dbaas.config.yml.old
$CDS_CLI config $WORK_DIR/dbaas.config.yml.old+${BASE_DIR}/deploy/${CONFIG_YML} >$WORK_DIR/dbaas.config.yml

echo "dbaas config is:"
cat $WORK_DIR/dbaas.config.yml
