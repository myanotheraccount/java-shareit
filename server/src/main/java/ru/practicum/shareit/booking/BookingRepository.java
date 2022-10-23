package ru.practicum.shareit.booking;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long>, BookingRepositoryCustom {
    Page<Booking> findAllByBookerId(Long bookerId, Pageable pageable);

    @Modifying
    @Query("SELECT b FROM Booking b WHERE b.bookerId=?1 AND ?2 BETWEEN b.start AND b.end ORDER BY b.start DESC")
    List<Booking> findBookerAllCurrent(Long bookerId, LocalDateTime date);

    @Modifying
    @Query("SELECT b FROM Booking b WHERE b.bookerId=?1 AND b.end < ?2 ORDER BY b.start DESC")
    List<Booking> findBookerAllPast(Long bookerId, LocalDateTime date);

    @Modifying
    @Query("SELECT b FROM Booking b WHERE b.bookerId=?1 AND b.start > ?2 ORDER BY b.start DESC")
    List<Booking> findBookerAllFuture(Long bookerId, LocalDateTime date);

    @Modifying
    @Query("SELECT b FROM Booking b WHERE b.bookerId=?1 AND b.status = ?2 ORDER BY b.start DESC")
    List<Booking> findBookerAllByStatus(Long bookerId, BookingStatus status);

    Page<Booking> findAllByItemIdInOrderByStartDesc(List<Long> itemIds, Pageable pageable);

    @Modifying
    @Query("SELECT b FROM Booking b WHERE b.itemId IN ?1 AND b.end < ?2 ORDER BY b.start DESC")
    List<Booking> findItemsInPast(List<Long> itemIds, LocalDateTime dt);

    @Modifying
    @Query("SELECT b FROM Booking b WHERE b.itemId IN ?1 AND b.start > ?2 ORDER BY b.start DESC")
    List<Booking> findItemsInFuture(List<Long> itemIds, LocalDateTime dt);

    @Modifying
    @Query("SELECT b FROM Booking b WHERE b.itemId IN ?1 AND ?2 BETWEEN b.start AND b.end ORDER BY b.start DESC")
    List<Booking> findItemsInCurrent(List<Long> itemIds, LocalDateTime dt);

    @Modifying
    @Query("SELECT b FROM Booking b WHERE b.itemId IN ?1 AND b.status = ?2 ORDER BY b.start DESC")
    List<Booking> findItemsByStatus(List<Long> itemIds, BookingStatus status);
}
