[ "$XTRACE" == "on" ] && set -x

BASE_DIR=$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )

export WORK_DIR=${WORK_DIR:-.work}

if [ "$(uname)" == "Darwin" ]; then
export SSH="ssh -o StrictHostKeyChecking=no -o UserKnownHostsFile=/dev/null"
export SCP="scp -o StrictHostKeyChecking=no -o UserKnownHostsFile=/dev/null"
else
export SSH="sshpass -p password ssh -o StrictHostKeyChecking=no -o UserKnownHostsFile=/dev/null"
export SCP="sshpass -p password scp -o StrictHostKeyChecking=no -o UserKnownHostsFile=/dev/null"
fi

export CDS_URL="http://$CDS_SERVER/api"
export CDS_CLI=$WORK_DIR/cds
export YAMLER="${BASE_DIR}/bin/yamler"