#!/usr/bin/env bash

which unzip >/dev/null 2>&1 || yum -y install unzip

rm -rf archive*
wget http://tempest-ci.eng.vmware.com/view/6-SRE/job/e2e-cloudtesting-stable/lastSuccessfulBuild/artifact/*zip*/archive.zip
unzip archive.zip
rm -rf end-to-end-testing*.jar
mv archive/end-to-end-testing/build/libs/end-to-end-testing-*.jar .
rm -rf archive*