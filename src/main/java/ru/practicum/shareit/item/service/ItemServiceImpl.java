package ru.practicum.shareit.item.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.dao.ItemDao;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ItemServiceImpl implements ItemService {
    @Autowired
    ItemMapper itemMapper;
    @Autowired
    ItemDao itemDao;

    public ItemDto save(ItemDto itemDto, Long userId) {
        if (validate(itemDto)) {
            Item item = itemMapper.dtoToItem(itemDto, userId);
            item.setOwner(userId);
            return itemMapper.itemToDto(itemDao.save(item));
        }
        return null;
    }

    public ItemDto get(Long itemId) {
        return itemMapper.itemToDto(itemDao.get(itemId));
    }

    public List<ItemDto> getAll(Long userId) {
        return itemDao.getAll().stream()
                .filter(item -> item.getOwner().equals(userId))
                .map(itemMapper::itemToDto)
                .collect(Collectors.toList());
    }

    public List<ItemDto> find(String text) {
        if (text.isBlank()) return List.of();
        return itemDao.getAll().stream()
                .filter(item ->
                        item.getAvailable()
                                && (item.getName() + " " + item.getDescription()).toLowerCase().contains(text)
                )
                .map(itemMapper::itemToDto)
                .collect(Collectors.toList());
    }

    public ItemDto update(ItemDto itemDto, Long itemId, Long userId) {
        Item item = itemDao.get(itemId);
        if (item.getOwner().equals(userId)) {
            if (itemDto.getName() != null) item.setName(itemDto.getName());
            if (itemDto.getDescription() != null) item.setDescription(itemDto.getDescription());
            if (itemDto.getAvailable() != null) item.setAvailable(itemDto.getAvailable());
            return itemMapper.itemToDto(itemDao.update(item));
        }
        throw new NotFoundException("предмет не принадлежить этому пользователю");
    }

    private boolean validate(ItemDto itemDto) {
        if (itemDto.getAvailable() == null) {
            throw new ValidationException("не указана доступность предмета");
        }
        if (itemDto.getName() == null || itemDto.getName().isBlank()) {
            throw new ValidationException("не указано название предмета");
        }
        if (itemDto.getDescription() == null || itemDto.getDescription().isBlank()) {
            throw new ValidationException("не указано описание предмета");
        }
        return true;
    }
}
