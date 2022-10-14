package ru.practicum.shareit.booking;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exceptions.ValidationException;

import javax.validation.Valid;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@Validated
public class BookingController {
    BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    public BookingDto postBooking(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @Valid @RequestBody BookingDto bookingDto
    ) {
        return bookingService.save(bookingDto, userId);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto approveBooking(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @PathVariable("bookingId") Long bookingId,
            @RequestParam("approved") Boolean approved
    ) {
        return bookingService.approveBooking(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getBooking(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @PathVariable("bookingId") Long bookingId
    ) {
        return bookingService.getBooking(userId, bookingId);
    }

    @GetMapping
    public List<BookingDto> getAllBookings(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestParam(value = "state", required = false, defaultValue = "ALL") String state,
            @RequestParam(value = "from", required = false, defaultValue = "0") @PositiveOrZero Integer from,
            @RequestParam(value = "size", required = false, defaultValue = "50") Integer size
    ) {
        return bookingService.getAllBookings(userId, parseStatus(state), PageRequest.of(from / size, size, Sort.Direction.DESC, "start"));
    }

    @GetMapping("/owner")
    public List<BookingDto> getOwnerAllBookings(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestParam(value = "state", required = false, defaultValue = "ALL") String state,
            @RequestParam(value = "from", required = false, defaultValue = "0") @PositiveOrZero Integer from,
            @RequestParam(value = "size", required = false, defaultValue = "50") Integer size
    ) {
        return bookingService.getOwnerAllBookings(userId, parseStatus(state), PageRequest.of(from / size, size));
    }

    private BookingStatus parseStatus(String status) {
        try {
            return BookingStatus.valueOf(status);
        } catch (Exception e) {
            throw new ValidationException("Unknown state: " + status);
        }
    }
}
