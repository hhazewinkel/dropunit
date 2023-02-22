package net.lisanza.dropunit.integrationtest.patterns;

import net.lisanza.dropunit.client.ClientDropUnit;
import net.lisanza.dropunit.integrationtest.BaseRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.util.EntityUtils;
import org.junit.Test;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.File;
import java.net.SocketTimeoutException;

import static net.lisanza.dropunit.server.utils.FileUtils.readFromFile;
import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class PostTestIT extends BaseRequest {

    @Test
    public void shouldTestWithPath() throws Exception {

        // setup dropunit endpoint
        ClientDropUnit dropUnit = new ClientDropUnit(DROP_UNIT_HOST).cleanup()
                .withPost("test-post/with/path")
                .withRequestPattern(MediaType.APPLICATION_XML, "<bag>droppy</bag>")
                .withResponseOkFromFile(MediaType.APPLICATION_XML, RESPONSE_FILE)
                .drop();

        // invoke message on engine-under-test to use dropunit endpoint
        HttpResponse response = httpClient.invokeHttpPost(dropUnit.getUrl(), MediaType.APPLICATION_XML, new File(REQUEST_FILE));

        // assert message from engine-under-test
        assertEquals(200, response.getStatusLine().getStatusCode());
        String body = EntityUtils.toString(response.getEntity(), "UTF-8");
        assertNotNull(body);
        assertThat(body, containsString(readFromFile(RESPONSE_FILE)));

        dropUnit.assertCountRecievedRequests(1);
        dropUnit.assertNotFound(0);
    }

    @Test
    public void shouldTestWithQueryString() throws Exception {

        // setup dropunit endpoint
        ClientDropUnit dropUnit = new ClientDropUnit(DROP_UNIT_HOST).cleanup()
                .withPost("test-post/with/path?and=variables")
                .withRequestPattern(MediaType.APPLICATION_XML, "<bag>droppy</bag>")
                .withResponseOkFromFile(MediaType.APPLICATION_XML, RESPONSE_FILE)
                .drop();

        // invoke message on engine-under-test to use dropunit endpoint
        HttpResponse response = httpClient.invokeHttpPost(dropUnit.getUrl(), MediaType.APPLICATION_XML, new File(REQUEST_FILE));

        // assert message from engine-under-test
        assertEquals(200, response.getStatusLine().getStatusCode());
        String body = EntityUtils.toString(response.getEntity(), "UTF-8");
        assertNotNull(body);
        assertThat(body, containsString(readFromFile(RESPONSE_FILE)));

        dropUnit.assertCountRecievedRequests(1);
        dropUnit.assertNotFound(0);
    }

    @Test
    public void shouldTestWithException() throws Exception {

        // setup dropunit endpoint
        ClientDropUnit dropUnit = new ClientDropUnit(DROP_UNIT_HOST).cleanup()
                .withPost("test-post-exception")
                .withRequestPattern(MediaType.APPLICATION_XML, "<bag>droppy</bag>")
                .withResponseBadRequest(MediaType.APPLICATION_XML, "")
                .drop();

        // invoke message on engine-under-test to use dropunit endpoint
        HttpResponse response = httpClient.invokeHttpPost(dropUnit.getUrl(), MediaType.APPLICATION_XML, new File(REQUEST_FILE));

        // assert message from engine-under-test
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatusLine().getStatusCode());

        dropUnit.assertCountRecievedRequests(1);
        dropUnit.assertNotFound(0);
    }

    @Test
    public void shouldTestWithConnectionTimeout() throws Exception {

        // setup dropunit endpoint
        ClientDropUnit dropUnit = new ClientDropUnit(DROP_UNIT_HOST).cleanup()
                .withPost("test-post")
                .withRequestPattern(MediaType.APPLICATION_XML, "<bag>droppy</bag>")
                .withResponseOkFromFile(MediaType.APPLICATION_XML, RESPONSE_FILE)
                .withResponseDelay(20000)
                .drop();

        // invoke message on engine-under-test to use dropunit endpoint
        // and assert message from engine-under-test
        try {
            httpClient.invokeHttpPost(dropUnit.getUrl(),
                    MediaType.APPLICATION_XML, new File(REQUEST_FILE),
                    RequestConfig.custom()
                            .setConnectionRequestTimeout(1000)
                            .setConnectTimeout(1000)
                            .setSocketTimeout(1000)
                            .build());
            fail("timeout not exceeded");
        } catch (SocketTimeoutException e) {
            assertTrue(true);
        }

        dropUnit.assertCountRecievedRequests(1);
        dropUnit.assertNotFound(0);
    }

    @Test
    public void shouldTestWithHeaders() throws Exception {
        // setup dropunit endpoint
        ClientDropUnit dropUnit = new ClientDropUnit(DROP_UNIT_HOST).cleanup()
                .withPost("test-post/with/path")
                .withHeader("Connection", "keep-alive")
                .withRequestPattern(MediaType.APPLICATION_XML, "<bag>droppy</bag>")
                .withResponseOkFromFile(MediaType.APPLICATION_XML, RESPONSE_FILE)
                .drop();

        // invoke message on engine-under-test to use dropunit endpoint
        HttpResponse response = httpClient.invokeHttpPost(dropUnit.getUrl(),
                MediaType.APPLICATION_XML, new File(REQUEST_FILE));

        // assert message from engine-under-test
        assertEquals(200, response.getStatusLine().getStatusCode());
        String body = EntityUtils.toString(response.getEntity(), "UTF-8");
        assertNotNull(body);
        assertThat(body, containsString(readFromFile(RESPONSE_FILE)));

        dropUnit.assertCountRecievedRequests(1);
        dropUnit.assertReceivedFromFile(1, REQUEST_FILE);
        dropUnit.assertNotFound(0);
    }

    @Test
    public void shouldFailWithHeaders() throws Exception {
        // setup dropunit endpoint
        ClientDropUnit dropUnit = new ClientDropUnit(DROP_UNIT_HOST).cleanup()
                .withPost("test-post/with/path")
                .withHeader("Authorization", "<api-key>")
                .withHeader("Connection", "keep-alive")
                .withRequestPattern(MediaType.APPLICATION_XML, "<bag>droppy</bag>")
                .withResponseOkFromFile(MediaType.APPLICATION_XML, RESPONSE_FILE)
                .drop();

        // invoke message on engine-under-test to use dropunit endpoint
        HttpResponse response = httpClient.invokeHttpPost(dropUnit.getUrl(),
                MediaType.APPLICATION_XML, new File(REQUEST_FILE));

        // assert message from engine-under-test
        assertEquals(404, response.getStatusLine().getStatusCode());

        dropUnit.assertCountRecievedRequests(0);
        dropUnit.assertNotFound(1);
    }

    @Test
    public void shouldTestMultipleWithSamePath() throws Exception {
        String data1 = "<bag>droppy</bag>";
        String data2 = "<bag>candy</bag>";
        String requestBody1 = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                "<request>" + data1 + "</request>";
        String responseBody1 = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                "<response>" + data1 + "</response>";
        String requestBody2 = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                "<request>" + data2 + "</request>";
        String responseBody2 = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                "<response>" + data2 + "</response>";

        // setup dropunit endpoint
        ClientDropUnit dropUnit1 = new ClientDropUnit(DROP_UNIT_HOST).cleanup()
                .withPost("/test-post/with/path")
                .withRequestPattern(MediaType.APPLICATION_XML, data1)
                .withResponseOk(MediaType.APPLICATION_XML, responseBody1)
                .drop();

        // setup dropunit endpoint
        ClientDropUnit dropUnit2 = new ClientDropUnit(DROP_UNIT_HOST)
                .withPost("/test-post/with/path")
                .withRequestPattern(MediaType.APPLICATION_XML, data2)
                .withResponseOk(MediaType.APPLICATION_XML, responseBody2)
                .drop();

        // invoke message on engine-under-test to use dropunit endpoint
        HttpResponse response1 = httpClient.invokeHttpPost(dropUnit1.getUrl(),
                MediaType.APPLICATION_XML, requestBody1);

        // assert message from engine-under-test
        assertEquals(200, response1.getStatusLine().getStatusCode());
        String body1 = EntityUtils.toString(response1.getEntity(), "UTF-8");
        assertNotNull(body1);
        assertThat(body1, containsString(responseBody1));

        // invoke message on engine-under-test to use dropunit endpoint
        HttpResponse response2 = httpClient.invokeHttpPost(dropUnit2.getUrl(),
                MediaType.APPLICATION_XML, requestBody2);

        // assert message from engine-under-test
        assertEquals(200, response2.getStatusLine().getStatusCode());
        String body2 = EntityUtils.toString(response2.getEntity(), "UTF-8");
        assertNotNull(body2);
        assertThat(body2, containsString(responseBody2));

        dropUnit1.assertCountRecievedRequests(1);
        dropUnit1.assertNotFound(0);

        dropUnit2.assertCountRecievedRequests(1);
        dropUnit2.assertNotFound(0);
    }

}