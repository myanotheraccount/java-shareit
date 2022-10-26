package ru.practicum.shareit.request;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;

@RestController
@RequestMapping(path = "/requests")
public class ItemRequestController {
    ItemRequestService itemRequestService;

    public ItemRequestController(ItemRequestService itemRequestService) {
        this.itemRequestService = itemRequestService;
    }

    @PostMapping
    public ItemRequestDto addRequest(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestBody ItemRequestDto itemRequestDto
    ) {
        return itemRequestService.save(itemRequestDto, userId);
    }

    @GetMapping
    public List<ItemRequestDto> getUserRequests(
            @RequestHeader("X-Sharer-User-Id") Long userId
    ) {
        return itemRequestService.getAllUserRequests(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getAllRequests(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestParam(value = "from", required = false, defaultValue = "0")  int from,
            @RequestParam(value = "size", required = false, defaultValue = "50") int size
    ) {
        return itemRequestService.getOtherUserRequests(userId, PageRequest.of(from / size, size, Sort.Direction.DESC, "created"));
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getUserRequest(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @PathVariable("requestId") Long requestId
    ) {
        return itemRequestService.getById(userId, requestId);
    }
}
