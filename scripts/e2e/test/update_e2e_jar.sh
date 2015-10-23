which unzip >/dev/null 2>&1 || yum -y install unzip

wget -q "http://tempest-ci.eng.vmware.com/job/e2e-cloudtesting-master/lastSuccessfulBuild/artifact/*zip*/archive.zip" -O archive.zip
unzip archive.zip
rm -rf end-to-end-testing*.jar
mv archive/end-to-end-testing/build/libs/end-to-end-testing-*.jar .
ln -sf end-to-end-testing-*.jar end-to-end-testing.jar
rm -rf archive*