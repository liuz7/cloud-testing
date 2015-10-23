#The testing framework for cloud testing.

##To get the project code
```
git clone ssh://<userid>@tempest-reviews.eng.vmware.com:29418/cloud-testing
```
##User manual
###For integration and end-to-end testing
1. Dependencies
    - JDK8, Groovy2+, Gradle2+.
    
2. How to open project. 
    - Use the Intellj IDEA/Eclipse to open the gradle project by selecting the `cloud-testing/build.gradle`.
    
3. How to run tests.
    - In Intellj IDEA, open `gradle tool` panel, run the `test` or `build` task in specific project.
    - Or in terminal, run `gradle test` in specific project directory (The gradle runtime is installed).
    
4. Where to add test cases.
    - In `src/test/java` directory, the TestNg test cases can bed added. Those can be ran by 
      IDE test runner.
 
5. How to get the code coverage result.
    - Run `jacocoTestReport` task after `test` to get the code coverage report.
          
5. Where to open the test report.
    - Open `build/reports/tests/html/index.html` to open the reportNg test report.
    
6. Where to open code coverage report.
    - Open `build/jacocoHtml/index.html` to open the Jacoco code coverage report.
    
7. How to run sanity test.
    1. Provision the local dev env. (Refer to https://github.com/vchs/services_dev_env)
    2. Run `git clone https://github.com/vchs/cloud-testing` to local machine.
    3. Run `cd cloud-testing`
    4. (Optional), Update the rest.baseUrl to actual env in `cloud-testing/end-to-end-testing/src/test/resources/config/application.yml`. If the ip and port in local dev env is not changed, ignore this step.
    5. Run `gradle end-to-end-testing:sanitytest`
    6. Collect result in console or in `end-to-end-testing/build/reports/` directory.  
    
8. How to execute the test case concurrently with gradle.
    1. Run `git clone https://github.com/vchs/cloud-testing` to local machine.
    2. Run `cd cloud-testing`
    3. Configure the `concurrent` settings in `cloud-testing/end-to-end-testing/src/test/resources/config/application.yml`。
    4. Or run single test with gradle and invocation count as`gradle fulltest --tests *testGetDBInstanceDuringProvision -DinvocationCount=100`.

9. How ro run test in executable jar.
    1. Run single test method:` java -jar end-to-end-testing-1.0.jar --testClass=UnprovisionInstanceTest --testMethod=testUnProvisionDBByList --rest.baseUrl=localhost:8080`.
    2. Run test group: `java -jar end-to-end-testing-1.0.jar --testGroup=fulltest --rest.baseUrl=localhost:8080`.
    3. Run test class: ` java -jar end-to-end-testing-1.0.jar --testClass=UnprovisionInstanceTest --rest.baseUrl=localhost:8080`.
    4. Run with base url and node size parameters: ` java -jar end-to-end-testing-1.0.jar --testClass=UnprovisionInstanceTest --rest.baseUrl=localhost:8080 -=nodeSize=2`.
    5. Run with different plan and db version: `java -jar end-to-end-testing-1.0.jar --testGroup=fulltest --rest.baseUrl=localhost:8080 -=planName=Large --dbEngineVersion=mssql_2008R2`.    
    
  
