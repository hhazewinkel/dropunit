package net.lisanza.dropunit.server.rest.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class DropUnitParametersDtoTest {

    ObjectMapper mapper;

    @Before
    public void setUp() throws Exception {
        mapper = new ObjectMapper();
    }

    @Test
    public void shouldGenerateJson() throws Exception {
        DropUnitParametersDto parameters = new DropUnitParametersDto()
                .withMatchAll(true)
                .withParameter("key-1", "value-1")
                .withParameter("key-2", "value-2")
                .withParameter("key-3", "value-3");

        String json = mapper.writeValueAsString(parameters);
        assertNotNull(json);
        assertEquals("{\"matchAll\":true,\"matchValue\":false,\"included\":{\"key-1\":\"value-1\",\"key-2\":\"value-2\",\"key-3\":\"value-3\"},\"includedParameters\":{\"key-1\":\"value-1\",\"key-2\":\"value-2\",\"key-3\":\"value-3\"}}", json);
    }

    @Test
    public void shouldParseUriParameters() throws Exception {
        DropUnitParametersDto parameters = new DropUnitParametersDto()
                .withMatchAll(true)
                .withParameterString("key-1=value-1&key-2=value-2&key-3=value-3");

        assertNotNull(parameters);
        Map<String, String> list = parameters.getIncludedParameters();
        assertNotNull(list);
        assertEquals(3, list.keySet().size());
        assertNotNull(list.get("key-1"));
        assertEquals("value-1", list.get("key-1"));
        assertNotNull(list.get("key-2"));
        assertEquals("value-2", list.get("key-2"));
        assertNotNull(list.get("key-3"));
        assertEquals("value-3", list.get("key-3"));

        String json = mapper.writeValueAsString(parameters);
        assertNotNull(json);
        assertEquals("{\"matchAll\":true,\"matchValue\":false,\"included\":{\"key-1\":\"value-1\",\"key-2\":\"value-2\",\"key-3\":\"value-3\"},\"includedParameters\":{\"key-1\":\"value-1\",\"key-2\":\"value-2\",\"key-3\":\"value-3\"}}", json);
    }
}