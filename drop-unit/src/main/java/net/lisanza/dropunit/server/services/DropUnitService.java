package net.lisanza.dropunit.server.services;

import net.lisanza.dropunit.server.services.data.ReceivedRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.BadRequestException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class DropUnitService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DropUnitService.class);

    private EndpointRegistrations registrations = new EndpointRegistrations();

    private List<DropUnitEndpoint> defaults = new ArrayList<>();

    private List<ReceivedRequest> notFound = new ArrayList<>();

    // getAll

    public Collection<DropUnitEndpoint> getAllDefaults() {
        return defaults;
    }

    public Collection<DropUnitEndpoint> getAllRegistrations() {
        return registrations;
    }

    public Collection<ReceivedRequest> getAllNotFound() {
        return notFound;
    }

    public String dropAll() {
        StringBuilder stringBuilder = new StringBuilder();
        infoLoadedEndpoints(stringBuilder.append("endpoints: "));
        registrations.clear();
        notFound.clear();
        registrations.addAll(defaults);
        infoLoadedEndpoints(stringBuilder.append(" -> after: "));
        return stringBuilder.toString();
    }

    public String registerDefault(DropUnitEndpoint endpoint) {
        String dropId = generateDropId(endpoint);
        endpoint.setId(dropId);
        defaults.add(endpoint);
        return dropId;
    }

    public String register(DropUnitEndpoint endpoint) {
        String dropId = generateDropId(endpoint);
        endpoint.setId(dropId);
        registrations.add(endpoint);
        return dropId;
    }

    public String generateDropId(DropUnitEndpoint endpoint) {
        if (endpoint == null) {
            String msg = "'drop unit' is missing!";
            LOGGER.error(msg);
            throw new BadRequestException(msg);
        }
        if (!endpoint.getUrl().startsWith("/")) {
            endpoint.setUrl("/" + endpoint.getUrl());
        }

        String dropId = Integer.toString(endpoint.hashCode());
        LOGGER.debug("register {} - {}", dropId, endpoint);
        return dropId;
    }

    public DropUnitEndpoint deregister(String dropId) {
        for (DropUnitEndpoint registration: registrations) {
            if ((registration.getId() != null) && (registration.getId().equals(dropId))) {
                registrations.remove(dropId);
                return registration;
            }
        }
        return null;
    }

    public String registerRequest(String dropId, DropUnitEndpointRequest patterns) {
        DropUnitEndpoint endpoint = lookupEndpoint(dropId);
        if (endpoint == null) {
            return "no registration found";
        }
        endpoint.setRequest(patterns);
        return "OK";
    }

    public String registerResponse(String dropId, DropUnitEndpointResponse response) {
        DropUnitEndpoint endpoint = lookupEndpoint(dropId);
        if (endpoint == null) {
            return "no registration found";
        }
        endpoint.setResponse(response);
        return "OK";
    }

    public List<DropUnitEndpoint> lookupEndpoint(String url, String method) {
        if (url == null) {
            LOGGER.warn("'url' is missing!");
            throw new BadRequestException("'url' is missing!");
        }
        if (method == null) {
            LOGGER.warn("'method' is missing!");
            throw new BadRequestException("'method' is missing!");
        }
        return registrations.findByUrlAndMethod(url, method);
    }

    public DropUnitEndpoint lookupEndpoint(String dropId) {
        return registrations.findById(dropId);
    }

    public void registerNotFound(String url, ReceivedRequest notFoundRequest) {
        if (url == null) {
            LOGGER.warn("'url' is missing!");
            throw new BadRequestException("'url' is missing!");
        }
        if (notFoundRequest == null) {
            LOGGER.warn("'request' is missing!");
            throw new BadRequestException("'request' is missing!");
        }
        notFound.add(new ReceivedRequest()
                .withUrl(url)
                .withMethod(notFoundRequest.getMethod())
                .withReceived(notFoundRequest.getBody()));
    }

    // Utils

    public StringBuilder infoLoadedEndpoints(StringBuilder stringBuilder) {
        stringBuilder.append("defaults ").append(defaults.size())
                .append(" registrations ").append(registrations.size())
                .append(" not found ").append(notFound.size());
        return stringBuilder;
    }
}