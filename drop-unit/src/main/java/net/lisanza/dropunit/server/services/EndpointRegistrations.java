package net.lisanza.dropunit.server.services;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.BadRequestException;
import java.util.ArrayList;

class EndpointRegistrations extends ArrayList<DropUnitEndpoint> {

    private static final Logger LOGGER = LoggerFactory.getLogger(EndpointRegistrations.class);

    public DropUnitEndpoint findById(final String dropId) {
        if (StringUtils.isEmpty(dropId)) {
            LOGGER.warn("'dropId' is missing!");
            throw new BadRequestException("'dropId' is missing!");
        }
        DropUnitEndpoint endpoint = findInList(dropId);
        if (endpoint == null) {
            LOGGER.warn("no endpoint registered for {}", dropId);
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
}