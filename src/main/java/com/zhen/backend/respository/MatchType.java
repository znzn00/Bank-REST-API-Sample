package com.zhen.backend.respository;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@AllArgsConstructor
@Getter
public enum MatchType {
    EQUAL("E"),
    NOT_EQUAL("NE"),
    LIKE("L"),
    NOT_LIKE("NL"),
    LESS_THAN("LT"),
    LESS_THAN_OR_EQUAL("LTE"),
    GREATER_THAN("GT"),
    GREATER_THAN_OR_EQUAL("GTE"),
    START_WITH("SW"),
    NOT_STARTING_WITH("NSW"),
    ENDS_WITH("EW"),
    NOT_ENDING_WITH("NEW"),
    BETWEEN("B"),
    IN("IN"),
    NOT_IN("NIN");

    private final String etiqueta;

    private final static Map<String, MatchType> matchType = new HashMap<>();

    static {
        for (MatchType m : values()
        ) {
            matchType.put(m.getEtiqueta(), m);
        }
    }

    public static MatchType valueOfEtiqueta(String etiqueta) {
        return matchType.get(etiqueta);
    }


}
