package ru.practicum.shareit.item.dao;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Component
public class ItemDaoImpl implements ItemDao {
    private final HashMap<Long, Item> storage = new HashMap<>();
    private Long uId = 0L;

    private Long getUid() {
        return ++uId;
    }

    @Override
    public Item get(Long itemId) {
        return storage.get(itemId);
    }

    @Override
    public List<Item> getAll() {
        return new ArrayList<>(storage.values());
    }

    @Override
    public Item save(Item item) {
        item.setId(getUid());
        storage.put(item.getId(), item);
        return item;
    }

    @Override
    public Item update(Item item) {
        storage.put(item.getId(), item);
        return item;
    }

    @Override
    public void delete(Long itemId) {
        storage.remove(itemId);
    }

}
