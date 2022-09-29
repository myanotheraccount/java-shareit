package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto save(ItemDto itemDto, Long userId);

    ItemDto get(Long itemId, Long userId);

    List<ItemDto> getAll(Long userId);

    List<ItemDto> find(String text);

    ItemDto update(ItemDto itemDto, Long itemId, Long userId);

    CommentDto addComment(Long userId, Long itemId, CommentDto commentDto);
}
