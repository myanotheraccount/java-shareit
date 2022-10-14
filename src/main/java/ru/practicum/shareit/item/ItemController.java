package ru.practicum.shareit.item;

import org.springframework.data.domain.PageRequest;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping("/items")
@Validated
public class ItemController {
    private ItemService itemService;

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @GetMapping("/{itemId}")
    public ItemDto get(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @PathVariable("itemId") Long itemId
    ) {
        return itemService.get(itemId, userId);
    }

    @GetMapping
    public List<ItemDto> getAll(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestParam(value = "from", required = false, defaultValue = "0") @PositiveOrZero Integer from,
            @RequestParam(value = "size", required = false, defaultValue = "50") Integer size
    ) {
        return itemService.getAll(userId, PageRequest.of(from / size, size));
    }

    @GetMapping("/search")
    public List<ItemDto> search(
            @RequestParam("text") String text,
            @RequestParam(value = "from", required = false, defaultValue = "0") @PositiveOrZero Integer from,
            @RequestParam(value = "size", required = false, defaultValue = "50") Integer size
    ) {
        return itemService.find(text.toLowerCase(), PageRequest.of(from / size, size));
    }

    @PostMapping
    public ItemDto postItem(
            @RequestBody ItemDto item,
            @RequestHeader("X-Sharer-User-Id") Long userId
    ) {
        return itemService.save(item, userId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @PathVariable("itemId") Long itemId,
            @RequestBody ItemDto item
    ) {
        return itemService.update(item, itemId, userId);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto saveComment(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @PathVariable("itemId") Long itemId,
            @RequestBody CommentDto commentDto
    ) {
        return itemService.addComment(userId, itemId, commentDto);
    }
}
