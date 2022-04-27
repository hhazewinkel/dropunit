package net.lisanza.dropunit.server.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotFoundException;
import java.util.ArrayList;
import java.util.List;

public class EndpointRegistrations extends ArrayList<DropUnitEndpoint> {

    private static final Logger LOGGER = LoggerFactory.getLogger(EndpointRegistrations.class);

    public List<DropUnitEndpoint> findByUrlAndMethod(String url, String method) {
        List<DropUnitEndpoint> subListByMethod = findByMethod(method, this);
        LOGGER.info("findByUrlAndMethod (by method): {}", subListByMethod.size());
        List<DropUnitEndpoint> subList = findByUrl(url, subListByMethod);
        LOGGER.info("findByUrlAndMethod: {}", subList.size());
        return subList;
    }

    public List<DropUnitEndpoint> findByMethod(String method, List<DropUnitEndpoint> endpoints) {
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

    public List<DropUnitEndpoint> findByUrl(String url, List<DropUnitEndpoint> endpoints) {
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

    public DropUnitEndpoint findById(String dropId) {
        if (dropId == null || dropId.isEmpty()) {
            LOGGER.warn("'dropId' is missing!");
            throw new BadRequestException("'dropId' is missing!");
        }
        DropUnitEndpoint endpoint = findInList(dropId);
        if (endpoint == null) {
            LOGGER.warn("no endpoint registered for {}", dropId);
            throw new NotFoundException("no endpoint registered for " + dropId);
        }
        return endpoint;
    }

    public DropUnitEndpoint findInList(String dropId) {
        for (DropUnitEndpoint endpoint : this) {
            if (dropId.equals(endpoint.getId())) {
                return endpoint;
            }
        }
        return null;
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