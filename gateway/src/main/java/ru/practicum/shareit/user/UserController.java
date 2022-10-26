package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;


@Controller
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
@Validated
public class UserController {
    private final UserClient userClient;

    @GetMapping("/{userId}")
    public ResponseEntity<Object> get(@PathVariable("userId") Long userId) {
        return userClient.getUser(userId);
    }

    @GetMapping
    public ResponseEntity<Object> getAll() {
        return userClient.getUsers();
    }

    @PostMapping
    public ResponseEntity<Object> create(@RequestBody UserDto userDto) {
        checkDto(userDto);
        return userClient.userItem(userDto);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<Object> update(
            @PathVariable("userId") Long userId,
            @RequestBody UserDto userDto
    ) {
        return userClient.update(userDto, userId);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Object> delete(@PathVariable("userId") Long userId) {
        return userClient.delete(userId);
    }

    private void checkDto(UserDto userDto) {
        if (userDto.getEmail() == null) {
            throw new ValidationException("не указан email пользователя");
        }
        if (!userDto.getEmail().contains("@")) {
            throw new ValidationException("невалидный email");
        }
    }
}
