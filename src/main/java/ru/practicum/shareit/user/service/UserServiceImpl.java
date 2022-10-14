package ru.practicum.shareit.user.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {
    private final UserRepository repository;

    public UserServiceImpl(UserRepository repository) {
        this.repository = repository;
    }

    @Override
    public UserDto get(Long userId) {
        try {
            log.info(String.format("найден пользователь с id = %d", userId));
            return UserMapper.userToDto(repository.getReferenceById(userId));
        } catch (Exception e) {
            throw new NotFoundException("пользователь не найден");
        }
    }

    @Override
    public List<UserDto> getAll() {
        log.info("найдены все пользователи");
        return repository.findAll().stream().map(UserMapper::userToDto).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public UserDto save(UserDto userDto) {
        validate(userDto);
        User user = UserMapper.dtoToUser(userDto, null);
        log.info("добавлен новый пользователь");
        return UserMapper.userToDto(repository.save(user));
    }

    @Override
    @Transactional
    public UserDto update(UserDto userDto, Long userId) {
        UserDto user = get(userId);
        if (user != null) {
            if (userDto.getName() != null) {
                user.setName(userDto.getName());
            }
            if (userDto.getEmail() != null && validate(userDto)) {
                user.setEmail(userDto.getEmail());
            }
            repository.save(UserMapper.dtoToUser(user, userId));
        }
        log.info(String.format("обновлены данные пользователя с id = %d", userId));
        return user;
    }

    @Override
    public void delete(Long userId) {
        repository.deleteByUserId(userId);
        log.info(String.format("удален пользователь с id = %d", userId));
    }

    private boolean validate(UserDto userDto) {
        if (userDto.getEmail() == null) {
            throw new ValidationException("не указан email пользователя");
        }
        if (!userDto.getEmail().contains("@")) {
            throw new ValidationException("невалидный email");
        }
        return true;
    }
}
