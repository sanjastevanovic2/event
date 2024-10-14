package com.event.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum UserOrderField {

    JMBG("jmbg"),
    FIRST_NAME("firstName"),
    LAST_NAME("lastName"),
    USERNAME("username");

    private final String label;
}
