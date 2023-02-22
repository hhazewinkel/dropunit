package net.lisanza.dropunit.integrationtest.errors;

import net.lisanza.dropunit.client.ClientDropUnit;
import net.lisanza.dropunit.integrationtest.BaseRequest;
import org.apache.http.HttpResponse;
import org.junit.Test;

import javax.ws.rs.core.MediaType;
import java.io.File;

import static org.junit.Assert.assertEquals;

public class PostTestIT extends BaseRequest {

    @Test
    public void shouldTestWithNotFound() throws Exception {
        // setup dropunit endpoint
        ClientDropUnit dropUnit = new ClientDropUnit(DROP_UNIT_HOST).cleanup()
                .withPost("test-get/with/path")
                .withRequestBodyFromFile(MediaType.APPLICATION_XML, REQUEST_FILE)
                .withResponseOkFromFile(MediaType.APPLICATION_XML, RESPONSE_FILE)
                .drop();

        // invoke message on engine-under-test to use dropunit endpoint
        HttpResponse response = httpClient.invokeHttpPost("test-get/with/path/that/is/not/found",
                MediaType.APPLICATION_XML, new File(REQUEST_FILE));

        // assert message from engine-under-test
        assertEquals(404, response.getStatusLine().getStatusCode());
        dropUnit.assertCountRecievedRequests(0);
        dropUnit.assertNotFound(1);
    }
}