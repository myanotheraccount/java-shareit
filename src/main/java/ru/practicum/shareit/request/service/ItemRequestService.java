package ru.practicum.shareit.request.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDto save(ItemRequestDto itemRequestDto, Long userId);

    List<ItemRequestDto> getAllUserRequests(Long userId);

    List<ItemRequestDto> getOtherUserRequests(Long userId, Pageable pageable);

    ItemRequestDto getById(Long userId, Long requestId);
}
