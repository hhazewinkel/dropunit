package net.lisanza.dropunit.server.rest.dto;

import java.util.Arrays;
import java.util.Optional;

public enum DropUnitComparionDto {
    TOTAL("MATCH-ALL"),
    PARTIAL("https://sit.domain.com:2019/");

    private String label;

    DropUnitComparionDto(String label) {
        this.label = label;
    }

    public String getUrl() {
        return label;
    }

    //****** Reverse Lookup ************//

    public static Optional<DropUnitComparionDto> get(String label) {
        return Arrays.stream(DropUnitComparionDto.values())
                .filter(comparision -> comparision.label.equals(label))
                .findFirst();
    }
}
