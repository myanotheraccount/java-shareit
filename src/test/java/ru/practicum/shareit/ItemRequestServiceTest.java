package ru.practicum.shareit;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.request.service.ItemRequestServiceImpl;
import ru.practicum.shareit.user.UserRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ItemRequestServiceTest {
    private ItemRequestService itemRequestService;
    @Mock
    private ItemRequestRepository itemRequestRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRepository itemRepository;

    @BeforeEach
    void beforeEach() {
        this.itemRequestService = new ItemRequestServiceImpl(itemRequestRepository, userRepository, itemRepository);
    }

    @Test
    void save() {
        when(userRepository.existsById(Mockito.anyLong())).thenReturn(true);
        when(itemRequestRepository.save(Mockito.any(ItemRequest.class))).thenReturn(new ItemRequest());
        assertNotNull(itemRequestService.save(new ItemRequestDto(), 1L));
    }

    @Test
    void getAllUserRequests() {
        when(userRepository.existsById(Mockito.anyLong())).thenReturn(true);
        when(itemRequestRepository.findAllByAuthorId(Mockito.anyLong())).thenReturn(List.of(new ItemRequest()));
        Assertions.assertEquals(itemRequestService.getAllUserRequests(1L).size(), 1);
    }

    @Test
    void getOtherUserRequests() {
        Pageable pageable = PageRequest.of(0, 10);
        when(itemRequestRepository.findAll(pageable)).thenReturn(new PageImpl<ItemRequest>(List.of(new ItemRequest())));
        Assertions.assertEquals(itemRequestService.getOtherUserRequests(1L, pageable).size(), 1);
    }

    @Test
    void getById() {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(1L);
        when(userRepository.existsById(1L)).thenReturn(true);
        when(userRepository.existsById(2L)).thenReturn(false);
        when(itemRepository.existsById(1L)).thenReturn(true);
        when(itemRepository.existsById(2L)).thenReturn(false);
        when(itemRequestRepository.getReferenceById(Mockito.anyLong())).thenReturn(itemRequest);
        when(itemRepository.findAllByRequestId(Mockito.anyLong())).thenReturn(List.of(
                new Item(1L, 1L, "name", "description", true, 2L)
        ));

        NotFoundException thrown = assertThrows(NotFoundException.class, () -> itemRequestService.getById(1L, 2L));
        assertEquals(thrown.getMessage(), "запрос не найден");

        NotFoundException thrown1 = assertThrows(NotFoundException.class, () -> itemRequestService.getById(2L, 1L));
        assertEquals(thrown1.getMessage(), "такого пользователя не существует");

        assertEquals(itemRequestService.getById(1L, 1L).getItems().size(), 1);
    }
}
