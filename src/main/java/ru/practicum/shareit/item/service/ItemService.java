package ru.practicum.shareit.item.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto save(ItemDto itemDto, Long userId);

    ItemDto get(Long itemId, Long userId);

    List<ItemDto> getAll(Long userId, Pageable pageable);

    List<ItemDto> find(String text, Pageable pageable);

    ItemDto update(ItemDto itemDto, Long itemId, Long userId);

    CommentDto addComment(Long userId, Long itemId, CommentDto commentDto);
}
