package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BookingServiceTest {
    private BookingService bookingService;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;

    @BeforeEach
    void beforeEach() {
        this.bookingService = new BookingServiceImpl(bookingRepository, itemRepository, userRepository);
    }

    @Test
    void saveInvalid() {
        when(userRepository.existsById(1L)).thenReturn(true);
        when(userRepository.existsById(2L)).thenReturn(false);
        when(itemRepository.existsById(1L)).thenReturn(false);
        when(itemRepository.existsById(2L)).thenReturn(true);
        when(itemRepository.getReferenceById(2L)).thenAnswer(i -> {
            Item item = new Item();
            item.setAvailable(false);
            return item;
        });

        BookingDto bookingDto = new BookingDto();
        NotFoundException exception = assertThrows(NotFoundException.class, () -> bookingService.save(bookingDto, 2L));
        assertEquals(exception.getMessage(), "такого пользователя не существует");

        bookingDto.setItemId(1L);
        NotFoundException thrown = assertThrows(NotFoundException.class, () -> bookingService.save(bookingDto, 1L));
        assertEquals(thrown.getMessage(), "предмет не найден");

        bookingDto.setItemId(2L);
        ValidationException thrown1 = assertThrows(ValidationException.class, () -> bookingService.save(bookingDto, 1L));
        assertEquals(thrown1.getMessage(), "предмет нельзя забронировать");
    }

    @Test
    void save() {
        when(userRepository.existsById(Mockito.anyLong())).thenReturn(true);
        when(itemRepository.existsById(Mockito.anyLong())).thenReturn(true);
        when(userRepository.getReferenceById(Mockito.anyLong())).thenReturn(new User());
        when(itemRepository.getReferenceById(Mockito.anyLong())).thenAnswer(a -> {
            Item item = new Item();
            item.setOwner(1L);
            item.setAvailable(true);
            return item;
        });
        when(bookingRepository.save(Mockito.any(Booking.class))).thenAnswer(i -> i.getArguments()[0]);

        BookingDto bookingDto = new BookingDto();
        bookingDto.setItemId(1L);
        NotFoundException thrown = assertThrows(NotFoundException.class, () -> bookingService.save(bookingDto, 1L));
        assertEquals(thrown.getMessage(), "владелец предмета не может его бронировать");

        assertEquals(bookingService.save(bookingDto, 2L).getStatus(), BookingStatus.WAITING);
    }

    @Test
    void approve() {
        when(userRepository.getReferenceById(Mockito.anyLong())).thenReturn(new User());
        when(userRepository.existsById(Mockito.anyLong())).thenReturn(true);
        when(bookingRepository.getReferenceById(Mockito.anyLong())).thenAnswer(i -> {
            Long id = (Long) i.getArguments()[0];
            Booking booking = new Booking();
            booking.setItemId(id);
            booking.setBookerId(1L);
            booking.setStatus(id.equals(2L) ? BookingStatus.APPROVED : BookingStatus.WAITING);
            return booking;
        });
        when(itemRepository.getReferenceById(Mockito.anyLong())).thenAnswer(i -> {
            Long id = (Long) i.getArguments()[0];
            Item item = new Item();
            item.setId(1L);
            item.setOwner(id);
            return item;
        });

        ValidationException thrown = assertThrows(ValidationException.class, () -> bookingService.approveBooking(1L, 2L, true));
        assertEquals(thrown.getMessage(), "бронирование уже подтверждено");

        NotFoundException thrown2 = assertThrows(NotFoundException.class, () -> bookingService.approveBooking(1L, 3L, true));
        assertEquals(thrown2.getMessage(), "не удалось подтвердить бронирование");

        assertEquals(bookingService.approveBooking(1L, 1L, true).getStatus(), BookingStatus.APPROVED);
        assertEquals(bookingService.approveBooking(1L, 1L, false).getStatus(), BookingStatus.REJECTED);
    }

    @Test
    void get() {
        when(userRepository.getReferenceById(Mockito.anyLong())).thenReturn(new User());
        when(userRepository.existsById(Mockito.anyLong())).thenReturn(true);
        when(bookingRepository.existsById(1L)).thenReturn(true);
        when(bookingRepository.existsById(2L)).thenReturn(false);
        when(itemRepository.getReferenceById(Mockito.anyLong())).thenAnswer(a -> {
            Item item = new Item();
            item.setOwner(1L);
            return item;
        });
        when(bookingRepository.getReferenceById(Mockito.anyLong())).thenAnswer(a -> {
            Booking booking = new Booking();
            booking.setItemId(1L);
            booking.setBookerId(1L);
            return booking;
        });
        NotFoundException thrown = assertThrows(NotFoundException.class, () ->
                bookingService.getBooking(1L, 2L)
        );
        assertEquals(thrown.getMessage(), "не удалось найти бронирование");

        BookingDto bookingDto = bookingService.getBooking(1L, 1L);
        BookingDto.Item item = bookingDto.getItem();
        assertEquals(item.getOwner(), 1);
        assertNotNull(bookingDto.getBooker());
    }

    @Test
    void getAll() {
        Booking booking = new Booking();
        booking.setItemId(1L);
        booking.setBookerId(1L);
        when(userRepository.getReferenceById(Mockito.anyLong())).thenReturn(new User());
        when(itemRepository.getReferenceById(Mockito.anyLong())).thenReturn(new Item());

        when(bookingRepository.findAllByBookerId(Mockito.anyLong(), Mockito.any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(booking)));
        when(bookingRepository.findBookerAllPast(Mockito.anyLong(), Mockito.any(LocalDateTime.class))).thenReturn(List.of(booking));
        when(bookingRepository.findBookerAllCurrent(Mockito.anyLong(), Mockito.any(LocalDateTime.class))).thenReturn(List.of(booking));
        when(bookingRepository.findBookerAllFuture(Mockito.anyLong(), Mockito.any(LocalDateTime.class))).thenReturn(List.of(booking));
        when(bookingRepository.findBookerAllByStatus(1L, BookingStatus.WAITING)).thenReturn(List.of(booking));
        when(bookingRepository.findBookerAllByStatus(1L, BookingStatus.REJECTED)).thenReturn(List.of());

        assertEquals(bookingService.getAllBookings(1L, BookingStatus.ALL, PageRequest.of(0, 10)).size(), 1);
        assertEquals(bookingService.getAllBookings(1L, BookingStatus.PAST, PageRequest.of(0, 10)).size(), 1);
        assertEquals(bookingService.getAllBookings(1L, BookingStatus.CURRENT, PageRequest.of(0, 10)).size(), 1);
        assertEquals(bookingService.getAllBookings(1L, BookingStatus.FUTURE, PageRequest.of(0, 10)).size(), 1);
        assertEquals(bookingService.getAllBookings(1L, BookingStatus.WAITING, PageRequest.of(0, 10)).size(), 1);

        NotFoundException thrown = assertThrows(NotFoundException.class, () -> bookingService.getAllBookings(1L, BookingStatus.REJECTED, PageRequest.of(0, 10)));
        assertEquals(thrown.getMessage(), "не удалось найти список бронирований");
    }

    @Test
    void getOwnerAll() {
        Booking booking = new Booking();
        booking.setItemId(1L);
        booking.setBookerId(1L);

        when(userRepository.getReferenceById(Mockito.anyLong())).thenReturn(new User());
        when(itemRepository.getReferenceById(Mockito.anyLong())).thenReturn(new Item());
        when(bookingRepository.findAllByItemIdInOrderByStartDesc(Mockito.anyList(), Mockito.any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(booking)));
        when(bookingRepository.findItemsInPast(Mockito.anyList(), Mockito.any(LocalDateTime.class))).thenReturn(List.of(booking));
        when(bookingRepository.findItemsInCurrent(Mockito.anyList(), Mockito.any(LocalDateTime.class))).thenReturn(List.of(booking));
        when(bookingRepository.findItemsInFuture(Mockito.anyList(), Mockito.any(LocalDateTime.class))).thenReturn(List.of(booking));
        when(bookingRepository.findItemsByStatus(List.of(), BookingStatus.APPROVED)).thenReturn(List.of(booking));
        when(bookingRepository.findItemsByStatus(List.of(), BookingStatus.REJECTED)).thenReturn(List.of());

        assertEquals(bookingService.getOwnerAllBookings(1L, BookingStatus.ALL, PageRequest.of(0, 10)).size(), 1);
        assertEquals(bookingService.getOwnerAllBookings(1L, BookingStatus.PAST, PageRequest.of(0, 10)).size(), 1);
        assertEquals(bookingService.getOwnerAllBookings(1L, BookingStatus.CURRENT, PageRequest.of(0, 10)).size(), 1);
        assertEquals(bookingService.getOwnerAllBookings(1L, BookingStatus.FUTURE, PageRequest.of(0, 10)).size(), 1);
        assertEquals(bookingService.getOwnerAllBookings(1L, BookingStatus.APPROVED, PageRequest.of(0, 10)).size(), 1);

        NotFoundException thrown = assertThrows(NotFoundException.class, () ->
                bookingService.getOwnerAllBookings(1L, BookingStatus.REJECTED, PageRequest.of(0, 10)));
        assertEquals(thrown.getMessage(), "не удалось найти список бронирований");
    }

    @Test
    void lastBooking() {
        when(bookingRepository.getLastBooking(Mockito.anyLong())).thenReturn(new Booking(
                1L,
                1L,
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(1),
                BookingStatus.APPROVED,
                4L
        ));
        BookingShortDto bookingShortDto = bookingService.getLastBooking(1L);
        assertEquals(bookingShortDto.getId(), 1L);
        assertEquals(bookingShortDto.getBookerId(), 4L);
    }

    @Test
    void nextBooking() {
        when(bookingRepository.getNextBooking(Mockito.anyLong())).thenReturn(new Booking(
                1L,
                1L,
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(1),
                BookingStatus.APPROVED,
                4L
        ));
        BookingShortDto bookingShortDto = bookingService.getNextBooking(1L);
        assertEquals(bookingShortDto.getId(), 1L);
        assertEquals(bookingShortDto.getBookerId(), 4L);
    }
}
