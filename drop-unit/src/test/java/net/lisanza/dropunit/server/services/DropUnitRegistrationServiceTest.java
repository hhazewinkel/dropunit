package net.lisanza.dropunit.server.services;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class DropUnitRegistrationServiceTest {

    EndpointRegistrationService subject;

    @Before
    public void setUp() throws Exception {
        subject = new EndpointRegistrationService();
    }

    @Test
    public void shouldRegisterStaticAndDropAll() {
        subject.registerStatic(new DropUnitEndpoint()
                .withUrl("/foo")
                .withResponse(new DropUnitEndpointResponse()
                        .withCode(200)
                        .withBody("")));
        assertNotNull(subject.getStaticRegistrations());
        assertEquals(1, subject.getStaticRegistrations().size());
        assertEquals(0, subject.getDynamicRegistrations().size());
        assertNotNull(subject.dropAll());
        assertEquals(1, subject.getStaticRegistrations().size());
        assertEquals(0, subject.getDynamicRegistrations().size());
    }

    @Test
    public void shouldRegisterDynamicAndDropAll() {
        subject.registerDynamic(new DropUnitEndpoint()
                .withUrl("/foo")
                .withResponse(new DropUnitEndpointResponse()
                        .withCode(200)
                        .withBody("")));
        assertNotNull(subject.getStaticRegistrations());
        assertEquals(0, subject.getStaticRegistrations().size());
        assertEquals(1, subject.getDynamicRegistrations().size());
        assertNotNull(subject.dropAll());
        assertEquals(0, subject.getStaticRegistrations().size());
        assertEquals(0, subject.getDynamicRegistrations().size());
    }

    @Test
    public void shouldRegisterStaticAndDeregister() {
        String id = subject.registerStatic(new DropUnitEndpoint()
                .withUrl("/foo")
                .withResponse(new DropUnitEndpointResponse()
                        .withCode(200)
                        .withBody("")));
        assertNotNull(subject.getStaticRegistrations());
        assertEquals(1, subject.getStaticRegistrations().size());
        assertEquals(0, subject.getDynamicRegistrations().size());
        assertNull(subject.deregister(id));
        assertEquals(1, subject.getStaticRegistrations().size());
        assertEquals(0, subject.getDynamicRegistrations().size());
    }

    @Test
    public void shouldRegisterDynamicAndDeregister() {
        String id = subject.registerDynamic(new DropUnitEndpoint()
                .withUrl("/foo")
                .withResponse(new DropUnitEndpointResponse()
                        .withCode(200)
                        .withBody("")));
        assertNotNull(subject.getStaticRegistrations());
        assertEquals(0, subject.getStaticRegistrations().size());
        assertEquals(1, subject.getDynamicRegistrations().size());
        assertNotNull(subject.deregister(id));
        assertEquals(0, subject.getStaticRegistrations().size());
        assertEquals(0, subject.getDynamicRegistrations().size());
    }
}