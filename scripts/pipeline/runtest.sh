#!/bin/bash

JAR_VERSION=${1}

java -jar end-to-end-testing-1.0.0-b$JAR_VERSION.jar --rest.baseUrl=http://192.168.70.20:8085  --praxisServerConnection.dbadminPassword=testBeta1#123  --edition=standard --vdcNumber=13  --sns=true --licenseType=LI --planName=Small --dbEngineVersion=mssql_2012 --exitOnFail=false --authentication=true --diskSize=153600  --praxisServerConnection.praxisConnectUrl=iam.vchs.vmware.com --praxisServerConnection.dbadminUsername=sre3.dbaas.automation@gmail.com  --praxisServerConnection.endUserPasswd=Pass@123 --praxisServerConnection.iamUserName=cshan@vmware.com --praxisServerConnection.iamUserPasswd=870411Abc_ --praxisServerConnection.endUsername=fanzhang.jeffrey@gmail.com --testGroup=pipelinetest
