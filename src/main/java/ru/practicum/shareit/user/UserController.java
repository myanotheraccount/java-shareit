package ru.practicum.shareit.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping(path = "/users")
public class UserController {
    @Autowired
    UserService userService;

    @GetMapping("/{userId}")
    public User get(@PathVariable("userId") Long userId) {
        return userService.get(userId);
    }

    @GetMapping
    public List<User> getAll() {
        return userService.getAll();
    }

    @PostMapping
    public User create(@RequestBody UserDto userDto) {
        return userService.save(userDto);
    }

    @PatchMapping("/{userId}")
    public User update(
            @PathVariable("userId") Long userId,
            @RequestBody UserDto userDto
    ) {
        return userService.update(userDto, userId);
    }

    @DeleteMapping("/{userId}")
    public void delete(@PathVariable("userId") Long userId) {
        userService.delete(userId);
    }
}
