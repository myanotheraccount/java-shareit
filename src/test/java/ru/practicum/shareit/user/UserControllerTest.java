package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.practicum.shareit.user.UserController;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

public class UserControllerTest {
    private UserService userService;

    private UserController userController;

    @BeforeEach
    void beforeEach() {
        this.userService = Mockito.mock(UserService.class);
        this.userController = new UserController(userService);
    }

    @Test
    void save() {
        when(userService.save(Mockito.any(UserDto.class))).thenReturn(new UserDto());
        assertNotNull(userController.create(new UserDto()));
    }

    @Test
    void get() {
        when(userService.get(Mockito.anyLong())).thenReturn(new UserDto());
        assertNotNull(userController.get(1L));
    }

    @Test
    void getAll() {
        when(userService.getAll()).thenReturn(List.of(new UserDto()));
        assertEquals(userController.getAll().size(), 1);
    }

    @Test
    void update() {
        when(userService.update(Mockito.any(UserDto.class), Mockito.anyLong())).thenReturn(new UserDto());
        assertNotNull(userController.update(1L, new UserDto()));
    }

    @Test
    void delete() {
        userController.delete(1L);
        Mockito.verify(userService,Mockito.times(1)).delete(1L);
    }
}
