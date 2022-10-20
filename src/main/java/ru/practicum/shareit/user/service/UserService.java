package ru.practicum.shareit.user.service;

import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {
    UserDto createUser(UserDto userDto);

    @Transactional
    UserDto updateUser(UserDto userDto, long id);

    @Transactional
    void deleteUser(long id);

    UserDto getUser(long id);

    List<UserDto> getUsers();
}
