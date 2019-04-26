package net.lisanza.dropunit.integrationtest.defaultEndpoints;

import net.lisanza.dropunit.client.ClientDropUnit;
import net.lisanza.dropunit.integrationtest.BaseRequest;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

public class FilesGetTestIT extends BaseRequest {

    @Test
    public void shouldTestDefaultConfigurationEndpointOne() throws Exception {
        // setup dropunit endpoint
        ClientDropUnit dropUnit = new ClientDropUnit(DROP_UNIT_HOST)
                .withGet("default/path/one");

        // invoke message on engine-under-test to use dropunit endpoint
        HttpResponse response = httpClient.invokeHttpGet("default/path/one");

        // assert message from engine-under-test
        assertEquals(200, response.getStatusLine().getStatusCode());
        String body = EntityUtils.toString(response.getEntity(), "UTF-8");
        assertNotNull(body);
        assertThat(body, containsString("<?xml version=\"1.0\" encoding=\"UTF-8\"?>"));
        assertThat(body, containsString("<pallet>"));
        assertThat(body, containsString("<bag>droppy one</bag>"));
        assertThat(body, containsString("</pallet>"));

        dropUnit.assertNotFound(0);
    }

    @Test
    public void shouldTestDefaultConfigurationEndpointOneWithParameters() throws Exception {
        // setup dropunit endpoint
        ClientDropUnit dropUnit = new ClientDropUnit(DROP_UNIT_HOST)
                .withGet("default/path/one?parameter=value");

        // invoke message on engine-under-test to use dropunit endpoint
        HttpResponse response = httpClient.invokeHttpGet("default/path/one?parameter=value");

        // assert message from engine-under-test
        assertEquals(200, response.getStatusLine().getStatusCode());
        String body = EntityUtils.toString(response.getEntity(), "UTF-8");
        assertNotNull(body);
        assertThat(body, containsString("<?xml version=\"1.0\" encoding=\"UTF-8\"?>"));
        assertThat(body, containsString("<pallet>"));
        assertThat(body, containsString("<bag>droppy one</bag>"));
        assertThat(body, containsString("</pallet>"));

        dropUnit.assertNotFound(0);
    }

    @Test
    public void shouldTestDefaultConfigurationEndpointTwo() throws Exception {
        // setup dropunit endpoint
        ClientDropUnit dropUnit = new ClientDropUnit(DROP_UNIT_HOST)
                .withGet("default/path/two");

        // invoke message on engine-under-test to use dropunit endpoint
        HttpResponse response = httpClient.invokeHttpGet("default/path/two");

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
    public void shouldTestDefaultConfigurationEndpointThree() throws Exception {
        // setup dropunit endpoint
        ClientDropUnit dropUnit = new ClientDropUnit(DROP_UNIT_HOST)
                .withGet("default/path/three");

        // invoke message on engine-under-test to use dropunit endpoint
        HttpResponse response = httpClient.invokeHttpGet("default/path/three");

        // assert message from engine-under-test
        assertEquals(200, response.getStatusLine().getStatusCode());
        String body = EntityUtils.toString(response.getEntity(), "UTF-8");
        assertNotNull(body);
        assertThat(body, containsString("\"pallet\""));
        assertThat(body, containsString("\"bag\""));
        assertThat(body, containsString("\"droppy three\""));

        dropUnit.assertNotFound(0);
    }

    @Test
    public void shouldTestDefaultConfigurationEndpointFour() throws Exception {
        // setup dropunit endpoint
        ClientDropUnit dropUnit = new ClientDropUnit(DROP_UNIT_HOST)
                .withGet("default/path/four");

        // invoke message on engine-under-test to use dropunit endpoint
        HttpResponse response = httpClient.invokeHttpGet("default/path/four");

        // assert message from engine-under-test
        assertEquals(404, response.getStatusLine().getStatusCode());

        dropUnit.assertNotFound(0);
    }
}