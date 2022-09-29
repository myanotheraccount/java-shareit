package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findAllByBookerIdOrderByStartDesc(Long bookerId);

    List<Booking> findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(Long bookerId, LocalDateTime date, LocalDateTime dateTime);

    List<Booking> findAllByBookerIdAndEndBeforeOrderByStartDesc(Long bookerId, LocalDateTime date);

    List<Booking> findAllByBookerIdAndStartAfterOrderByStartDesc(Long bookerId, LocalDateTime date);

    List<Booking> findAllByBookerIdAndStatusEqualsOrderByStartDesc(Long bookerId, BookingStatus status);

    List<Booking> findAllByItemIdInOrderByStartDesc(List<Long> itemIds);

    List<Booking> findAllByItemIdInAndEndBeforeOrderByStartDesc(List<Long> itemIds, LocalDateTime dt);

    List<Booking> findAllByItemIdInAndStartAfterOrderByStartDesc(List<Long> itemIds, LocalDateTime dt);

    List<Booking> findAllByItemIdInAndStartBeforeAndEndAfterOrderByStartDesc(List<Long> itemIds, LocalDateTime dt, LocalDateTime date);

    List<Booking> findAllByItemIdInAndStatusEqualsOrderByStartDesc(List<Long> itemIds, BookingStatus status);

    Booking findFirstByItemIdAndStatusAndEndIsBeforeOrderByStartDesc(Long itemId, BookingStatus status, LocalDateTime dt);

    Booking findFirstByItemIdAndStatusAndStartIsAfterOrderByStartDesc(Long itemId, BookingStatus status, LocalDateTime dt);

    Boolean existsBookingByBookerIdAndItemIdAndStatusAndStartBefore(Long userId, Long bookerId, BookingStatus status, LocalDateTime dt);
}
