package net.lisanza.dropunit.server.services;

import org.junit.Test;

import static net.lisanza.dropunit.server.services.EndPointParameters.EndPointParameterComparison.MATCH_ALL_FAILED_SIZES_ARE_NOT_EQUAL;
import static net.lisanza.dropunit.server.services.EndPointParameters.EndPointParameterComparison.MATCH_FAILED_EXPECTED_MORE_THEN_RECEIVED;
import static net.lisanza.dropunit.server.services.EndPointParameters.EndPointParameterComparison.MATCH_FAILED_ON_VALUE;
import static net.lisanza.dropunit.server.services.EndPointParameters.EndPointParameterComparison.MATCH_FAILED_PARAMETER_WAS_EXCLUDED;
import static net.lisanza.dropunit.server.services.EndPointParameters.EndPointParameterComparison.MATCH_OK;
import static org.junit.Assert.assertEquals;

public class EndPointParametersTest {

    @Test
    public void testBothEmpty() {
        String receivedQueryParameters = "";
        EndPointParameters endPointParameters = new EndPointParameters();

        endPointParameters.withMatchAll(true).withMatchValue(false);
        assertEquals(MATCH_OK, endPointParameters.doesMatch(receivedQueryParameters));
        endPointParameters.withMatchAll(true).withMatchValue(true);
        assertEquals(MATCH_OK, endPointParameters.doesMatch(receivedQueryParameters));
        endPointParameters.withMatchAll(false).withMatchValue(false);
        assertEquals(MATCH_OK, endPointParameters.doesMatch(receivedQueryParameters));
        endPointParameters.withMatchAll(false).withMatchValue(true);
    }

    @Test
    public void testReceivedEmpty() {
        String receivedQueryParameters = "";
        EndPointParameters endPointParameters = new EndPointParameters();
        endPointParameters.withParameter("key", "value");
        endPointParameters.withoutParameter("not_allowed", "value_of_not_allowed");

        endPointParameters.withMatchAll(true).withMatchValue(false);
        assertEquals(MATCH_ALL_FAILED_SIZES_ARE_NOT_EQUAL, endPointParameters.doesMatch(receivedQueryParameters));
        endPointParameters.withMatchAll(true).withMatchValue(true);
        assertEquals(MATCH_ALL_FAILED_SIZES_ARE_NOT_EQUAL, endPointParameters.doesMatch(receivedQueryParameters));
        endPointParameters.withMatchAll(false).withMatchValue(false);
        assertEquals(MATCH_FAILED_EXPECTED_MORE_THEN_RECEIVED, endPointParameters.doesMatch(receivedQueryParameters));
        endPointParameters.withMatchAll(false).withMatchValue(true);
        assertEquals(MATCH_FAILED_EXPECTED_MORE_THEN_RECEIVED, endPointParameters.doesMatch(receivedQueryParameters));
    }

    @Test
    public void testExpectedEmpty() {
        String receivedQueryParameters = "key=value";
        EndPointParameters endPointParameters = new EndPointParameters();

        endPointParameters.withMatchAll(true).withMatchValue(false);
        assertEquals(MATCH_ALL_FAILED_SIZES_ARE_NOT_EQUAL, endPointParameters.doesMatch(receivedQueryParameters));
        endPointParameters.withMatchAll(true).withMatchValue(true);
        assertEquals(MATCH_ALL_FAILED_SIZES_ARE_NOT_EQUAL, endPointParameters.doesMatch(receivedQueryParameters));
        endPointParameters.withMatchAll(false).withMatchValue(false);
        assertEquals(MATCH_OK, endPointParameters.doesMatch(receivedQueryParameters));
        endPointParameters.withMatchAll(false).withMatchValue(true);
        assertEquals(MATCH_OK, endPointParameters.doesMatch(receivedQueryParameters));
    }

