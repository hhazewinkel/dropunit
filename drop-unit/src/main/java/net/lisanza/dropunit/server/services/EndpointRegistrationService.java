package net.lisanza.dropunit.server.services;

import net.lisanza.dropunit.server.services.data.ReceivedRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.BadRequestException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

public class EndpointRegistrationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(EndpointRegistrationService.class);

    private EndpointRegistrations dynamicRegistrations = new EndpointRegistrations();
    private EndpointRegistrations staticRegistrations = new EndpointRegistrations();
    private List<ReceivedRequest> notFound = new ArrayList<>();

    // getAll

    public Collection<DropUnitEndpoint> getStaticRegistrations() {
        return staticRegistrations;
    }

    public Collection<DropUnitEndpoint> getDynamicRegistrations() {
        return dynamicRegistrations;
    }

    public Collection<ReceivedRequest> getAllNotFound() {
        return notFound;
    }

    public String dropAll() {
        StringBuilder stringBuilder = new StringBuilder();
        infoLoadedEndpoints(stringBuilder.append("endpoints: "));
        dynamicRegistrations.clear();
        notFound.clear();
        infoLoadedEndpoints(stringBuilder.append(" -> after: "));
        return stringBuilder.toString();
    }

    public String registerStatic(DropUnitEndpoint endpoint) {
        LOGGER.debug("default before {}", staticRegistrations.size());
        String dropId = UUID.randomUUID().toString();
        endpoint.setId(dropId);
        LOGGER.debug("register default {} - {}", dropId, endpoint);
        staticRegistrations.add(endpoint);
        LOGGER.debug("defaults after {}", staticRegistrations.size());
        return dropId;
    }

    public String registerDynamic(DropUnitEndpoint endpoint) {
        LOGGER.debug("registrations before {}", dynamicRegistrations.size());
        String dropId = UUID.randomUUID().toString();
        endpoint.setId(dropId);
        LOGGER.debug("register {} - {}", dropId, endpoint);
        dynamicRegistrations.add(endpoint);
        LOGGER.debug("registrations after {}", dynamicRegistrations.size());
        return dropId;
    }

    public DropUnitEndpoint deregister(String dropId) {
        LOGGER.debug("registrations before {}", dynamicRegistrations.size());
        for (DropUnitEndpoint registration: dynamicRegistrations) {
            if ((registration.getId() != null) && (registration.getId().equals(dropId))) {
                dynamicRegistrations.remove(registration);
                LOGGER.debug("registrations after {}", dynamicRegistrations.size());
                return registration;
            }
        }
        LOGGER.debug("registrations after {}", dynamicRegistrations.size());
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

    public DropUnitEndpoint lookupEndpoint(String dropId) {
        return dynamicRegistrations.findById(dropId);
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
        stringBuilder.append("staticRegistrations ").append(staticRegistrations.size())
                .append(" dynamicRegistrations ").append(dynamicRegistrations.size())
                .append(" not found ").append(notFound.size());
        return stringBuilder;
    }
}
