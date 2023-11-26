package net.lisanza.dropunit.engineundertest.config.yml;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.core.Configuration;

import javax.validation.Valid;

public class AppConfiguration extends Configuration {

    @Valid
    private String proxyUrl;

    @JsonProperty
    public String getProxyUrl() {
        return proxyUrl;
    }

    @JsonProperty
    public void setProxyUrl(String proxyUrl) {
        this.proxyUrl = proxyUrl;
    }
}