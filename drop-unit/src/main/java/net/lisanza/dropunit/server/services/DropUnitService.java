package net.lisanza.dropunit.server.services;

import net.lisanza.dropunit.server.services.data.ReceivedRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.BadRequestException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class DropUnitService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DropUnitService.class);

    private EndpointRegistrations registrations = new EndpointRegistrations();
    private EndpointRegistrations defaults = new EndpointRegistrations();
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
        LOGGER.debug("default before {}", defaults.size());
        String dropId = UUID.randomUUID().toString();
        endpoint.setId(dropId);
        LOGGER.debug("register default {} - {}", dropId, endpoint);
        defaults.add(endpoint);
        LOGGER.debug("defaults after {}", defaults.size());
        return dropId;
    }

    public String register(DropUnitEndpoint endpoint) {
        LOGGER.debug("registrations before {}", registrations.size());
        String dropId = UUID.randomUUID().toString();
        endpoint.setId(dropId);
        LOGGER.debug("register {} - {}", dropId, endpoint);
        registrations.add(endpoint);
        LOGGER.debug("registrations after {}", registrations.size());
        return dropId;
    }

    public DropUnitEndpoint deregister(String dropId) {
        LOGGER.debug("registrations before {}", registrations.size());
        for (DropUnitEndpoint registration: registrations) {
            if ((registration.getId() != null) && (registration.getId().equals(dropId))) {
                registrations.remove(registration);
                LOGGER.debug("registrations after {}", registrations.size());
                return registration;
            }
        }
        LOGGER.debug("registrations after {}", registrations.size());
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
        if (endpoint.getResponse() == null) {
            return "no regisered response found";
        }
        endpoint.getResponse().setContentType(response.getContentType());
        endpoint.getResponse().setBody(response.getBody());
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
        List<DropUnitEndpoint> foundUrls = registrations.findByUrlAndMethod(url, method);
        Collections.sort(foundUrls);
        return foundUrls;
    }

    public DropUnitEndpoint lookupEndpoint(String dropId) {
        return registrations.findById(dropId);
    }

    public void registerNotFound(ReceivedRequest notFoundRequest) {
        if (notFoundRequest == null) {
            LOGGER.warn("'request' is missing!");
            throw new BadRequestException("'request' is missing!");
        }
        notFound.add(notFoundRequest);
    }

    // Utils

    public StringBuilder infoLoadedEndpoints(StringBuilder stringBuilder) {
        stringBuilder.append("defaults ").append(defaults.size())
                .append(" registrations ").append(registrations.size())
                .append(" not found ").append(notFound.size());
        return stringBuilder;
    }
}
