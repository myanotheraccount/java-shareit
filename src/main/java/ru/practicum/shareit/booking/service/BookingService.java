package ru.practicum.shareit.booking.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.util.List;

public interface BookingService {
    BookingDto save(BookingDto bookingDto, Long userId);

    BookingDto approveBooking(Long userId, Long bookingId, Boolean isApproved);

    BookingDto getBooking(Long userId, Long bookingId);

    List<BookingDto> getAllBookings(Long userId, BookingStatus status, Pageable pageable);

    List<BookingDto> getOwnerAllBookings(Long userId, BookingStatus status, Pageable pageable);

    BookingShortDto getLastBooking(Long itemId);

    BookingShortDto getNextBooking(Long itemId);
}
