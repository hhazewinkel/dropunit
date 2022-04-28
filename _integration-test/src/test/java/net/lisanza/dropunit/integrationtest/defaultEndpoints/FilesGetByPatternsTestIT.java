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

public class FilesGetByPatternsTestIT extends BaseRequest {

    @Test
    public void shouldTestDefaultConfigurationEndpointWithPatternAbc() throws Exception {
        // setup dropunit endpoint
        ClientDropUnit dropUnit = new ClientDropUnit(DROP_UNIT_HOST).cleanup();

        // invoke message on engine-under-test to use dropunit endpoint
        HttpResponse response = httpClient.invokeHttpPost("default/path/five",
                MediaType.APPLICATION_XML, "<abc></abc>");

        // assert message from engine-under-test
        assertEquals(200, response.getStatusLine().getStatusCode());
        String body = EntityUtils.toString(response.getEntity(), "UTF-8");
        assertNotNull(body);
        assertThat(body, containsString("<?xml version=\"1.0\" encoding=\"UTF-8\"?>"));
        assertThat(body, containsString("<pallet>"));
        assertThat(body, containsString("<bag>droppy one</bag>"));
        assertThat(body, containsString("</pallet>"));

        new ClientDropUnit(DROP_UNIT_HOST).assertNotFound(0);
    }

    @Test
    public void shouldTestDefaultConfigurationEndpointWithPatternXyz() throws Exception {
        // setup dropunit endpoint
        ClientDropUnit dropUnit = new ClientDropUnit(DROP_UNIT_HOST).cleanup();

        // invoke message on engine-under-test to use dropunit endpoint
        HttpResponse response = httpClient.invokeHttpPost("default/path/five",
                MediaType.APPLICATION_XML, "<xyz></xyz>");

        // assert message from engine-under-test
        assertEquals(200, response.getStatusLine().getStatusCode());
        String body = EntityUtils.toString(response.getEntity(), "UTF-8");
        assertNotNull(body);
        assertThat(body, containsString("<?xml version=\"1.0\" encoding=\"UTF-8\"?>"));
        assertThat(body, containsString("<pallet>"));
        assertThat(body, containsString("<bag>droppy two</bag>"));
        assertThat(body, containsString("</pallet>"));

        dropUnit.assertNotFound(0);
    }

    @Test
    public void shouldFailDefaultConfigurationEndpointWithPatterns() throws Exception {
        // setup dropunit endpoint
        ClientDropUnit dropUnit = new ClientDropUnit(DROP_UNIT_HOST).cleanup();

        // invoke message on engine-under-test to use dropunit endpoint
        HttpResponse response = httpClient.invokeHttpPost("/default/path/five",
                MediaType.APPLICATION_XML, "<should></fail>");

        // assert message from engine-under-test
        assertEquals(404, response.getStatusLine().getStatusCode());

        dropUnit.assertNotFound(1);
    }
}