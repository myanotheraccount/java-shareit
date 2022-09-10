package ru.practicum.shareit.item.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.dao.ItemDao;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ItemServiceImpl implements ItemService {
    private final ItemDao itemDao;

    public ItemServiceImpl(ItemDao itemDao) {
        this.itemDao = itemDao;
    }

    public ItemDto save(ItemDto itemDto, Long userId) {
        if (validate(itemDto)) {
            Item item = ItemMapper.dtoToItem(itemDto, userId);
            item.setOwner(userId);
            log.info(String.format("добавлен новый предмет у пользователя id = %d", userId));
            return ItemMapper.itemToDto(itemDao.save(item));
        }
        return null;
    }

    public ItemDto get(Long itemId) {
        log.info(String.format("найден предмет  id = %d", itemId));
        return ItemMapper.itemToDto(itemDao.get(itemId));
    }

    public List<ItemDto> getAll(Long userId) {
        log.info(String.format("найден список предметов у пользователя с id = %d", userId));
        return itemDao.getAll().stream()
                .filter(item -> item.getOwner().equals(userId))
                .map(ItemMapper::itemToDto)
                .collect(Collectors.toList());
    }

    public List<ItemDto> find(String text) {
        log.info(String.format("поиск предмета по тексту = $s", text));
        if (text.isBlank()) {
            return List.of();
        }
        return itemDao.getAll().stream()
                .filter(item ->
                        item.getAvailable()
                                && (item.getName() + " " + item.getDescription()).toLowerCase().contains(text)
                )
                .map(ItemMapper::itemToDto)
                .collect(Collectors.toList());
    }

    public ItemDto update(ItemDto itemDto, Long itemId, Long userId) {
        Item item = itemDao.get(itemId);
        if (item.getOwner().equals(userId)) {
            if (itemDto.getName() != null) {
                item.setName(itemDto.getName());
            }
            if (itemDto.getDescription() != null) {
                item.setDescription(itemDto.getDescription());
            }
            if (itemDto.getAvailable() != null) {
                item.setAvailable(itemDto.getAvailable());
            }
            log.info(String.format("обновлен предмет %d у пользователя %d", itemId, userId));
            return ItemMapper.itemToDto(itemDao.update(item));
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
