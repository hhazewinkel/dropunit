# DROPUNIT

Drop unit is composed of DropWizard providing a simulator server and JUnit invoking tests.
Combined with Rocker containers it is to be used to perform integration-testing for services
that depend on other services. Dropunit provides a simulator for dependent services that can
be configured remotely or by configuration in order to react in a defined way on an incoming
request.
As such, Dropunit is there to support integration-testing with which it simulates dependent
services for a component (engine) under development that on its turn can be tested as a 
real-life environment. In order to realise even a more real-life environment Docker is used to
create the 'servers' with a network connecting the components. Docker is not mandatory to
use Dropunit, but is just an option. The following diagram depicts the usage and setup of
dropunit.

```
  +---------------+                                       +----------+
  | integration-  |                                       |          |
  | test          |                                       | dropunit |
  |               |                                       |          |
  | @Before       |                                       |          |   
  |  setup    ====|========== deliver drop unit =========>| register |<== configfile
  |               |                                       |          |
  |               |          +-------------+              |          |
  |               |          |engine under |              |          |
  |@Test          |          |development  |              |          |
  |  invoke       |          |             |              |          |
  |  request &   =|== req ==>| operation ==|== backend ==>| simulate |
  |  test         |          |             |   req        |          |
  |  response     |          |             |              |          |
  +---------------+          +-------------+              +----------+
```

Dropunit consistst of three components (from left to right):
1) integration-test are JUnit based tests that first configures the dropunit that
   is followed by the requests targetted to the engine-under-development,
2) engine-under-development the component that is to be tested,
3) dropunit which is the simulator for any dependent services for the 
   engine-under-development.

In this project it is implemented as multi-module project that serves part as the
development environment and example how you can use dropunit in your development.
This multi-module project is composed of the following modules: 
1) _docker provides, the setup with docker-compose;
2) _integration-test, where all the jUnit based integration tests are implemented;
3) drop-unit, the common module for client and server (simulator);
4) drop-unit-simulator, the base for the simlator;
5) engine-under-test, the rest proxy to be replaced with a real rest-service.

In short, if you use dropunit you would need the modules _docker (1), 
_integration-test (2), drop-unit-simulator (4), and engine-under-test (5).
The module dropunit will then be a dependency in your project.


# DEVELOPMENT ENVIRONMENT

This section explains how to use the project for its own development purposes. 

### Build the projects

	$ mvn -f pom.xml clean package
	
### Starting IT environment

    $ docker-compose -f _docker/application/docker-compose-it.yml up -d --build --renew-anon-volumes

Wait until all dockers and applications have started.

### Run the intetgrationtests

    $ mvn test -Dtest=*IT -DfailIfNoTests=false


### Stop integration-test environment

    $ docker-compose -f _docker/application/docker-compose-it.yml stop


Given that this project is also setup as a possible example your own development
you can use the same structure and logic. 


# SIMULATOR

Dropunit (the simulator) can act as a REST service that can respond as configured.
There are two ways how you can configure Dropunit: the first is 'remote configuration'
by means of HTTP operations and the second is 'default configuration' from a yaml file.
Both are more explained ibelow.


## The 'remote configuration' by HTTP operations

Endpoints are created in the unit-test and configure the endpoints and the responses
of the simulator. Below is such a unit-test presented and it consists of 4 steps:
1) setup dropunit endpoints
2) invoke message on engine-under-test to use dropunit endpoints
3) assert message from engine-under-test
4) assert dropunit endpoints

```
    @Test
    public void shouldTest() throws Exception {

        // setup dropunit endpoint
        ClientDropUnit dropUnit = new ClientDropUnit("http://server.tld")
                .cleanup()
                .withGet("test-get/with/path?and=variables")
                .withResponseOkFromFile(MediaType.APPLICATION_XML, "response-filename")
                .drop();

        // invoke message on engine-under-test to use dropunit endpoint
        HttpResponse response = httpClient.invokeHttpGet(dropUnit.getUrl());

        // assert message from engine-under-test
        assertEquals(200, response.getStatusLine().getStatusCode());
        String body = EntityUtils.toString(response.getEntity(), "UTF-8");
        assertNotNull(body);
        assertThat(body, containsString(readFromFile("response-filename")));

        // assert dropunit
        dropUnit.assertCountRecievedRequests(1);
        dropUnit.assertReceived(1);
        dropUnit.assertNotFound(0);
    }
```

