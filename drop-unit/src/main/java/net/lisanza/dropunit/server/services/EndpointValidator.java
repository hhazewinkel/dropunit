package net.lisanza.dropunit.server.services;

import net.lisanza.dropunit.server.services.data.ReceivedRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.ValidationException;
import java.util.Map;

import static org.apache.commons.lang3.StringUtils.isEmpty;

public class EndpointValidator {
    private static final Logger LOGGER = LoggerFactory.getLogger(EndpointValidator.class);

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
        if (isEmpty(endpointValue)
                && isEmpty(requestValue)) {
            return;
        }
        if (!isEmpty(endpointValue)
                && !isEmpty(requestValue)
                && (endpointValue.equals(requestValue))) {
            return;
        }
        throw new ValidationException("header: '" + name + "' : '" + endpointValue + "' != request '" + requestValue + "'");
    }

    protected void validateRequestContentType(String endpointContentType, String requestContentType) {
        LOGGER.debug("validateRequestContentType: '{}' <> '{}'", endpointContentType, requestContentType);
        if (isEmpty(endpointContentType)
                && isEmpty(requestContentType)) {
            return;
        }
        if (!isEmpty(endpointContentType)
                && !isEmpty(requestContentType)
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
}
