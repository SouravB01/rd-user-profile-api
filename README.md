# rd-user-profile-api

User Profile API

## Purpose

Provides user profile data to clients, implemented as a Java/SpringBoot application.

### Prerequisites

To run the project you will need to have the following installed:

* Java 8
* Docker

For information about the software versions used to build this API and a complete list of it's dependencies see build.gradle

### Running the application

To run the API quickly use the docker helper script as follows:

```
./bin/run-in-docker.sh install
```
or

```
docker-compose up
```


Alternatively, you can start the application from the current source files using Gradle as follows:

```
./gradlew clean bootRun
```

If required, to run with a low memory consumption, the following can be used:

```
./gradlew --no-daemon assemble && java -Xmx384m -jar build/libs/rd-user-profile-api.jar
```

### Using the application

To understand if the application is working, you can call it's health endpoint:

```
curl http://localhost:8091/health
```

If the API is running, you should see this response:

```
{"status":"UP"}
```

### DB Initialisation˙

The application uses a Postgres database which can be run through a docker container on its own if required.

this

The application should automatically apply any database migrations using flyway.

### Running integration tests:


You can run the *integration tests* as follows:

```
./gradlew integration
```

### Running functional tests:

If the API is running (either inside a Docker container or via `gradle bootRun`) you can run the *functional tests* as follows:

```
./gradlew functional
```

If you want to run a specific scenario use this command:

```
./gradlew functional --tests <TestClassName> --info -Dscenario=<Scenario>
```

### Running smoke tests:

If the API is running (either inside a Docker container or via `gradle bootRun`) you can run the *smoke tests* as follows:

```
./gradlew smoke
```

### Running mutation tests tests:

If you have some time to spare, you can run the *mutation tests* as follows:

```
./gradlew pitest
```

As the project grows, these tests will take longer and longer to execute but are useful indicators of the quality of the test suite.

More information about mutation testing can be found here:
http://pitest.org/

### Testing in Postman

To test in Postman the easiest way is to start this service using the ./bin/run-in-docker.sh script.  The in postman paste the following script:

```
pm.sendRequest('http://127.0.0.1:8089/token', function (err, res) {
    if (err) {
        console.log(err);
    } else {
        pm.environment.set("token", res.text());
    }
});
```
into the pre-script window.  Also add a header as follows:

```
ServiceAuthorization: Bearer {{token}}
```

Authorization :  Bearer copy IDAM access token

### Contract testing with pact

To generate the json inside target/pacts directory you need to run the tests first.
This file is not committed to the repo.

To publish against remote broker:
`./gradlew pactPublish`

Turn on VPN and verify on url `https://pact-broker.platform.hmcts.net/`
The pact contract(s) should be published


To publish against local broker:
Uncomment out the line found in the build.gradle:
`pactBrokerUrl = 'http://localhost:9292'`
comment out the real broker

Start the docker container from the root dir run
`docker-compose -f broker-compose.yml up`

Publish via the gradle command
`./gradlew pactPublish`

Once Verify on url `http://localhost:9292/`
The pact contract(s) should be published

Remember to return the localhost back to the remote broker

### 'No tasks available' when running Pact tests
`Step 1: Go to where u can edit configurations for the tests here..`
![pact1](readme-images/pact1.png?raw=true "Step 1")

`Step 2: Press the plus to add a new Junit test class..`
![pact1](readme-images/pact2.png?raw=true "Step 2")

`Step 3: Then setup the configuration like so, making sure the path to the test class is correct..`
![pact1](readme-images/pact3.png?raw=true "Step 3")
