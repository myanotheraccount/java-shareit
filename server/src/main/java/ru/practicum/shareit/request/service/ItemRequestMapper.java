package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.dto.ItemRequestDto;

public class ItemRequestMapper {
    public static ItemRequestDto requestToDto(ItemRequest itemRequest) {
        return new ItemRequestDto(
                itemRequest.getId(),
                itemRequest.getAuthorId(),
                itemRequest.getDescription(),
                itemRequest.getCreated(),
                null
        );
    }

    public static ItemRequest dtoToRequest(ItemRequestDto itemRequestDto, Long userId) {
        return new ItemRequest(
                itemRequestDto.getId(),
                userId,
                itemRequestDto.getDescription(),
                null
        );
    }
}
