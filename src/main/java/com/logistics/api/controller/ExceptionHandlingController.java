package com.logistics.api.controller;

import com.logistics.api.exception.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;


@Slf4j
@ControllerAdvice
public class ExceptionHandlingController extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = BadRequestException.class)
    protected ResponseEntity<Object> handleException(BadRequestException ex) {
        return responseEntity(ex, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = NotFoundException.class)
    protected ResponseEntity<Object> handleException(NotFoundException ex) {
        return responseEntity(ex, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = OrderConflictException.class)
    protected ResponseEntity<Object> handleException(OrderConflictException ex) {
        return responseEntity(ex, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(value = MapException.class)
    protected ResponseEntity<Object> handleException(MapException ex) {
        return responseEntity(ex, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private ResponseEntity<Object> responseEntity(Throwable ex, HttpStatus httpStatus) {
        ErrorResponse errorResponse = new ErrorResponse(ex.getMessage());
        log.warn("API error: " + errorResponse);
        return new ResponseEntity<>(errorResponse, httpStatus);
    }

}
