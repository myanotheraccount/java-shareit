package ru.practicum.shareit.booking.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingItemDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemMapper;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.service.UserMapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Slf4j
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
    public BookingDto save(BookingDto bookingDto, Long userId) {
        if (vaildateUser(userId) && vaildateBooking(bookingDto)) {
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
        log.info("не удалось сохранить бронирование");
        throw new ValidationException("не удалось сохранить бронирование");
    }

    @Override
    public BookingDto approveBooking(Long userId, Long bookingId, Boolean isApproved) {
        Booking booking = bookingRepository.getReferenceById(bookingId);
        Item item = itemRepository.getReferenceById(booking.getItemId());
        if (booking.getStatus().equals(BookingStatus.APPROVED)) {
            log.info("бронирование уже подтверждено");
            throw new ValidationException("бронирование уже подтверждено");
        }
        if (vaildateUser(userId) && Objects.equals(item.getOwner(), userId)) {
            booking.setStatus(isApproved ? BookingStatus.APPROVED : BookingStatus.REJECTED);
            bookingRepository.save(booking);
            BookingDto bookingDto = BookingMapper.bookingToDto(booking);
            bookingDto.setItem(ItemMapper.itemToDto(item));
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
            if (vaildateUser(userId) && (booking.getBookerId().equals(userId) || item.getOwner().equals(userId))) {
                BookingDto bookingDto = BookingMapper.bookingToDto(booking);
                bookingDto.setItem(ItemMapper.itemToDto(item));
                bookingDto.setBooker(UserMapper.userToDto(userRepository.getReferenceById(booking.getBookerId())));
                return bookingDto;
            }
        }
        log.info("не удалось найти бронирование");
        throw new NotFoundException("не удалось найти бронирование");
    }

    @Override
    public List<BookingDto> getAllBookings(Long userId, String status) {
        List<Booking> bookings;
        BookingStatus bookingStatus = parseStatus(status);
        switch (bookingStatus) {
            case ALL:
                bookings = bookingRepository.findAllByBookerIdOrderByStartDesc(userId);
                break;
            case PAST:
                bookings = bookingRepository.findAllByBookerIdAndEndBeforeOrderByStartDesc(userId, LocalDateTime.now());
                break;
            case CURRENT:
                LocalDateTime dt = LocalDateTime.now();
                bookings = bookingRepository.findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(userId, dt, dt);
                break;
            case FUTURE:
                bookings = bookingRepository.findAllByBookerIdAndStartAfterOrderByStartDesc(userId, LocalDateTime.now());
                break;
            default:
                bookings = bookingRepository.findAllByBookerIdAndStatusEqualsOrderByStartDesc(userId, bookingStatus);
        }
        List<BookingDto> bookingDtos = bookings.stream().map(booking -> {
            BookingDto bookingDto = BookingMapper.bookingToDto(booking);
            bookingDto.setBooker(UserMapper.userToDto(userRepository.getReferenceById(booking.getBookerId())));
            bookingDto.setItem(ItemMapper.itemToDto(itemRepository.getReferenceById(booking.getItemId())));
            return bookingDto;
        }).collect(Collectors.toList());

        if (bookingDtos.size() > 0) {
            return bookingDtos;
        }

        log.info("не удалось найти список бронирований");
        throw new NotFoundException("не удалось найти список бронирований");
    }

    @Override
    public List<BookingDto> getOwnerAllBookings(Long userId, String status) {
        List<Long> idsList = itemRepository.findAllByOwner(userId).stream().map(Item::getId).collect(Collectors.toList());
        List<Booking> bookings;

        BookingStatus bookingStatus = parseStatus(status);
        switch (bookingStatus) {
            case ALL:
                bookings = bookingRepository.findAllByItemIdInOrderByStartDesc(idsList);
                break;
            case PAST:
                bookings = bookingRepository.findAllByItemIdInAndEndBeforeOrderByStartDesc(idsList, LocalDateTime.now());
                break;
            case CURRENT:
                LocalDateTime dt = LocalDateTime.now();
                bookings = bookingRepository.findAllByItemIdInAndStartBeforeAndEndAfterOrderByStartDesc(idsList, dt, dt);
                break;
            case FUTURE:
                bookings = bookingRepository.findAllByItemIdInAndStartAfterOrderByStartDesc(idsList, LocalDateTime.now());
                break;
            default:
                bookings = bookingRepository.findAllByItemIdInAndStatusEqualsOrderByStartDesc(idsList, bookingStatus);
        }

        List<BookingDto> bookingDtos = bookings.stream().map(booking -> {
            BookingDto bookingDto = BookingMapper.bookingToDto(booking);
            bookingDto.setBooker(UserMapper.userToDto(userRepository.getReferenceById(booking.getBookerId())));
            bookingDto.setItem(ItemMapper.itemToDto(itemRepository.getReferenceById(booking.getItemId())));
            return bookingDto;
        }).collect(Collectors.toList());

        if (bookingDtos.size() > 0) {
            return bookingDtos;
        }

        log.info("не удалось найти список бронирований");
        throw new NotFoundException("не удалось найти список бронирований");
    }

    @Override
    public BookingItemDto getLastBooking(Long itemId) {
        return BookingMapper.bookingToItemDto(
                bookingRepository.findFirstByItemIdAndStatusAndEndIsBeforeOrderByStartDesc(
                        itemId,
                        BookingStatus.APPROVED,
                        LocalDateTime.now()
                )
        );
    }

    @Override
    public BookingItemDto getNextBooking(Long itemId) {
        return BookingMapper.bookingToItemDto(
                bookingRepository.findFirstByItemIdAndStatusAndStartIsAfterOrderByStartDesc(
                        itemId,
                        BookingStatus.APPROVED,
                        LocalDateTime.now()
                )
        );
    }

    private BookingStatus parseStatus(String status) {
        try {
            return BookingStatus.valueOf(status);
        } catch (Exception e) {
            throw new ValidationException("Unknown state: " + status);
        }
    }

    private Boolean vaildateUser(long userId) {
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
        if (bookingDto.getEnd().isBefore(LocalDateTime.now()) ||
                bookingDto.getStart().isBefore(LocalDateTime.now()) ||
                bookingDto.getEnd().isBefore(bookingDto.getStart())) {
            log.info("время окончания бронирования не может быть раньше времени начала");
            throw new ValidationException("время окончания бронирования не может быть раньше времени начала");
        }
        return true;
    }
}
