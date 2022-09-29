package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingItemDto;
import ru.practicum.shareit.booking.model.Booking;

public class BookingMapper {
    public static BookingDto bookingToDto(Booking booking) {
        return new BookingDto(
                booking.getId(),
                booking.getItemId(),
                booking.getStart(),
                booking.getEnd(),
                booking.getStatus(),
                null,
                null
        );
    }

    public static Booking dtoToBooking(BookingDto bookingDto) {
        Booking booking = new Booking(
                bookingDto.getId(),
                bookingDto.getItemId(),
                bookingDto.getStart(),
                bookingDto.getEnd(),
                bookingDto.getStatus(),
                null
        );
        if (bookingDto.getBooker() != null) {
            booking.setBookerId(bookingDto.getBooker().getId());
        }
        return booking;
    }

    public static BookingItemDto bookingToItemDto(Booking booking) {
        return new BookingItemDto(
                booking.getId(),
                booking.getBookerId()
        );
    }
}
