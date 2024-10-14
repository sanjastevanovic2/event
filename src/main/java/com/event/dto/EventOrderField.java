package com.event.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum EventOrderField {

    ID("id"),
    NAME("name"),
    DESCRIPTION("description"),
    LOCATION("location"),
    START_TIME("startTime"),
    END_TIME("endTime");

    private final String label;

}
