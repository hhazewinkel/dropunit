package net.lisanza.dropunit.integrationtest.md5;

import net.lisanza.dropunit.client.ClientDropUnit;
import net.lisanza.dropunit.integrationtest.BaseRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.util.EntityUtils;
import org.junit.Test;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.SocketTimeoutException;

import static net.lisanza.dropunit.server.utils.FileUtils.readFromFile;
import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class DeleteTestIT extends BaseRequest {

    @Test
    public void shouldTestWithPath() throws Exception {

        // setup dropunit endpoint
        ClientDropUnit dropUnit = new ClientDropUnit(DROP_UNIT_HOST).cleanup()
                .withDelete("test-delete")
                .withResponseOkFromFile(MediaType.APPLICATION_XML, RESPONSE_FILE)
                .drop();

        // invoke message on engine-under-test to use dropunit endpoint
        HttpResponse response = httpClient.invokeHttpDelete(dropUnit.getUrl());

        // assert message from engine-under-test
        assertEquals(200, response.getStatusLine().getStatusCode());
        String body = EntityUtils.toString(response.getEntity(), "UTF-8");
        assertNotNull(body);
        assertThat(body, containsString(readFromFile(RESPONSE_FILE)));

        dropUnit.assertCountRecievedRequests(1);
        dropUnit.assertReceived(1);
        dropUnit.assertNotFound(0);
    }

    @Test
    public void shouldTestWithQueryString() throws Exception {

        // setup dropunit endpoint
        ClientDropUnit dropUnit = new ClientDropUnit(DROP_UNIT_HOST).cleanup()
                .withDelete("test-delete")
                .withResponseOkFromFile(MediaType.APPLICATION_XML, RESPONSE_FILE)
                .drop();

        // invoke message on engine-under-test to use dropunit endpoint
        HttpResponse response = httpClient.invokeHttpDelete(dropUnit.getUrl());

        // assert message from engine-under-test
        assertEquals(200, response.getStatusLine().getStatusCode());
        String body = EntityUtils.toString(response.getEntity(), "UTF-8");
        assertNotNull(body);
        assertThat(body, containsString(readFromFile(RESPONSE_FILE)));

        dropUnit.assertCountRecievedRequests(1);
        dropUnit.assertReceived(1);
        dropUnit.assertNotFound(0);
    }

    @Test
    public void shouldTestWithException() throws Exception {

        // setup dropunit endpoint
        ClientDropUnit dropUnit = new ClientDropUnit(DROP_UNIT_HOST).cleanup()
                .withDelete("test-delete-exception")
                .withResponseBadRequest(MediaType.APPLICATION_XML, "")
                .drop();

        // invoke message on engine-under-test to use dropunit endpoint
        HttpResponse response = httpClient.invokeHttpDelete(dropUnit.getUrl());

        // assert message from engine-under-test
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatusLine().getStatusCode());

        dropUnit.assertCountRecievedRequests(1);
        dropUnit.assertReceived(1);
        dropUnit.assertNotFound(0);
    }

    @Test
    public void shouldTestWithConnectionTimeout() throws Exception {

        // setup dropunit endpoint
        ClientDropUnit dropUnit = new ClientDropUnit(DROP_UNIT_HOST).cleanup()
                .withDelete("test-delete")
                .withResponseBadGateway(MediaType.APPLICATION_XML, RESPONSE_FILE)
                .withResponseDelay(20000)
                .drop();

        // invoke message on engine-under-test to use dropunit endpoint
        // and assert message from engine-under-test
        try {
            httpClient.invokeHttpDelete(dropUnit.getUrl(),
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
        dropUnit.assertReceived(1);
        dropUnit.assertNotFound(0);
    }

    @Test
    public void shouldTestWithHeaders() throws Exception {
        // setup dropunit endpoint
        ClientDropUnit dropUnit = new ClientDropUnit(DROP_UNIT_HOST).cleanup()
                .withDelete("test-delete/with/path")
                .withHeader("Connection", "keep-alive")
                .withResponseOkFromFile(MediaType.APPLICATION_XML, RESPONSE_FILE)
                .drop();

        // invoke message on engine-under-test to use dropunit endpoint
        HttpResponse response = httpClient.invokeHttpDelete(dropUnit.getUrl());

        // assert message from engine-under-test
        assertEquals(200, response.getStatusLine().getStatusCode());
        String body = EntityUtils.toString(response.getEntity(), "UTF-8");
        assertNotNull(body);
        assertThat(body, containsString(readFromFile(RESPONSE_FILE)));

        dropUnit.assertCountRecievedRequests(1);
        dropUnit.assertReceived(1);
        dropUnit.assertNotFound(0);
    }

    @Test
    public void shouldFailWithHeaders() throws Exception {
        // setup dropunit endpoint
        ClientDropUnit dropUnit = new ClientDropUnit(DROP_UNIT_HOST).cleanup()
                .withDelete("test-delete/with/path")
                .withHeader("Authorization", "<api-key>")
                .withHeader("Connection", "keep-alive")
                .withResponseOkFromFile(MediaType.APPLICATION_XML, RESPONSE_FILE)
                .drop();

        // invoke message on engine-under-test to use dropunit endpoint
        HttpResponse response = httpClient.invokeHttpDelete(dropUnit.getUrl());

        // assert message from engine-under-test
        assertEquals(404, response.getStatusLine().getStatusCode());

        dropUnit.assertCountRecievedRequests(0);
        dropUnit.assertNotFound(1);
    }
}