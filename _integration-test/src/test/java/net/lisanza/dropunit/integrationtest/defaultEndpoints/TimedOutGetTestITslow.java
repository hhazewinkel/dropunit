package net.lisanza.dropunit.integrationtest.defaultEndpoints;

import net.lisanza.dropunit.client.ClientDropUnit;
import net.lisanza.dropunit.integrationtest.BaseRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.junit.Test;

import java.net.SocketTimeoutException;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class TimedOutGetTestITslow extends BaseRequest {

    @Test
    public void shouldTestDefaultConfigurationEndpointSix() throws Exception {
        // setup dropunit endpoint
        ClientDropUnit dropUnit = new ClientDropUnit(DROP_UNIT_HOST).cleanup();

        // invoke message on engine-under-test to use dropunit endpoint
        try {
            HttpResponse response = httpClient.invokeHttpGet("default/path/six",
                    RequestConfig.custom()
                            .setConnectionRequestTimeout(10000)
                            .setConnectTimeout(10000)
                            .setSocketTimeout(10000)
                            .build());
            fail("timeout not exceeded");
        } catch (SocketTimeoutException e) {
            assertTrue(true);
        }

        dropUnit.assertNotFound(0);
    }

}