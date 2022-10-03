package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingShortDto;

import java.util.List;

public interface BookingService {
    BookingDto save(BookingDto bookingDto, Long userId);

    BookingDto approveBooking(Long userId, Long bookingId, Boolean isApproved);

    BookingDto getBooking(Long userId, Long bookingId);

    List<BookingDto> getAllBookings(Long userId, String status);

    List<BookingDto> getOwnerAllBookings(Long userId, String status);

    BookingShortDto getLastBooking(Long itemId);

    BookingShortDto getNextBooking(Long itemId);
}
