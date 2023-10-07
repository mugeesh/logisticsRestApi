package com.logistics.api.exception;

import lombok.*;

@ToString
@Getter
@Setter
@AllArgsConstructor
public class ErrorResponse {
    private String error;
}