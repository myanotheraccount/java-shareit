package ru.practicum.shareit.user.dao;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Component
public class UserDaoImpl implements UserDao {
    private final HashMap<Long, User> storage = new HashMap<>();
    private Long uId = 0L;

    private Long getUid() {
        return ++uId;
    }

    @Override
    public User get(Long userId) {
        return storage.get(userId);
    }

    @Override
    public List<User> getAll() {
        return new ArrayList<>(storage.values());
    }

    @Override
    public User save(User user) {
        user.setId(getUid());
        storage.put(user.getId(), user);
        return user;
    }

    @Override
    public User update(User user) {
        storage.put(user.getId(), user);
        return user;
    }

    @Override
    public void delete(Long userId) {
        storage.remove(userId);
    }
}
