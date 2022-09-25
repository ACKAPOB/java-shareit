package ru.practicum.shareit.user.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dao.UserStorage;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.exception.AlreadyExistsException;
import ru.practicum.shareit.user.model.UserMapper;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private long id = 0;
    private final UserStorage userStorage;
    private final UserMapper userMapper;

    @Override
    public UserDto createUser(UserDto userDto) {
        log.info("Запрос на добавление " + userDto);
        if (userStorage.alreadyExists(userDto.getEmail())) {
            throw new AlreadyExistsException(
                    "Некорректный запрос, пользователь уже существует  " + userDto.getId() + " , " + userDto.getEmail());
        }
        userStorage.createUser(userMapper.toUser(userDto, userStorage.genId()));
        return userMapper.toUserDto(userStorage.getUser(userDto.getEmail()));
    }

    public UserDto updateUser(UserDto userDto, long id) {
        log.info("Запрос на обновление " + userDto + " , id пользователя " + id);
        if (userStorage.alreadyExists(userDto.getEmail())) {
            throw new AlreadyExistsException(
                    "Некорректный запрос " + userDto);
        }
        if (userDto.getName() != null) {
            userStorage.getUser(id).setName(userDto.getName());
        }
        if (userDto.getEmail() != null) {
            userStorage.getUser(id).setEmail(userDto.getEmail());
        }
        return userMapper.toUserDto(userStorage.getUser(id));
    }

    public void deleteUser(long id) {
        log.info("Запрос на удаление пользователя " + id);
        if (!userStorage.alreadyExists(id)) {
            throw new AlreadyExistsException(
                    "Некорректный запрос, пользователь не существует ");
        }
        userStorage.deleteUser(userStorage.getUser(id));
    }

    public UserDto getUser(long id) {
        if (!userStorage.alreadyExists(id)) {
            throw new AlreadyExistsException(
                    "Некорректный запрос, пользователь не существует");
        }
        return userMapper.toUserDto(userStorage.getUser(id));
    }

    public List<UserDto> getUsers() {
        return userStorage.getUsers().stream().map(userMapper::toUserDto).collect(toList());
    }
}
