package net.lisanza.dropunit.server.rest.dto;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.apache.commons.lang3.StringUtils.isNotEmpty;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonPropertyOrder({"mustMatch", "parameters"})
public class DropUnitUrlParametersDto {

    @JsonProperty("mustMatch")
    private boolean mustMatch;

    @JsonProperty()
    private Map<String, String> parameters;

    @JsonProperty("order")
    private List<String> order;

    // constructor

    public DropUnitUrlParametersDto() {
        this(false);
    }

    public DropUnitUrlParametersDto(String uriParameters) {
        this(false, uriParameters);
    }

    public DropUnitUrlParametersDto(boolean withOrder, String uriParameters) {
        this(true);
        if (isNotEmpty(uriParameters)) {
            if (withOrder) {
                order = new ArrayList<>();
            }
            // split into parameters (key / value cobinations)
            for (String pair : uriParameters.split("&")) {
                final String[] keyValue = pair.split("=");
                withParameter(keyValue[0], keyValue[1]);
                if (withOrder) {
                    order.add(keyValue[0]);
                }
            }
        }
    }

    public DropUnitUrlParametersDto(boolean mustMatch) {
        this.mustMatch = mustMatch;
        this.parameters = new HashMap<>();
    }

    public DropUnitUrlParametersDto withParameter(final String key,
                                                  final String value) {
        parameters.put(key, value);
        return this;
    }

    // Getter & Setter

    public boolean isMustMatch() {
        return mustMatch;
    }

    public void setMustMatch(boolean mustMatch) {
        this.mustMatch = mustMatch;
    }

    public Map<String, String> getParameters() {
        return parameters;
    }

    public void setParameters(Map<String, String> parameters) {
        this.parameters = parameters;
    }
}
