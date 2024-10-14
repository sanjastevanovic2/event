package com.event.exception;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class ApiResponseExceptionMessage {
    private final LocalDateTime timestamp = LocalDateTime.now();
    private String code;
    private String message;
    private String path;
}
