package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.CommentRepository;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ItemServiceTest {
    private ItemService itemService;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private UserRepository userRepository;

    @BeforeEach
    void beforeEach() {
        this.itemService = new ItemServiceImpl(itemRepository, commentRepository, bookingRepository, userRepository);
    }

    @Test
    void saveInvalidItem() {
        when(userRepository.existsById(1L)).thenReturn(true);
        when(userRepository.existsById(2L)).thenReturn(false);

        ItemDto itemDto = new ItemDto();

        NotFoundException exceptionUser = assertThrows(NotFoundException.class, () ->
                itemService.save(itemDto, 2L)
        );
        assertEquals(exceptionUser.getMessage(), "такого пользователя не существует");

        ValidationException exceptionAvailable = assertThrows(ValidationException.class, () ->
                itemService.save(itemDto, 1L)
        );
        assertEquals(exceptionAvailable.getMessage(), "не указана доступность предмета");
        itemDto.setAvailable(true);

        ValidationException exceptionName = assertThrows(ValidationException.class, () ->
                itemService.save(itemDto, 1L)
        );
        assertEquals(exceptionName.getMessage(), "не указано название предмета");
        itemDto.setName("name");

        ValidationException exceptionDesc = assertThrows(ValidationException.class, () ->
                itemService.save(itemDto, 1L)
        );
        assertEquals(exceptionDesc.getMessage(), "не указано описание предмета");
    }

    @Test
    void saveItem() {
        when(userRepository.existsById(Mockito.anyLong())).thenReturn(true);
        when(itemRepository.save(Mockito.any(Item.class))).thenAnswer(i -> {
            Item item = new Item();
            item.setId(1L);
            return item;
        });
        ItemDto itemDto = new ItemDto();
        itemDto.setAvailable(true);
        itemDto.setName("name");
        itemDto.setDescription("description");

        assertEquals(itemService.save(itemDto, 1L).getId(), 1L);
    }

    @Test
    void getItem() {
        when(itemRepository.getReferenceById(1L)).thenAnswer(i -> {
            Item item = new Item();
            item.setOwner(2L);
            item.setId(1L);
            return item;
        });
        when(commentRepository.findAllByItemId(Mockito.anyLong())).thenAnswer(i -> {
            Comment comment = new Comment();
            comment.setAuthorId(2L);
            return List.of(comment, comment);
        });
        when(userRepository.getReferenceById(Mockito.anyLong())).thenReturn(new User());
        when(bookingRepository.getLastBooking(Mockito.anyLong())).thenReturn(new Booking());
        when(bookingRepository.getNextBooking(Mockito.anyLong())).thenReturn(new Booking());

        NotFoundException thrown = assertThrows(NotFoundException.class, () -> itemService.get(2L, 2L));
        assertEquals(thrown.getMessage(), "предмет не найден");

        ItemDto itemDto = itemService.get(1L, 2L);

        assertNotNull(itemDto.getLastBooking());
        assertNotNull(itemDto.getNextBooking());
        Mockito.verify(userRepository, Mockito.times(2)).getReferenceById(2L);
    }

    @Test
    void getAll() {
        when(itemRepository.findAll(Mockito.any(Pageable.class))).thenAnswer(i -> {
            Item item = new Item();
            item.setId(1L);
            item.setOwner(2L);
            return new PageImpl<>(List.of(item));
        });
        List<ItemDto> itemDtos = itemService.getAll(2L, PageRequest.of(0, 10));
        Mockito.verify(bookingRepository, Mockito.times(1)).getNextBooking(1L);
        Mockito.verify(bookingRepository, Mockito.times(1)).getLastBooking(1L);
        assertEquals(itemDtos.size(), 1);
    }

    @Test
    void find() {
        assertEquals(itemService.find("", null).size(), 0);
        when(itemRepository.findAll(Mockito.any(Pageable.class))).thenAnswer(i -> {
            Item item = new Item();
            item.setName("item name");
            item.setDescription("some description");
            item.setAvailable(true);
            return new PageImpl<>(List.of(item));
        });
        assertEquals(itemService.find("text", PageRequest.of(0, 10)).size(), 0);
        assertEquals(itemService.find("some", PageRequest.of(0, 10)).size(), 1);
    }

    @Test
    void update() {
        when(userRepository.existsById(Mockito.anyLong())).thenReturn(true);
        when(itemRepository.getReferenceById(Mockito.anyLong())).thenAnswer(i -> {
            Item item = new Item();
            item.setOwner(2L);
            return item;
        });
        when(itemRepository.save(Mockito.any(Item.class))).thenAnswer(i -> {
            Item item = new Item();
            item.setId(1L);
            item.setOwner(2L);
            return item;
        });
        ItemDto itemDto = new ItemDto();

        NotFoundException thrown = assertThrows(NotFoundException.class, () -> itemService.update(itemDto, 1L, 1L));
        assertEquals(thrown.getMessage(), "предмет не принадлежит этому пользователю");

        itemDto.setName("test");
        itemDto.setDescription("description");
        itemDto.setAvailable(false);
        assertNotNull(itemService.update(itemDto, 1L, 2L));

        Item item = new Item();
        item.setOwner(2L);
    }

    @Test
    void addComment() {
        when(userRepository.getReferenceById(Mockito.anyLong())).thenReturn(new User());
        when(commentRepository.save(Mockito.any(Comment.class))).thenReturn(new Comment());
        when(bookingRepository.existsBooking(Mockito.anyLong(), Mockito.anyLong(), Mockito.any(BookingStatus.class))).thenReturn(true);
        ValidationException thrown = assertThrows(ValidationException.class, () ->
                itemService.addComment(1L, 1L, new CommentDto(null, "", null, null))
        );
        assertEquals(thrown.getMessage(), "нe удалось добавить комментарий");

        CommentDto commentDto = new CommentDto(null, "text", null, null);
        assertNotNull(itemService.addComment(1L, 1L, commentDto));
    }
}
