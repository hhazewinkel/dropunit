package net.lisanza.dropunit.server.rest.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DropUnitHeaderDto {

    @JsonProperty("name")
    private String name;

    @JsonProperty("value")
    private String value;

    public DropUnitHeaderDto() {
    }

    public DropUnitHeaderDto(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
