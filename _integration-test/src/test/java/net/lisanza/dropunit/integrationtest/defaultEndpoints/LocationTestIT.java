package net.lisanza.dropunit.integrationtest.defaultEndpoints;

import net.lisanza.dropunit.client.ClientDropUnit;
import net.lisanza.dropunit.integrationtest.BaseRequest;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.junit.Test;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static javax.ws.rs.core.HttpHeaders.LOCATION;
import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

public class LocationTestIT extends BaseRequest {

    @Test
    public void shouldRedirect() throws Exception {
        // setup dropunit endpoint
        ClientDropUnit dropUnit = new ClientDropUnit(DROP_UNIT_HOST).cleanup()
                .withPost("/dynamic/path/redirect/remote-setup")
                .withResponse(Response.Status.FOUND, MediaType.APPLICATION_XML, "<xyz></xyz>")
                .withResponseHeader("Location", "https://this.domain.local/returned")
                .drop();

        // invoke message on engine-under-test to use dropunit endpoint
        HttpResponse response = httpClient.invokeHttpPost("/dynamic/path/redirect/remote-setup",
                MediaType.APPLICATION_XML, "");

        // assert message from engine-under-test
        assertEquals(302, response.getStatusLine().getStatusCode());
        assertNotNull(response.getFirstHeader(LOCATION));
        assertEquals("https://this.domain.local/returned", response.getFirstHeader(LOCATION).getValue());
        String body = EntityUtils.toString(response.getEntity(), "UTF-8");
        assertNotNull(body);
        assertThat(body, containsString(""));

        dropUnit.assertNotFound(0);
    }


    @Test
    public void shouldRedirectByServerConfig() throws Exception {
        // setup dropunit endpoint
        ClientDropUnit dropUnit = new ClientDropUnit(DROP_UNIT_HOST).cleanup()
                .withPost("/default/path/redirect")
                .drop();

        // invoke message on engine-under-test to use dropunit endpoint
        HttpResponse response = httpClient.invokeHttpPost("/default/path/redirect",
                MediaType.APPLICATION_XML, "<xyz></xyz>");

        // assert message from engine-under-test
        assertEquals(302, response.getStatusLine().getStatusCode());
        assertNotNull(response.getFirstHeader(LOCATION));
        assertEquals("https://this.domain.local/returned", response.getFirstHeader(LOCATION).getValue());
        String body = EntityUtils.toString(response.getEntity(), "UTF-8");
        assertNotNull(body);
        assertThat(body, containsString(""));

        dropUnit.assertNotFound(0);
    }
}