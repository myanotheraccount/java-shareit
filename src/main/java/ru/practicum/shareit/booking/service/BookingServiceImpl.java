package ru.practicum.shareit.booking.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.service.UserMapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    public BookingServiceImpl(BookingRepository bookingRepository, ItemRepository itemRepository, UserRepository userRepository) {
        this.bookingRepository = bookingRepository;
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public BookingDto save(BookingDto bookingDto, Long userId) {
        checkUserExist(userId);
        vaildateBooking(bookingDto);
        Booking booking = BookingMapper.dtoToBooking(bookingDto);
        Item item = itemRepository.getReferenceById(booking.getItemId());
        if (item.getOwner().equals(userId)) {
            log.info("владелец предмета не может его бронировать");
            throw new NotFoundException("владелец предмета не может его бронировать");
        }
        booking.setBookerId(userId);
        booking.setStatus(BookingStatus.WAITING);
        return BookingMapper.bookingToDto(bookingRepository.save(booking));
    }

    @Override
    @Transactional
    public BookingDto approveBooking(Long userId, Long bookingId, Boolean isApproved) {
        Booking booking = bookingRepository.getReferenceById(bookingId);
        Item item = itemRepository.getReferenceById(booking.getItemId());
        if (booking.getStatus().equals(BookingStatus.APPROVED)) {
            log.info("бронирование уже подтверждено");
            throw new ValidationException("бронирование уже подтверждено");
        }
        if (checkUserExist(userId) && Objects.equals(item.getOwner(), userId)) {
            booking.setStatus(isApproved ? BookingStatus.APPROVED : BookingStatus.REJECTED);
            bookingRepository.save(booking);
            BookingDto bookingDto = BookingMapper.bookingToDto(booking);
            bookingDto.setItem(BookingMapper.itemToBookingItemDto(item));
            bookingDto.setBooker(UserMapper.userToDto(userRepository.getReferenceById(booking.getBookerId())));
            return bookingDto;
        }
        log.info("не удалось подтвердить бронирование");
        throw new NotFoundException("не удалось подтвердить бронирование");
    }

    @Override
    public BookingDto getBooking(Long userId, Long bookingId) {
        if (bookingRepository.existsById(bookingId)) {
            Booking booking = bookingRepository.getReferenceById(bookingId);
            Item item = itemRepository.getReferenceById(booking.getItemId());
            if (checkUserExist(userId) && (booking.getBookerId().equals(userId) || item.getOwner().equals(userId))) {
                BookingDto bookingDto = BookingMapper.bookingToDto(booking);
                bookingDto.setItem(BookingMapper.itemToBookingItemDto(item));
                bookingDto.setBooker(UserMapper.userToDto(userRepository.getReferenceById(booking.getBookerId())));
                return bookingDto;
            }
        }
        log.info("не удалось найти бронирование");
        throw new NotFoundException("не удалось найти бронирование");
    }

    @Override
    public List<BookingDto> getAllBookings(Long userId, BookingStatus bookingStatus, Pageable pageable) {
        List<Booking> bookings;
        LocalDateTime dt = LocalDateTime.now();
        switch (bookingStatus) {
            case ALL:
                bookings = bookingRepository.findAllByBookerId(userId, pageable)
                        .stream().collect(Collectors.toList());
                break;
            case PAST:
                bookings = bookingRepository.findBookerAllPast(userId, dt);
                break;
            case CURRENT:
                bookings = bookingRepository.findBookerAllCurrent(userId, dt);
                break;
            case FUTURE:
                bookings = bookingRepository.findBookerAllFuture(userId, dt);
                break;
            default:
                bookings = bookingRepository.findBookerAllByStatus(userId, bookingStatus);
        }
        List<BookingDto> bookingDtos = bookings.stream().map(booking -> {
            BookingDto bookingDto = BookingMapper.bookingToDto(booking);
            bookingDto.setBooker(UserMapper.userToDto(userRepository.getReferenceById(booking.getBookerId())));
            bookingDto.setItem(BookingMapper.itemToBookingItemDto(itemRepository.getReferenceById(booking.getItemId())));
            return bookingDto;
        }).collect(Collectors.toList());

        if (bookingDtos.size() > 0) {
            return bookingDtos;
        }

        log.info("не удалось найти список бронирований");
        throw new NotFoundException("не удалось найти список бронирований");
    }

    @Override
    public List<BookingDto> getOwnerAllBookings(Long userId, BookingStatus bookingStatus, Pageable pageable) {
        List<Long> idsList = itemRepository.findAllByOwner(userId).stream().map(Item::getId).collect(Collectors.toList());
        List<Booking> bookings;

        LocalDateTime dt = LocalDateTime.now();
        switch (bookingStatus) {
            case ALL:
                bookings = bookingRepository.findAllByItemIdInOrderByStartDesc(idsList, pageable)
                        .stream().collect(Collectors.toList());
                break;
            case PAST:
                bookings = bookingRepository.findItemsInPast(idsList, dt);
                break;
            case CURRENT:
                bookings = bookingRepository.findItemsInCurrent(idsList, dt);
                break;
            case FUTURE:
                bookings = bookingRepository.findItemsInFuture(idsList, dt);
                break;
            default:
                bookings = bookingRepository.findItemsByStatus(idsList, bookingStatus);
        }

        List<BookingDto> bookingDtos = bookings.stream().map(booking -> {
            BookingDto bookingDto = BookingMapper.bookingToDto(booking);
            bookingDto.setBooker(UserMapper.userToDto(userRepository.getReferenceById(booking.getBookerId())));
            bookingDto.setItem(BookingMapper.itemToBookingItemDto(itemRepository.getReferenceById(booking.getItemId())));
            return bookingDto;
        }).collect(Collectors.toList());

        if (bookingDtos.size() > 0) {
            return bookingDtos;
        }

        log.info("не удалось найти список бронирований");
        throw new NotFoundException("не удалось найти список бронирований");
    }

    @Override
    public BookingShortDto getLastBooking(Long itemId) {
        return BookingMapper.bookingToShortDto(bookingRepository.getLastBooking(itemId));
    }

    @Override
    public BookingShortDto getNextBooking(Long itemId) {
        return BookingMapper.bookingToShortDto(bookingRepository.getNextBooking(itemId));
    }

    private Boolean checkUserExist(long userId) {
        if (userRepository.existsById(userId)) {
            return true;
        }
        throw new NotFoundException("такого пользователя не существует");
    }

    private Boolean vaildateBooking(BookingDto bookingDto) {
        if (!itemRepository.existsById(bookingDto.getItemId())) {
            log.info("предмет не найден");
            throw new NotFoundException("предмет не найден");
        }
        if (!itemRepository.getReferenceById(bookingDto.getItemId()).getAvailable()) {
            log.info("предмет нельзя забронировать");
            throw new ValidationException("предмет нельзя забронировать");
        }
        return true;
    }
}
