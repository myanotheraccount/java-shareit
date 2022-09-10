package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/items")
public class ItemController {
    @Autowired
    private ItemServiceImpl itemService;

    @Autowired
    private UserService userService;

    @GetMapping("/{itemId}")
    public ItemDto get(@PathVariable("itemId") Long itemId) {
        return itemService.get(itemId);
    }

    @GetMapping
    public List<ItemDto> getAll(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemService.getAll(userId);
    }

    @GetMapping("/search")
    public List<ItemDto> search(@RequestParam("text") String text) {
        return itemService.find(text.toLowerCase());
    }

    @PostMapping
    public ItemDto postItem(
            @RequestBody ItemDto item,
            @RequestHeader("X-Sharer-User-Id") Long userId
    ) {
        if (userService.get(userId) == null) {
            throw new NotFoundException("пользователь не найден");
        }
        return itemService.save(item, userId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @PathVariable("itemId") Long itemId,
            @RequestBody ItemDto item
    ) {
        if (userService.get(userId) == null) {
            throw new NotFoundException("пользователь не найден");
        }
        return itemService.update(item, itemId, userId);
    }
}
