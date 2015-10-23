[ "$XTRACE" == "on" ] && set -x

BASE_DIR=$( dirname "$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )")

source ${BASE_DIR}/common.sh
source ${BASE_DIR}/function.sh
source ${BASE_DIR}/wdc/wdc_env.sh

check_env CDS_SERVER

DSP_ARTIFACTS_URL=${DSP_ARTIFACTS_URL:-http://tempest-ci.eng.vmware.com/job/dsp-release/lastSuccessfulBuild/artifact/manifests/releases}

if [ -z $DSP_RELEASE_DIR ]; then
    # download from jenkins job
    pushd $WORK_DIR
    wget -q $DSP_ARTIFACTS_URL/dsp.config.yml -O dsp.config.yml
    wget -q $DSP_ARTIFACTS_URL/dsp.release.yml -O dsp.release.yml
    popd
else
    # copy from $DSP_RELEASE_DIR
    rm -rf $WORK_DIR/dsp.*.yml
    cp $DSP_RELEASE_DIR/dsp.*.yml $WORK_DIR/.
fi

# release yml
echo "dsp release is:"
cat $WORK_DIR/dsp.release.yml


# config yml
$YAMLER set $WORK_DIR/dsp.config.yml vcd_template/vcloud-host $VCLOUD_HOST
$YAMLER set $WORK_DIR/dsp.config.yml vcd_template/username dev_$VDC_NUMBER
$YAMLER set $WORK_DIR/dsp.config.yml vcd_template/vdc-name dbaas_dev_$VDC_NUMBER
$YAMLER set $WORK_DIR/dsp.config.yml network_template/properties/network pri_net_$VDC_NUMBER

$YAMLER set $WORK_DIR/dsp.config.yml vcd_template/catalog $STORAGE_CATALOG_NAME
$YAMLER set $WORK_DIR/dsp.config.yml storage_template/properties/storage-profile $STORAGE_NAME

echo "dsp config is:"
cat $WORK_DIR/dsp.config.yml
