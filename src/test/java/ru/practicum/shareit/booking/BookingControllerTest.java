package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.BookingController;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;


class BookingControllerTest {
    private BookingService bookingService;
    private BookingController bookingController;

    @BeforeEach
    void beforeEach() {
        this.bookingService = Mockito.mock(BookingService.class);
        this.bookingController = new BookingController(bookingService);
    }

    @Test
    void postBooking() {
        when(bookingService.save(Mockito.any(BookingDto.class), Mockito.anyLong())).thenReturn(new BookingDto());
        assertNotNull(bookingController.postBooking(1L, new BookingDto()));
    }

    @Test
    void approveBooking() {
        when(bookingService.approveBooking(Mockito.anyLong(), Mockito.anyLong(), Mockito.anyBoolean())).thenReturn(new BookingDto());
        assertNotNull(bookingController.approveBooking(1L, 1L, true));
    }

    @Test
    void getBooking() {
        when(bookingService.getBooking(Mockito.anyLong(), Mockito.anyLong())).thenReturn(new BookingDto());
        assertNotNull(bookingController.getBooking(1L, 1L));
    }

    @Test
    void getAllBookings() {
        when(bookingService.getAllBookings(Mockito.anyLong(), Mockito.any(BookingStatus.class), Mockito.any(Pageable.class)))
                .thenReturn(List.of(new BookingDto()));
        assertEquals(bookingController.getAllBookings(1L, "ALL", 0, 10).size(), 1);
    }

    @Test
    void getOwnerAllBookings() {
        when(bookingService.getOwnerAllBookings(Mockito.anyLong(), Mockito.any(BookingStatus.class), Mockito.any(Pageable.class)))
                .thenReturn(List.of(new BookingDto()));
        assertEquals(bookingController.getOwnerAllBookings(1L, "ALL", 0, 10).size(), 1);
    }
}