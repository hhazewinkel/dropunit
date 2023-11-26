package net.lisanza.dropunit.server.services;

import net.lisanza.dropunit.server.rest.dto.DropUnitParametersDto;

import java.util.LinkedHashMap;

import static org.apache.commons.lang3.StringUtils.isNotEmpty;

public class EndPointParameters extends DropUnitParametersDto {

    private final static int KEY_OF_PAIR = 0;
    private final static int VALUE_OF_PAIR = 0;

    enum EndPointParameterComparison {
        MATCH_OK,
        MATCH_ALL_FAILED_SIZES_ARE_NOT_EQUAL,
        MATCH_FAILED_EXPECTED_MORE_THEN_RECEIVED,
        MATCH_FAILED_PARAMETER_WAS_EXPECTED,
        MATCH_FAILED_PARAMETER_WAS_EXCLUDED,
        MATCH_FAILED_ON_VALUE_NULL,
        MATCH_FAILED_ON_VALUE
    }

    public EndPointParameterComparison doesMatch(String receivedQueryString) {
        LinkedHashMap<String, String> receivedParameters = new LinkedHashMap<>();
        if (isNotEmpty(receivedQueryString)) {
            // split into parameters (key / value cobinations)
            for (String pair : receivedQueryString.split("&")) {
                final String[] keyValue = pair.split("=");
                receivedParameters.put(keyValue[0], keyValue[1]);
            }
        }
        return doesMatch(receivedParameters);
    }

    public EndPointParameterComparison doesMatch(LinkedHashMap<String, String> receivedParameters) {
        if (isMatchAll() && getIncludedParameters().size() != receivedParameters.size()) {
            return EndPointParameterComparison.MATCH_ALL_FAILED_SIZES_ARE_NOT_EQUAL;
        }
        if (!isMatchAll() && (receivedParameters.size() < getIncludedParameters().size())) {
            return EndPointParameterComparison.MATCH_FAILED_EXPECTED_MORE_THEN_RECEIVED;
        }
        for (String receivedParameterKey : receivedParameters.keySet()) {
            if (getExcludedParameters().containsKey(receivedParameterKey)) {
                return EndPointParameterComparison.MATCH_FAILED_PARAMETER_WAS_EXCLUDED;
            }
        }
        for (String key : getIncludedParameters().keySet()) {
            if (isMatchAll() && !receivedParameters.containsKey(key)) {
                return EndPointParameterComparison.MATCH_FAILED_PARAMETER_WAS_EXPECTED;
            }
            if (isMatchValue() && receivedParameters.containsKey(key)) {
                String receivedParameterValue = receivedParameters.get(key);
                String includedParameterValue = getIncludedParameters().get(key);
                if ((receivedParameterValue != null) && (includedParameterValue == null)) {
                    return EndPointParameterComparison.MATCH_FAILED_ON_VALUE_NULL;
                }
                if ((receivedParameterValue == null) && (includedParameterValue != null)) {
                    return EndPointParameterComparison.MATCH_FAILED_ON_VALUE_NULL;
                }
                if (!receivedParameterValue.equals(includedParameterValue)) {
                    return EndPointParameterComparison.MATCH_FAILED_ON_VALUE;
                }
            }
        }
        return EndPointParameterComparison.MATCH_OK;
    }

}
