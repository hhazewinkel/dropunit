package net.lisanza.dropunit.server.config.yml;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.core.Configuration;

import java.util.List;

public class DropUnitConfiguration extends Configuration {

    private List<EndpointDocument> endpoints;

    @JsonProperty
    public List<EndpointDocument> getEndpoints() {
        return endpoints;
    }

    @JsonProperty
    public void setEndpoints(List<EndpointDocument> endpoints) {
        this.endpoints = endpoints;
    }

}
