package ru.practicum.shareit.user.service;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Service
public interface UserService {
    User get(Long userId);

    List<User> getAll();

    User save(UserDto userDto);

    User update(UserDto userDto, Long userId);

    void delete(Long userId);
}
