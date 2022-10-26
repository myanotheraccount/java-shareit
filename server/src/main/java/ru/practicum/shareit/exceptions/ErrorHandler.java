package ru.practicum.shareit.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
@Slf4j
public class ErrorHandler extends Throwable {
    @ExceptionHandler
    public ResponseEntity<?> catchNotFoundException(NotFoundException e) {
        log.error(e.getMessage(), e);
        return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    public ResponseEntity<?> catchValidationException(final ValidationException e) {
        log.error(e.getMessage());
        Map<String, Object> map = new HashMap<>();
        map.put("error", e.getMessage());
        return new ResponseEntity<>(map, HttpStatus.BAD_REQUEST);
    }
}
