package ru.practicum.shareit.booking;

import org.springframework.beans.factory.annotation.Autowired;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.time.LocalDateTime;

public class BookingRepositoryImpl implements BookingRepositoryCustom {
    @Autowired
    private EntityManager entityManager;

    @Override
    public Booking getLastBooking(Long itemId) {
        Query query = entityManager.createQuery("SELECT b FROM Booking b WHERE b.itemId IN ?1 AND b.status = ?2 AND b.end < ?3 ORDER BY b.start DESC", Booking.class);
        query.setParameter(1, itemId);
        query.setParameter(2, BookingStatus.APPROVED);
        query.setParameter(3, LocalDateTime.now());
        try {
            return (Booking) query.getResultList().get(0);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public Booking getNextBooking(Long itemId) {
        Query query = entityManager.createQuery("SELECT b FROM Booking b WHERE b.itemId = ?1 AND b.status = ?2 AND b.start > ?3 ORDER BY b.start DESC", Booking.class);
        query.setParameter(1, itemId);
        query.setParameter(2, BookingStatus.APPROVED);
        query.setParameter(3, LocalDateTime.now());
        try {
            return (Booking) query.getResultList().get(0);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public Boolean existsBooking(Long userId, Long bookerId, BookingStatus status) {
        Query query = entityManager.createQuery("SELECT b FROM Booking b WHERE b.bookerId = ?1 AND b.itemId = ?2 AND b.status = ?3 AND b.start < ?4 ORDER BY b.start DESC", Booking.class);
        query.setParameter(1, userId);
        query.setParameter(2, bookerId);
        query.setParameter(3, status);
        query.setParameter(4, LocalDateTime.now());
        return query.getResultList().size() > 0;
    }
}
