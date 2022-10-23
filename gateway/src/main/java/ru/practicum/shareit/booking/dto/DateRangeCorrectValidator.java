package ru.practicum.shareit.booking.dto;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class DateRangeCorrectValidator implements ConstraintValidator<DateRangeCorrect, BookItemRequestDto> {
    @Override
    public void initialize(DateRangeCorrect constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(BookItemRequestDto bookingDto, ConstraintValidatorContext context) {
        return bookingDto.getEnd().isAfter(bookingDto.getStart());
    }
}