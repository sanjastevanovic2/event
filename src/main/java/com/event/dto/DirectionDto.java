package com.event.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum DirectionDto {

    ASC("asc"),
    DESC("desc");

    private final String direction;

}
