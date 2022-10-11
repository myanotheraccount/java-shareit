package ru.practicum.shareit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.item.ItemController;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;


class ItemControllerTest {
    private ItemService itemService;
    private ItemController itemController;

    @BeforeEach
    void beforeEach() {
        this.itemService = Mockito.mock(ItemService.class);
        this.itemController = new ItemController(itemService);
    }

    @Test
    void get() {
        when(itemService.get(Mockito.anyLong(), Mockito.anyLong())).thenReturn(new ItemDto());
        assertNotNull(itemController.get(1L, 1L));
    }

    @Test
    void getAll() {
        when(itemService.getAll(Mockito.anyLong(), Mockito.any(Pageable.class))).thenReturn(List.of(new ItemDto()));
        assertEquals(itemController.getAll(1L, 0, 10).size(), 1);
    }

    @Test
    void search() {
        when(itemService.find(Mockito.anyString(), Mockito.any(Pageable.class))).thenReturn(List.of(new ItemDto()));
        assertEquals(itemController.search("test", 0, 10).size(), 1);
    }

    @Test
    void postItem() {
        when(itemService.save(Mockito.any(ItemDto.class), Mockito.anyLong())).thenReturn(new ItemDto());
        assertNotNull(itemController.postItem(new ItemDto(), 1L));
    }

    @Test
    void update() {
        when(itemService.update(Mockito.any(ItemDto.class), Mockito.anyLong(), Mockito.anyLong())).thenReturn(new ItemDto());
        assertNotNull(itemController.update(1L, 1L, new ItemDto()));
    }

    @Test
    void saveComment() {
        when(itemService.addComment(Mockito.anyLong(), Mockito.anyLong(), Mockito.any(CommentDto.class))).thenReturn(new CommentDto());
        assertNotNull(itemController.saveComment(1L, 1L, new CommentDto()));
    }
}