BASE_DIR=$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )
source $BASE_DIR/function.sh
init
if [ ! $CDS_SERVER = $TEST_AGENT ]; then
    config_forward
fi
