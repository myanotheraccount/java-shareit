package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;

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

    public static BookingShortDto bookingToShortDto(Booking booking) {
        return new BookingShortDto(
                booking.getId(),
                booking.getBookerId()
        );
    }

    public static BookingDto.Item itemToBookingItemDto(Item item) {
        return new BookingDto.Item(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getOwner(),
                null,
                null,
                null
        );
    }
}
