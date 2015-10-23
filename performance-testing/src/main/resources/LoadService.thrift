namespace java com.vmware.vchs.load.generator.service

struct TestResult {
    1: double success,
    2: double failure,
    3: map<string,list<double>> results,
    4: list<string> requests,
}

service LoadService {
    oneway void setupTest(1:string tests, 2:i32 user, 3:i32 interval, 4:i32 duration)
    oneway void startTest()
    oneway void stopTest()
    TestResult getResult()
}
