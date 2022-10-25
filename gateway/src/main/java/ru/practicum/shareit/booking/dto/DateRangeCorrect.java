package ru.practicum.shareit.booking.dto;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = DateRangeCorrectValidator.class)
public @interface DateRangeCorrect {
    String message() default "некорректно задан период бронирования";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}