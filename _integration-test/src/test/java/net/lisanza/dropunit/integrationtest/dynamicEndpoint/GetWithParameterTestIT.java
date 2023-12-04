package net.lisanza.dropunit.integrationtest.dynamicEndpoint;

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

public class GetWithParameterTestIT extends BaseRequest {

    @Test
    public void shouldTestStaticConfigurationTestOne() throws Exception {
        // setup dropunit endpoint
        ClientDropUnit dropUnit = new ClientDropUnit(DROP_UNIT_HOST).cleanup()
                .withGet("dynamic/path/and/parameters/dynamic")
                .withResponseOkFromFile(MediaType.APPLICATION_XML, RESPONSE_FILE)
                .drop();

        // invoke message on engine-under-test to use dropunit endpoint
        HttpResponse response = httpClient.invokeHttpGet("dynamic/path/and/parameters/dynamic");

        // assert message from engine-under-test
        assertEquals(200, response.getStatusLine().getStatusCode());
        String body = EntityUtils.toString(response.getEntity(), "UTF-8");
        assertNotNull(body);
        assertThat(body, containsString("<?xml version=\"1.0\" encoding=\"UTF-8\"?>"));
        assertThat(body, containsString("<pallet>"));
        assertThat(body, containsString("<bag>droppy</bag>"));
        assertThat(body, containsString("</pallet>"));

        dropUnit.assertNotFound(0);
    }

    @Test
    public void shouldTestStaticConfigurationTestTwo() throws Exception {
        // setup dropunit endpoint
        ClientDropUnit dropUnit = new ClientDropUnit(DROP_UNIT_HOST).cleanup()
                .withGet("dynamic/path/and/parameters/dynamic")
                .withQueryParameterMatch(true, true)
                .withQueryParameter("key", "value")
                .withResponseOkFromFile(MediaType.APPLICATION_XML, RESPONSE_FILE)
                .drop();

        // invoke message on engine-under-test to use dropunit endpoint
        HttpResponse response = httpClient.invokeHttpGet("dynamic/path/and/parameters/dynamic?key=value");

        // assert message from engine-under-test
        assertEquals(200, response.getStatusLine().getStatusCode());
        String body = EntityUtils.toString(response.getEntity(), "UTF-8");
        assertNotNull(body);
        assertThat(body, containsString("<?xml version=\"1.0\" encoding=\"UTF-8\"?>"));
        assertThat(body, containsString("<pallet>"));
        assertThat(body, containsString("<bag>droppy</bag>"));
        assertThat(body, containsString("</pallet>"));

        dropUnit.assertNotFound(0);
    }

    @Test
    public void shouldTestStaticConfigurationTestThree() throws Exception {
        // setup dropunit endpoint
        ClientDropUnit dropUnit = new ClientDropUnit(DROP_UNIT_HOST).cleanup()
                .withGet("dynamic/path/and/parameters/dynamic")
                .withQueryParameterMatch(false, false)
                .withQueryParameter("key", "value")
                .withResponseOkFromFile(MediaType.APPLICATION_XML, RESPONSE_FILE)
                .drop();

        // invoke message on engine-under-test to use dropunit endpoint
        HttpResponse response = httpClient.invokeHttpGet("dynamic/path/and/parameters/dynamic?foo=bar");

        // assert message from engine-under-test
        assertEquals(200, response.getStatusLine().getStatusCode());
        String body = EntityUtils.toString(response.getEntity(), "UTF-8");
        assertNotNull(body);
        assertThat(body, containsString("<?xml version=\"1.0\" encoding=\"UTF-8\"?>"));
        assertThat(body, containsString("<pallet>"));
        assertThat(body, containsString("<bag>droppy</bag>"));
        assertThat(body, containsString("</pallet>"));

        dropUnit.assertNotFound(0);
    }

    @Test
    public void shouldTestStaticConfigurationTestExclude() throws Exception {
        // setup dropunit endpoint
        ClientDropUnit dropUnit = new ClientDropUnit(DROP_UNIT_HOST).cleanup()
                .withGet("dynamic/path/and/parameters/dynamic")
                .withoutQueryParameter("key", "value")
                .withResponseOkFromFile(MediaType.APPLICATION_XML, RESPONSE_FILE)
                .drop();

        // invoke message on engine-under-test to use dropunit endpoint
        HttpResponse response = httpClient.invokeHttpGet("dynamic/path/and/parameters/dynamic?key=bar");

        // assert message from engine-under-test
        assertEquals(404, response.getStatusLine().getStatusCode());
        String body = EntityUtils.toString(response.getEntity(), "UTF-8");
        assertNotNull(body);
        assertThat(body, containsString("response: 404 Not Found >> exception"));
        assertThat(body, containsString(" \"missing registration: /dynamic/path/and/parameters/dynamic\""));

        dropUnit.assertNotFound(1);
    }
}