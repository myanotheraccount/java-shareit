package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class BookingRepositoryTest {
    @Autowired
    BookingRepository bookingRepository;

    @Autowired
    TestEntityManager em;

    @Test
    void checkBooking() {
        em.persist(new User(null, "name", "name@email.ru"));
        em.persist(new Item(null, null, "item", "description", true, 1L));
        em.persist(new Booking(null, 1L, LocalDateTime.now().minusDays(2), LocalDateTime.now().minusDays(1), BookingStatus.APPROVED, 1L));
        em.persist(new Booking(null, 1L, LocalDateTime.now().plusDays(1), null, BookingStatus.APPROVED, 1L));

        assertNotNull(bookingRepository.getLastBooking(1L));
        assertNull(bookingRepository.getLastBooking(2L));
        assertNotNull(bookingRepository.getNextBooking(1L));

        assertTrue(bookingRepository.existsBooking(1L, 1L, BookingStatus.APPROVED));
        assertFalse(bookingRepository.existsBooking(1L, 1L, BookingStatus.REJECTED));
    }
}
