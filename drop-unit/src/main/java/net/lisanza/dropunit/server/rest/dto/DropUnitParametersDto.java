package net.lisanza.dropunit.server.rest.dto;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.LinkedHashMap;

import static org.apache.commons.lang3.StringUtils.isNotEmpty;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonPropertyOrder({"matchAll", "matchValue", "included", "excluded"})
public class DropUnitParametersDto {

    @JsonProperty("matchAll")
    private boolean matchAll;

    @JsonProperty("matchValue")
    private boolean matchValue;

    @JsonProperty("included")
    private LinkedHashMap<String, String> included = new LinkedHashMap<>();

    @JsonProperty("excluded")
    private LinkedHashMap<String, String> excluded = new LinkedHashMap<>();

    public boolean isMatchAll() {
        return matchAll;
    }

    public void setMatchAll(boolean matchAll) {
        this.matchAll = matchAll;
    }

    public DropUnitParametersDto withMatchAll(boolean matchAll) {
        this.matchAll = matchAll;
        return this;
    }

    public boolean isMatchValue() {
        return matchValue;
    }

    public void setMatchValue(boolean matchValue) {
        this.matchValue = matchValue;
    }

    public DropUnitParametersDto withMatchValue(boolean matchValue) {
        this.matchValue = matchValue;
        return this;
    }

    public LinkedHashMap<String, String> getIncludedParameters() {
        return included;
    }

    public void setIncludedParameters(LinkedHashMap<String, String> parameters) {
        this.included = parameters;
    }

    public DropUnitParametersDto withParameter(String key, String value) {
        this.included.put(key, value);
        return this;
    }

    public DropUnitParametersDto withParameterString(String parameterString) {
        if (isNotEmpty(parameterString)) {
            // split into parameters (key / value cobinations)
            for (String pair : parameterString.split("&")) {
                final String[] keyValue = pair.split("=");
                withParameter(keyValue[0], keyValue[1]);
            }
        }
        return this;
    }

    public LinkedHashMap<String, String> getExcludedParameters() {
        return excluded;
    }

    public void setExcludedParameters(LinkedHashMap<String, String> parameters) {
        this.excluded = parameters;
    }

    public DropUnitParametersDto withoutParameter(String key, String value) {
        this.excluded.put(key, value);
        return this;
    }
}
