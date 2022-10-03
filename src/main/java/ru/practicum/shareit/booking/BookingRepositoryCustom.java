package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

public interface BookingRepositoryCustom {
    Booking getLastBooking(Long itemId);

    Booking getNextBooking(Long itemId);

    Boolean existsBooking(Long userId, Long bookerId, BookingStatus status);
}
