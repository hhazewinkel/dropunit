package net.lisanza.dropunit.server.services;

import net.lisanza.dropunit.server.services.data.ReceivedRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.ValidationException;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotFoundException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class EndpointLookupService {

    private static final Logger LOGGER = LoggerFactory.getLogger(EndpointRegistrationService.class);

    private EndpointRegistrationService registrationService;
    private final EndpointValidator validator = new EndpointValidator();

    public EndpointLookupService(EndpointRegistrationService registrationService) {
        this.registrationService = registrationService;
    }

    public DropUnitEndpoint lookupEndpoint(ReceivedRequest receivedRequest) {
        for (DropUnitEndpoint endpoint : lookupEndpoints(receivedRequest.getUrl(), receivedRequest.getMethod())) {
            try {
                // validate if this is the request endpoint to be used
                validator.validate(endpoint, receivedRequest);
                // request received
                endpoint.addReceived(receivedRequest);
                LOGGER.debug(endpoint.requestInfoString());
                // Response build up
                return endpoint;
            } catch (ValidationException e) {
                LOGGER.debug("{}\n{}\n{}", e.getMessage(),
                        endpoint.requestInfoString(),
                        receivedRequest.getBody());
            }
        }
        LOGGER.warn("missing registration: {}", receivedRequest);
        registrationService.registerNotFound(receivedRequest);
        throw new NotFoundException("missing registration: " + receivedRequest.getUrl());
    }

    public List<DropUnitEndpoint> lookupEndpoints(String url, String method) {
        if (url == null) {
            LOGGER.warn("'url' is missing!");
            throw new BadRequestException("'url' is missing!");
        }
        if (method == null) {
            LOGGER.warn("'method' is missing!");
            throw new BadRequestException("'method' is missing!");
        }
        List<DropUnitEndpoint> foundRegistrations = findByUrlAndMethod(url, method,
                registrationService.getDynamicRegistrations());
        Collections.sort(foundRegistrations);
        if (0 < foundRegistrations.size()) {
            return foundRegistrations;
        }
        List<DropUnitEndpoint> foundDefaults = findByUrlAndMethod(url, method,
                registrationService.getStaticRegistrations());
        Collections.sort(foundDefaults);
        return foundDefaults;
    }

    public List<DropUnitEndpoint> findByUrlAndMethod(String url, String method, Collection<DropUnitEndpoint> endpoints) {
        List<DropUnitEndpoint> subListByMethod = findByMethod(method, endpoints);
        LOGGER.info("findByUrlAndMethod (by method): {}", subListByMethod.size());
        List<DropUnitEndpoint> subList = findByUrl(url, subListByMethod);
        LOGGER.info("findByUrlAndMethod: {}", subList.size());
        return subList;
    }

    public List<DropUnitEndpoint> findByMethod(String method, Collection<DropUnitEndpoint> endpoints) {
        List<DropUnitEndpoint> subList = new ArrayList<>();
        for (DropUnitEndpoint endpoint : endpoints) {
            LOGGER.debug("findByMethod({}) <> {} (url: {})", method, endpoint.getMethod(), endpoint.getUrl());
            if (matchesMethod(method, endpoint.getMethod())) {
                subList.add(endpoint);
            }
        }
        LOGGER.info("findByMethod: {}", subList.size());
        return subList;
    }

    public List<DropUnitEndpoint> findByUrl(String url, Collection<DropUnitEndpoint> endpoints) {
        List<DropUnitEndpoint> subList = new ArrayList<>();
        for (DropUnitEndpoint endpoint : endpoints) {
            LOGGER.debug("findByUrl({}) <> {}", url, endpoint.getUrl());
            if (matchesUrl(url, endpoint.getUrl())) {
                subList.add(endpoint);
            }
        }
        LOGGER.info("findByUrl: {}", subList.size());
        return subList;
    }

    private boolean matchesUrl(String requestUrl, String endpointUrl) {
        if (endpointUrl.endsWith("?")) {
            LOGGER.debug("endpoint ends with ? and {} starts with {} ({})", endpointUrl, requestUrl, endpointUrl.startsWith(requestUrl));
            return requestUrl.startsWith(endpointUrl);
        }
        LOGGER.debug("endpoint full {} equals {} ({})", endpointUrl, requestUrl, endpointUrl.equals(requestUrl));
        return requestUrl.equals(endpointUrl);
    }

    private boolean matchesMethod(String method, String endpointMethod) {
        return method.equals(endpointMethod);
    }
}
