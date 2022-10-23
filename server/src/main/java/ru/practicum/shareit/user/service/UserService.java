package ru.practicum.shareit.user.service;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

@Service
public interface UserService {
    UserDto get(Long userId);

    List<UserDto> getAll();

    UserDto save(UserDto userDto);

    UserDto update(UserDto userDto, Long userId);

    void delete(Long userId);
}