    @Test
    public void testWithExcludesReceivedEmpty() {
        String receivedQueryParameters = "";
        EndPointParameters endPointParameters = new EndPointParameters();
        endPointParameters.withoutParameter("not_allowed", "value_of_not_allowed");

        endPointParameters.withMatchAll(true).withMatchValue(false);
        assertEquals(MATCH_OK, endPointParameters.doesMatch(receivedQueryParameters));
        endPointParameters.withMatchAll(true).withMatchValue(true);
        assertEquals(MATCH_OK, endPointParameters.doesMatch(receivedQueryParameters));
        endPointParameters.withMatchAll(false).withMatchValue(false);
        assertEquals(MATCH_OK, endPointParameters.doesMatch(receivedQueryParameters));
        endPointParameters.withMatchAll(false).withMatchValue(true);
        assertEquals(MATCH_OK, endPointParameters.doesMatch(receivedQueryParameters));
    }

    @Test
    public void testWithExcludesExpectedEmpty() {
        String receivedQueryParameters = "key=value";
        EndPointParameters endPointParameters = new EndPointParameters();
        endPointParameters.withoutParameter("key", "value");

        endPointParameters.withMatchAll(true).withMatchValue(false);
        assertEquals(MATCH_ALL_FAILED_SIZES_ARE_NOT_EQUAL, endPointParameters.doesMatch(receivedQueryParameters));
        endPointParameters.withMatchAll(true).withMatchValue(true);
        assertEquals(MATCH_ALL_FAILED_SIZES_ARE_NOT_EQUAL, endPointParameters.doesMatch(receivedQueryParameters));
        endPointParameters.withMatchAll(false).withMatchValue(false);
        assertEquals(MATCH_FAILED_PARAMETER_WAS_EXCLUDED, endPointParameters.doesMatch(receivedQueryParameters));
        endPointParameters.withMatchAll(false).withMatchValue(true);
        assertEquals(MATCH_FAILED_PARAMETER_WAS_EXCLUDED, endPointParameters.doesMatch(receivedQueryParameters));
    }

    @Test
    public void testExpectedAndReceivedOne() {
        String receivedQueryParameters = "key=value";
        EndPointParameters endPointParameters = new EndPointParameters();
        endPointParameters.withParameter("key", "value");

        endPointParameters.withMatchAll(true).withMatchValue(false);
        assertEquals(MATCH_OK, endPointParameters.doesMatch(receivedQueryParameters));
        endPointParameters.withMatchAll(true).withMatchValue(true);
        assertEquals(MATCH_OK, endPointParameters.doesMatch(receivedQueryParameters));
        endPointParameters.withMatchAll(false).withMatchValue(false);
        assertEquals(MATCH_OK, endPointParameters.doesMatch(receivedQueryParameters));
        endPointParameters.withMatchAll(false).withMatchValue(true);
        assertEquals(MATCH_OK, endPointParameters.doesMatch(receivedQueryParameters));
    }

    @Test
    public void testExpectedOneAndReceivedTwo() {
        String receivedQueryParameters = "key1=value1&key2=value2";
        EndPointParameters endPointParameters = new EndPointParameters();
        endPointParameters.withParameter("key1", "value1");

        endPointParameters.withMatchAll(true).withMatchValue(false);
        assertEquals(MATCH_ALL_FAILED_SIZES_ARE_NOT_EQUAL, endPointParameters.doesMatch(receivedQueryParameters));
        endPointParameters.withMatchAll(true).withMatchValue(true);
        assertEquals(MATCH_ALL_FAILED_SIZES_ARE_NOT_EQUAL, endPointParameters.doesMatch(receivedQueryParameters));
        endPointParameters.withMatchAll(false).withMatchValue(false);
        assertEquals(MATCH_OK, endPointParameters.doesMatch(receivedQueryParameters));
        endPointParameters.withMatchAll(false).withMatchValue(true);
        assertEquals(MATCH_OK, endPointParameters.doesMatch(receivedQueryParameters));
    }

