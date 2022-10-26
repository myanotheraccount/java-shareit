package ru.practicum.shareit.item.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingMapper;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.CommentRepository;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final CommentRepository commentRepository;

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;

    public ItemServiceImpl(ItemRepository itemRepository, CommentRepository commentRepository, BookingRepository bookingRepository, UserRepository userRepository) {
        this.itemRepository = itemRepository;
        this.commentRepository = commentRepository;
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
    }

    public ItemDto save(ItemDto itemDto, Long userId) {
        checkUserExist(userId);
        Item item = ItemMapper.dtoToItem(itemDto, userId);
        item.setOwner(userId);
        log.info(String.format("добавлен новый предмет у пользователя id = %d", userId));
        return ItemMapper.itemToDto(itemRepository.save(item));
    }

    public ItemDto get(Long itemId, Long userId) {
        try {
            log.info(String.format("найден предмет  id = %d", itemId));
            ItemDto itemDto = ItemMapper.itemToDto(itemRepository.getReferenceById(itemId));
            itemDto.setComments(commentRepository.findAllByItemId(itemId)
                    .stream().map(comment -> {
                        User user = userRepository.getReferenceById(comment.getAuthorId());
                        return CommentMapper.commentToDto(comment, user.getName());
                    }).collect(Collectors.toList()));
            addBookingInfo(itemDto, userId);
            return itemDto;
        } catch (Exception e) {
            throw new NotFoundException("предмет не найден");
        }
    }

    public List<ItemDto> getAll(Long userId, Pageable pageable) {
        log.info(String.format("найден список предметов у пользователя с id = %d", userId));
        return itemRepository.findAll(pageable).stream()
                .filter(item -> item.getOwner().equals(userId))
                .sorted(Comparator.comparing(Item::getId))
                .map(item -> {
                    ItemDto itemDto = ItemMapper.itemToDto(item);
                    addBookingInfo(itemDto, userId);
                    return itemDto;
                }).collect(Collectors.toList());
    }

    public List<ItemDto> find(String text, Pageable pageable) {
        log.info(String.format("поиск предмета по тексту = $s", text));
        return itemRepository.findAll(pageable).stream()
                .filter(item ->
                        item.getAvailable()
                                && (item.getName() + " " + item.getDescription()).toLowerCase().contains(text)
                )
                .map(ItemMapper::itemToDto)
                .collect(Collectors.toList());
    }

    public ItemDto update(ItemDto itemDto, Long itemId, Long userId) {
        Item item = itemRepository.getReferenceById(itemId);
        if (checkUserExist(userId) && item.getOwner().equals(userId)) {
            if (itemDto.getName() != null) {
                item.setName(itemDto.getName());
            }
            if (itemDto.getDescription() != null) {
                item.setDescription(itemDto.getDescription());
            }
            if (itemDto.getAvailable() != null) {
                item.setAvailable(itemDto.getAvailable());
            }
            log.info(String.format("обновлен предмет %d у пользователя %d", itemId, userId));
            return ItemMapper.itemToDto(itemRepository.save(item));
        }
        throw new NotFoundException("предмет не принадлежит этому пользователю");
    }

    private Boolean checkUserExist(long userId) {
        if (userRepository.existsById(userId)) {
            return true;
        }
        throw new NotFoundException("такого пользователя не существует");
    }

    @Override
    public CommentDto addComment(Long userId, Long itemId, CommentDto commentDto) {
        if (bookingRepository.existsBooking(userId, itemId, BookingStatus.APPROVED)) {
            Comment comment = CommentMapper.dtoToComment(commentDto, itemId, userId);
            User user = userRepository.getReferenceById(userId);
            return CommentMapper.commentToDto(commentRepository.save(comment), user.getName());
        }
        log.info("нe удалось добавить комментарий");
        throw new ValidationException("нe удалось добавить комментарий");
    }

    private void addBookingInfo(ItemDto itemDto, Long userId) {
        if (itemDto.getOwner().equals(userId)) {
            Booking lastBooking = bookingRepository.getLastBooking(itemDto.getId());
            Booking nextBooking = bookingRepository.getNextBooking(itemDto.getId());
            if (lastBooking != null) {
                itemDto.setLastBooking(BookingMapper.bookingToShortDto(lastBooking));
            }
            if (nextBooking != null) {
                itemDto.setNextBooking(BookingMapper.bookingToShortDto(nextBooking));
            }
        }
    }
}
