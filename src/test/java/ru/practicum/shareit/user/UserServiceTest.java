package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    private UserService userService;
    @Mock
    private UserRepository userRepository;

    @BeforeEach
    void beforeEach() {
        this.userService = new UserServiceImpl(userRepository);
    }

    @Test
    void saveInvalidUserEmail() {
        UserDto userDto = new UserDto();

        ValidationException exception2 = assertThrows(ValidationException.class, () -> userService.save(userDto));
        assertEquals(exception2.getMessage(), "не указан email пользователя");

        userDto.setEmail("test");

        ValidationException exception1 = assertThrows(ValidationException.class, () -> userService.save(userDto));
        assertEquals(exception1.getMessage(), "невалидный email");
    }

    @Test
    void saveUser() {
        when(userRepository.save(Mockito.any(User.class))).thenReturn(new User(1L, "test", "test@test.ru"));
        UserDto userDto = userService.save(new UserDto(null, "test", "test@test.ru"));
        assertEquals(userDto.getId(), 1);
    }

    @Test
    void getInvalidUser() {
        NotFoundException thrown = assertThrows(NotFoundException.class, () -> userService.get(1L));
        assertEquals(thrown.getMessage(), "пользователь не найден");
    }

    @Test
    void getUser() {
        when(userRepository.getReferenceById(Mockito.anyLong())).thenReturn(new User(1L, "test", "test@test.ru"));
        assertEquals(userService.get(1L).getEmail(), "test@test.ru");
    }

    @Test
    void getAllUsers() {
        when(userRepository.findAll()).thenReturn(List.of(
                new User(1L, "test", "test@test.ru"),
                new User(2L, "test", "test@test.ru")
        ));
        assertEquals(userService.getAll().size(), 2);
    }

    @Test
    void updateUser() {
        when(userRepository.getReferenceById(Mockito.anyLong()))
                .thenReturn(new User(1L, "test", "test@test.ru"));
        UserDto userDto = userService.update(new UserDto(null, "test2", "test2@test.ru"), 1L);
        assertEquals(userDto.getName(), "test2");
        assertEquals(userDto.getEmail(), "test2@test.ru");
    }

    @Test
    void delete() {
        userService.delete(1L);
    }
}
