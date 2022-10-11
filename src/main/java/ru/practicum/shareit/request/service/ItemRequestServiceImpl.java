package ru.practicum.shareit.request.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;

    private final UserRepository userRepository;

    private final ItemRepository itemRepository;

    public ItemRequestServiceImpl(ItemRequestRepository itemRequestRepository, UserRepository userRepository, ItemRepository itemRepository) {
        this.itemRequestRepository = itemRequestRepository;
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
    }

    @Override
    public ItemRequestDto save(ItemRequestDto itemRequestDto, Long userId) {
        checkUserExist(userId);
        ItemRequest itemRequest = ItemRequestMapper.dtoToRequest(itemRequestDto, userId);
        itemRequest.setCreated(LocalDateTime.now());
        log.info(String.format("добавлен новый запрос от пользователя id = %d", userId));
        return ItemRequestMapper.requestToDto(itemRequestRepository.save(itemRequest));
    }

    @Override
    public List<ItemRequestDto> getAllUserRequests(Long userId) {
        checkUserExist(userId);
        log.info("список запросов пользователя userId = %d", userId);
        return itemRequestRepository.findAllByAuthorId(userId).stream().map(itemRequest -> {
            ItemRequestDto itemRequestDto = ItemRequestMapper.requestToDto(itemRequest);
            itemRequestDto.setItems(getItems(itemRequestDto.getId()));
            return itemRequestDto;
        }).collect(Collectors.toList());
    }

    @Override
    public List<ItemRequestDto> getOtherUserRequests(Long userId, Pageable pageable) {
        log.info("постраничный список пользователей");
        return itemRequestRepository.findAll(pageable)
                .getContent().stream().map(itemRequest -> {
                    ItemRequestDto itemRequestDto = ItemRequestMapper.requestToDto(itemRequest);
                    itemRequestDto.setItems(getItems(itemRequestDto.getId()));
                    return itemRequestDto;
                }).filter(itemRequestDto -> !Objects.equals(itemRequestDto.getAuthorId(), userId))
                .collect(Collectors.toList());
    }

    @Override
    public ItemRequestDto getById(Long userId, Long requestId) {
        if (checkUserExist(userId) && itemRepository.existsById(requestId)) {
            ItemRequestDto itemRequestDto = ItemRequestMapper.requestToDto(itemRequestRepository.getReferenceById(requestId));
            itemRequestDto.setItems(getItems(itemRequestDto.getId()));
            return itemRequestDto;
        }
        log.info("запрос не найден");
        throw new NotFoundException("запрос не найден");
    }

    private Boolean checkUserExist(long userId) {
        if (userRepository.existsById(userId)) {
            return true;
        }
        throw new NotFoundException("такого пользователя не существует");
    }

    private List<ItemRequestDto.Item> getItems(Long requestId) {
        List<Item> items = itemRepository.findAllByRequestId(requestId);
        return items.stream().map(item -> new ItemRequestDto.Item(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getRequestId()
        )).collect(Collectors.toList());
    }
}