    @Test
    public void testExpectedTwoAndReceivedTwo() {
        String receivedQueryParameters = "key1=value1&key2=value2";
        EndPointParameters endPointParameters = new EndPointParameters();
        endPointParameters.withParameter("key1", "value1");
        endPointParameters.withParameter("key2", "value2");

        endPointParameters.withMatchAll(true).withMatchValue(false);
        assertEquals(MATCH_OK, endPointParameters.doesMatch(receivedQueryParameters));
        endPointParameters.withMatchAll(true).withMatchValue(true);
        assertEquals(MATCH_OK, endPointParameters.doesMatch(receivedQueryParameters));
        endPointParameters.withMatchAll(false).withMatchValue(false);
        assertEquals(MATCH_OK, endPointParameters.doesMatch(receivedQueryParameters));
        endPointParameters.withMatchAll(false).withMatchValue(true);
        assertEquals(MATCH_OK, endPointParameters.doesMatch(receivedQueryParameters));
    }

    @Test
    public void testExpectedTwoAndReceivedThree() {
        String receivedQueryParameters = "key1=value1&key2=value2&key3=value3";
        EndPointParameters endPointParameters = new EndPointParameters();
        endPointParameters.withParameter("key1", "value1");
        endPointParameters.withParameter("key2", "value2");

        endPointParameters.withMatchAll(true).withMatchValue(false);
        assertEquals(MATCH_ALL_FAILED_SIZES_ARE_NOT_EQUAL, endPointParameters.doesMatch(receivedQueryParameters));
        endPointParameters.withMatchAll(true).withMatchValue(true);
        assertEquals(MATCH_ALL_FAILED_SIZES_ARE_NOT_EQUAL, endPointParameters.doesMatch(receivedQueryParameters));
        endPointParameters.withMatchAll(false).withMatchValue(false);
        assertEquals(MATCH_OK, endPointParameters.doesMatch(receivedQueryParameters));
        endPointParameters.withMatchAll(false).withMatchValue(true);
        assertEquals(MATCH_OK, endPointParameters.doesMatch(receivedQueryParameters));
    }

    @Test
    public void testExpectedTwoAndReceivedTwoForDifferentValues() {
        String receivedQueryParameters = "key1=value1&key2=value2";
        EndPointParameters endPointParameters = new EndPointParameters();
        endPointParameters.withParameter("key1", "value");
        endPointParameters.withParameter("key2", "value");

        endPointParameters.withMatchAll(true).withMatchValue(false);
        assertEquals(MATCH_OK, endPointParameters.doesMatch(receivedQueryParameters));
        endPointParameters.withMatchAll(true).withMatchValue(true);
        assertEquals(MATCH_FAILED_ON_VALUE, endPointParameters.doesMatch(receivedQueryParameters));
        endPointParameters.withMatchAll(false).withMatchValue(false);
        assertEquals(MATCH_OK, endPointParameters.doesMatch(receivedQueryParameters));
        endPointParameters.withMatchAll(false).withMatchValue(true);
        assertEquals(MATCH_FAILED_ON_VALUE, endPointParameters.doesMatch(receivedQueryParameters));
    }

    @Test
    public void testExpectedTwoAndReceivedTwoForDifferentValuesDifferentOrder() {
        String receivedQueryParameters = "key1=value1&key2=value2";
        EndPointParameters endPointParameters = new EndPointParameters();
        endPointParameters.withParameter("key2", "value");
        endPointParameters.withParameter("key1", "value");

        endPointParameters.withMatchAll(true).withMatchValue(false);
        assertEquals(MATCH_OK, endPointParameters.doesMatch(receivedQueryParameters));
        endPointParameters.withMatchAll(true).withMatchValue(true);
        assertEquals(MATCH_FAILED_ON_VALUE, endPointParameters.doesMatch(receivedQueryParameters));
        endPointParameters.withMatchAll(false).withMatchValue(false);
        assertEquals(MATCH_OK, endPointParameters.doesMatch(receivedQueryParameters));
        endPointParameters.withMatchAll(false).withMatchValue(true);
        assertEquals(MATCH_FAILED_ON_VALUE, endPointParameters.doesMatch(receivedQueryParameters));
    }
}