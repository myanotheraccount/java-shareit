package ru.practicum.shareit.user.service;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.dto.UserDto;

@Service
public class UserMapper {
    public static User dtoToUser(UserDto userDto, Long id) {
        return new User(id, userDto.getName(), userDto.getEmail());
    }

    public static UserDto userToDto(User user) {
        return new UserDto(user.getId(), user.getName(), user.getEmail());
    }
}
