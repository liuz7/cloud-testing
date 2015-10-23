#!/usr/bin/env bash

BASE_DIR=$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )
source $BASE_DIR/test_env.sh
SSH="sshpass -p password ssh -o StrictHostKeyChecking=no -o UserKnownHostsFile=/dev/null"
SCP="sshpass -p password scp -o StrictHostKeyChecking=no -o UserKnownHostsFile=/dev/null"

# Gather the test artifacts
sudo rm -rf run
mkdir -p run
$SCP -r root@$TEST_AGENT:end2end/log run
$SCP -r root@$TEST_AGENT:end2end/test-output run
$SCP root@$TEST_AGENT:end2end/deploymentInfo.properties run

sudo zip -r  $JOB_NAME_$BUILD_NUMBER run
