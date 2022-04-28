package net.lisanza.dropunit.integrationtest.defaultEndpoints;

import net.lisanza.dropunit.client.ClientDropUnit;
import net.lisanza.dropunit.integrationtest.BaseRequest;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.junit.Test;

import javax.ws.rs.core.MediaType;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

public class NotFoundTestIT extends BaseRequest {

    @Test
    public void shouldFailForDifferentUrl() throws Exception {
        // setup dropunit endpoint
        ClientDropUnit dropUnit = new ClientDropUnit(DROP_UNIT_HOST).cleanup();

        // invoke message on engine-under-test to use dropunit endpoint
        HttpResponse response = httpClient.invokeHttpGet("/different-url-test/path/not-found-1");

        // assert message from engine-under-test
        assertEquals(404, response.getStatusLine().getStatusCode());
        String body = EntityUtils.toString(response.getEntity(), "UTF-8");
        assertNotNull(body);
        assertThat(body, containsString("response: 404 Not Found >> exception "));
        assertThat(body, containsString("different-url-test/path/not-found-1"));
        assertThat(body, containsString("missing registration"));

        dropUnit.assertNotFound(1);

        // invoke message on engine-under-test to use dropunit endpoint
        response = httpClient.invokeHttpGet("different-url-test/path/not-found-2");

        // assert message from engine-under-test
        assertEquals(404, response.getStatusLine().getStatusCode());

        dropUnit.assertNotFound(2);
    }

    @Test
    public void shouldFailForDifferentMethod() throws Exception {
        // setup dropunit endpoint
        ClientDropUnit dropUnit = new ClientDropUnit(DROP_UNIT_HOST).cleanup();

        // invoke message on engine-under-test to use dropunit endpoint
        HttpResponse response = httpClient.invokeHttpPost("different-method-test/path",
                MediaType.APPLICATION_XML, "<xyz></xyz>");

        // assert message from engine-under-test
        assertEquals(404, response.getStatusLine().getStatusCode());
        String body = EntityUtils.toString(response.getEntity(), "UTF-8");
        assertNotNull(body);
        assertThat(body, containsString("missing registration: /different-method-test/path"));

        dropUnit.assertNotFound(1);

        // invoke message on engine-under-test to use dropunit endpoint
        response = httpClient.invokeHttpDelete("different-method-test/path");

        // assert message from engine-under-test
        assertEquals(404, response.getStatusLine().getStatusCode());

        dropUnit.assertNotFound(2);
    }
}