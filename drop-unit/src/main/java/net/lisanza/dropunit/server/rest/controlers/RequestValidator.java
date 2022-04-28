package net.lisanza.dropunit.server.rest.controlers;

import net.lisanza.dropunit.server.services.DropUnitEndpoint;
import net.lisanza.dropunit.server.services.DropUnitEndpointRequest;
import net.lisanza.dropunit.server.services.data.ReceivedRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.ValidationException;
import java.util.Map;

public class RequestValidator {
    private static final Logger LOGGER = LoggerFactory.getLogger(RequestValidator.class);

    protected void validate(DropUnitEndpoint endpoint, ReceivedRequest receivedRequest) {
        // validate
        validateRequestHeaders(endpoint, receivedRequest.getHeaders());
        if (endpoint.getRequest() != null) {
            validateRequestContentType(endpoint.getRequest().getContentType(), receivedRequest.getContentType());
            validateRequestContent(endpoint.getRequest(), receivedRequest.getBody());
        }
    }

    protected void validateRequestHeaders(DropUnitEndpoint dropUnitEndpoint, Map<String, String> requestHeaders) {
        for (String name : dropUnitEndpoint.getHeaders().keySet()) {
            validateRequestHeader(name, dropUnitEndpoint.getHeaders().get(name), requestHeaders.get(name));
        }
    }

    protected void validateRequestHeader(String name, String endpointValue, String requestValue) {
        LOGGER.debug("validateRequestHeader({}): '{}' <> '{}'", name, endpointValue, requestValue);
        if (isNullOrEmpty(endpointValue)
                && isNullOrEmpty(requestValue)) {
            return;
        }
        if (!isNullOrEmpty(endpointValue)
                && !isNullOrEmpty(requestValue)
                && (endpointValue.equals(requestValue))) {
            return;
        }
        throw new ValidationException("header: '" + name + "' : '" + endpointValue + "' != request '" + requestValue + "'");
    }

    protected void validateRequestContentType(String endpointContentType, String requestContentType) {
        LOGGER.debug("validateRequestContentType: '{}' <> '{}'", endpointContentType, requestContentType);
        if (isNullOrEmpty(endpointContentType)
                && isNullOrEmpty(requestContentType)) {
            return;
        }
        if (!isNullOrEmpty(endpointContentType)
                && !isNullOrEmpty(requestContentType)
                && (endpointContentType.equals(requestContentType))) {
            return;
        }
        throw new ValidationException("content-type: '" + endpointContentType + "' != request '" + requestContentType + "'");
    }

    private void validateRequestContent(DropUnitEndpointRequest dropUnitRequest, String body) {
        if (dropUnitRequest != null) {
            for (String pattern : dropUnitRequest.getPatterns()) {
                LOGGER.debug("validate pattern '{}' in body", pattern);
                if (!body.contains(pattern)) {
                    throw new ValidationException("pattern: '" + pattern + "' not in body\n" + body);
                }
            }
            return;
        }
        throw new ValidationException("request-body: not matching'\n" + body);
    }

    private boolean isNullOrEmpty(String string) {
        return (string == null) || string.isEmpty();
    }

}
