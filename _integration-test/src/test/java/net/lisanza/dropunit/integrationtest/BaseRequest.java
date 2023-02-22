package net.lisanza.dropunit.integrationtest;

import net.lisanza.dropunit.client.BaseHttpClient;

public class BaseRequest {

    protected static final String ENDPOINT_HOST = "http://127.0.0.1:8080/";
    protected static final String DROP_UNIT_HOST = "http://127.0.0.1:9080/";

    protected static final String REQUEST_FILE = "src/test/resources/xml/drop-request.xml";
    protected static final String RESPONSE_FILE = "src/test/resources/xml/drop-response.xml";

    protected static final BaseHttpClient httpClient = new BaseHttpClient(ENDPOINT_HOST);

}