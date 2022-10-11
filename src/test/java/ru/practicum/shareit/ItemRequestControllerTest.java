package ru.practicum.shareit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.request.ItemRequestController;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;


class ItemRequestControllerTest {
    private ItemRequestService itemRequestService;
    private ItemRequestController itemRequestController;

    @BeforeEach
    void beforeEach() {
        this.itemRequestService = Mockito.mock(ItemRequestService.class);
        this.itemRequestController = new ItemRequestController(itemRequestService);
    }

    @Test
    void addRequest() {
        when(itemRequestService.save(Mockito.any(ItemRequestDto.class), Mockito.anyLong())).thenReturn(new ItemRequestDto());
        assertNotNull(itemRequestController.addRequest(1L, new ItemRequestDto()));
    }

    @Test
    void getUserRequests() {
        when(itemRequestService.getAllUserRequests(Mockito.anyLong())).thenReturn(List.of(new ItemRequestDto()));
        assertEquals(itemRequestController.getUserRequests(1L).size(), 1);
    }

    @Test
    void getAllRequests() {
        when(itemRequestService.getOtherUserRequests(Mockito.anyLong(), Mockito.any(Pageable.class))).thenReturn(List.of(new ItemRequestDto()));
        assertEquals(itemRequestController.getAllRequests(1L, 0, 10).size(), 1);
    }

    @Test
    void getUserRequest() {
        when(itemRequestService.getById(Mockito.anyLong(), Mockito.anyLong())).thenReturn(new ItemRequestDto());
        assertNotNull(itemRequestController.getUserRequest(1L, 1L));
    }
}