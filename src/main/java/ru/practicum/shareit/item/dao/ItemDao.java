package ru.practicum.shareit.item.dao;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemDao {
    Item get(Long itemId);

    List<Item> getAll();

    Item save(Item item);

    Item update(Item item);

    void delete(Long itemId);
}
