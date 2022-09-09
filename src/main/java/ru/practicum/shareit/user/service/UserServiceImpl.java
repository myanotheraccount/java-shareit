package ru.practicum.shareit.user.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.user.dao.UserDao;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Objects;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    UserMapper userMapper;
    @Autowired
    UserDao userDao;

    @Override
    public User get(Long userId) {
        return userDao.get(userId);
    }

    @Override
    public List<User> getAll() {
        return userDao.getAll();
    }

    @Override
    public User save(UserDto userDto) {
        if (validate(userDto)) {
            User user = userMapper.dtoToUser(userDto, null);
            return userDao.save(user);
        }
        return null;
    }

    @Override
    public User update(UserDto userDto, Long userId) {
        User user = get(userId);
        if (user != null) {
            if (userDto.getName() != null) user.setName(userDto.getName());
            if (userDto.getEmail() != null && validate(userDto)) user.setEmail(userDto.getEmail());
            userDao.update(user);
        }
        return user;
    }

    @Override
    public void delete(Long userId) {
        userDao.delete(userId);
    }

    private boolean validate(UserDto userDto) {
        if (userDto.getEmail() == null) {
            throw new ValidationException("не указан email пользователя");
        }
        if (!userDto.getEmail().contains("@")) {
            throw new ValidationException("невалидный email");
        }
        if (userDao.getAll().stream().anyMatch(item -> Objects.equals(item.getEmail(), userDto.getEmail()))) {
            throw new RuntimeException("такой email уже существует");
        }
        return true;
    }
}
