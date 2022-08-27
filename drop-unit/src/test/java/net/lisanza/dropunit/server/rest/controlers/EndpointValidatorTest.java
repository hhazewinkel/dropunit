package net.lisanza.dropunit.server.rest.controlers;

import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.core.MediaType;

import static org.junit.Assert.fail;

public class EndpointValidatorTest {

    private EndpointValidator subject;

    @Before
    public void setUp() throws Exception {
        subject = new EndpointValidator();
    }

    @Test
    public void shouldValidateEmpyContentType() {
        try {
            subject.validateRequestContentType((String)null, (String)null);
        } catch (Exception e) {
            fail();
        }
        try {
            subject.validateRequestContentType("", null);
        } catch (Exception e) {
            fail();
        }
        try {
            subject.validateRequestContentType(null, "");
        } catch (Exception e) {
            fail();
        }
        try {
            subject.validateRequestContentType("", "");
        } catch (Exception e) {
            fail();
        }
    }


    @Test
    public void shouldValidateContentType() {
        try {
            subject.validateRequestContentType(MediaType.APPLICATION_XML, null);
            fail();
        } catch (Exception e) {
        }
        try {
            subject.validateRequestContentType(null, MediaType.APPLICATION_XML);
            fail();
        } catch (Exception e) {
        }
        try {
            subject.validateRequestContentType(MediaType.APPLICATION_XML, MediaType.APPLICATION_XML);
        } catch (Exception e) {
            fail();
        }
    }
}