#### setup dropunit endpoints

For the API details is referred to the java doc in the ClientDropUnit class, but the
general usage is explained by the comments in the following code. For examples you can
invesitgate also the unit/integration tests in the _integration-test module.

```
    //
    // Initiate a dropunit for a specific simulator server url.
    //
    new ClientDropUnit("http://server.tld")
    
    // Cleanup all remote configured endpoints. This is advised in the first 
    // created ClientDropUnit and when called in the second ClientDropUnit it
    // will cleanup the endpoint of the first.
    .cleanup()
    
    // Configure the remote endpoint with the required HTTP-method and the URL
    .withGet("test-get/with/path?and=variables")
    
    // Define headers that must be matched in order to determine the correct
    // dropunit endpoint.
    .withHeader("Connection", "keep-alive")
    
    // respond with a define delay
    // This can be used to define a server-side timeout on the simulator.
    .withResponseDelay(20000)

    // respond with an HTTP response code of 200 (OK) and with the provided 
    // body composed of the content-type and the contents of a file.
    .withResponseOkFromFile(MediaType.APPLICATION_XML, "response-filename")
    
    // Convey the dropunit endpoint to the remote simulator.
    .drop();
```
                
#### invoke message on engine-under-test to use dropunit endpoints

Implement here the operations you want to test on the engine-under-test.

#### assert message from engine-under-test

Assert here the responses from the engine-under-test.

#### assert dropunit endpoints

All operations and for each dropunit endpoint that are invoked on the engine-under-test
you can assert for usage.

```
   // Assert the total expected amount of reqeuested received for all dropunit 
   // endpoints combined.
   dropUnit.assertCountRecievedRequests(1);
   
   // Assert the  expected amount of reqeuested received
   dropUnit.assertReceived(1);
   
   // Ensure that there are no requests made that we did not know
   // (configured either remote or as defaults)
   dropUnit.assertNotFound(0);
```


## The 'default configuration' by file

Default endpoints can be created at boottime from a configuration file. As such these
endpoints are directly available for the engine-under-test without the help of
a unit/integration-test to (remotely) define the response of the simulator.
 
The endpoints for the simulator are read from an configuration file which is the same
config-file as used by DropWizard and is extended with the following yaml.

```
endpoints:
  - path: "/path/of/some/endpoint"
    method: "GET"
    delay: 150
    responseCode: 200
    responseContentType: "application/xml"
    responseBodyFileName: "src/main/resources/endpoints/response-body-one.xml"
  - path: "/path/of/some/endpoint"
    method: "POST"
    delay: 150
    responseCode: 200
    responseContentType: "application/xml"
    responseBodyFileName: "src/main/resources/endpoints/response-body-one.xml"
```

The yaml is composed of an array of 'endpoints' where each endpoint is composed
of the following elements:
- path: "/path/of/some/endpoint",
    which is the URL portion for this dropunit (endpoint);
- method: "GET", 
    which is the HTTP method for this dropunit (endpoint);
- delay: 150, 
    which is the delay in milliseconds before the response is returned;
- responseCode: 200, 
    which is the HTTP response code for this dropunit (endpoint);
- responseContentType: "application/xml", 
    which is the content-type of the response code for this dropunit (endpoint);
- responseBodyFileName: "src/main/resources/endpoints/response-body-one.xml",
    which is the file from which the response body is made for this dropunit (endpoint);


note: These endpoints are persistent when a remote client invoke the 'clear all drops'
operation.


/* Copyright 2023 Harrie Hazewinkel. All rights reserved. */