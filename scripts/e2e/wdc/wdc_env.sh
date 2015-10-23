[ "$XTRACE" == "on" ] && set -x

BASE_DIR=$( dirname "$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )")

check_env WDC
check_env VDC_NUMBER

if [ -f "${BASE_DIR}/wdc/${WDC}.sh" ]; then
  source "${BASE_DIR}/wdc/${WDC}.sh"
else
  echo "Error: Unexpected WDC value $WDC. Expected values are wdc1, wdc3."
  exit 1
fi

export STORAGE=${STORAGE:-vnx}
case "$STORAGE" in
  sf )
    export STORAGE_NAME=$STORAGE_SF_NAME
    export STORAGE_CATALOG_NAME=$STORAGE_SF_CATALOG_NAME
    ;;
  vnx)
    export STORAGE_NAME=$STORAGE_VNX_NAME
    export STORAGE_CATALOG_NAME=$STORAGE_VNX_CATALOG_NAME
    ;;
esac

if [ -z "$STORAGE_NAME" ]; then
  echo "STORAGE $STORAGE is not supported in $WDC dev_$VDC_NUMBER."
  exit 1
fi
