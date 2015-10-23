#!/usr/bin/env bash

BASE_DIR=$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )
source $BASE_DIR/test_env.sh
SSH="sshpass -p password ssh -o StrictHostKeyChecking=no -o UserKnownHostsFile=/dev/null"
SCP="sshpass -p password scp -o StrictHostKeyChecking=no -o UserKnownHostsFile=/dev/null"
local_test_jar="../../end-to-end-testing/build/libs/*-b*.jar"
bash -x $BASE_DIR/build_app_yml.sh
echo "billing is "$BILLING
if [ -n "$TEST_AGENT" ]; then
    TEST_DIR=${TEST_DIR:-end2end}
    $SSH -tt root@$TEST_AGENT rm -rf $TEST_DIR
    $SSH -tt root@$TEST_AGENT mkdir $TEST_DIR
    $SCP run_test.sh root@$TEST_AGENT:$TEST_DIR/
    $SCP update_jar.sh root@$TEST_AGENT:$TEST_DIR/
    $SCP download_jar.sh root@$TEST_AGENT:$TEST_DIR/
    $SCP *.yml root@$TEST_AGENT:$TEST_DIR/
    $SSH -tt root@$TEST_AGENT  "cd $TEST_DIR; chmod 777 *"
    if [ -e $local_test_jar ]; then
        $SCP $local_test_jar root@$TEST_AGENT:$TEST_DIR/
    else
        $SSH -tt root@$TEST_AGENT  "cd $TEST_DIR; bash -x download_jar.sh"
    fi
    $SSH -tt root@$TEST_AGENT  "cd $TEST_DIR; bash -x update_jar.sh"
    if [ "$BILLING" = "true" ]; then
        $SSH -tt root@$TEST_AGENT  "cd $TEST_DIR; BILLING=true bash -x run_test.sh $@"
    else
        $SSH -tt root@$TEST_AGENT  "cd $TEST_DIR; BILLING=false bash -x run_test.sh $@"
    fi
else
    cp download_jar.sh update_jar.sh run_test.sh *.yml $TEST_DIR
    if [ -e $local_test_jar ]; then
        cp ../../end-to-end-testing/build/libs/*.jar $TEST_DIR
    else
        bash -x $TEST_DIR/download_jar.sh
    fi
    cd $TEST_DIR
    bash -x update_jar.sh
    if [ "$BILLING" = "true" ]; then
        BILLING=true bash -x run_test.sh $@
    else
        BILLING=false bash -x run_test.sh $@
    fi
fi
