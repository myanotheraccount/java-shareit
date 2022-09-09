package ru.practicum.shareit.user.dao;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserDao {
    User get(Long userId);

    List<User> getAll();

    User save(User user);

    User update(User user);

    void delete(Long userId);
}
