package ru.practicum.shareit.user.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dao.UserStorage;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.exception.alreadyExistsException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.UserMapper;
import ru.practicum.shareit.user.service.UserService;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    long id = 0;
    private final UserStorage userStorage;

    private final UserMapper userMapper;

    @Autowired
    public UserServiceImpl(UserStorage userStorage, UserMapper userMapper) {
        this.userStorage = userStorage;
        this.userMapper = userMapper;
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        if (userStorage.alreadyExists(userDto.getEmail())) {
            throw new alreadyExistsException(
                    "Некорректный запрос, пользователь уже существует  " + userDto.getId() + " , " + userDto.getEmail());
        }
        userStorage.createUser(userMapper.toUser(userDto, userStorage.genId()));
        return userMapper.toUserDto(userStorage.getUser(userDto.getEmail()));
    }

    public UserDto updateUser(UserDto userDto, long id) {
        if (userStorage.alreadyExists(userDto.getEmail()))  {
            throw new alreadyExistsException(
                    "Некорректный запрос " + userDto);
        }
        userStorage.updateUser(userMapper.toUser(userDto, id));
        return userMapper.toUserDto(userStorage.getUser(id));
    }

    public void deleteUser (long id) {
        if (!userStorage.alreadyExists(id)) {
            throw new alreadyExistsException(
                    "Некорректный запрос, пользователь не существует " );
        }
        userStorage.deleteUser(userStorage.getUser(id));
    }

    public UserDto getUser (long id) {
        if (!userStorage.alreadyExists(id)) {
            throw new alreadyExistsException(
                    "Некорректный запрос, пользователь не существует");
        }
        return userMapper.toUserDto(userStorage.getUser(id));
    }

    public List<UserDto> getUsers () {
        List<UserDto> userDtoList = new ArrayList<>();
        for (User out : userStorage.getUsers()) {
            userDtoList.add(userMapper.toUserDto(out));
        }
        return userDtoList;
    }
}
