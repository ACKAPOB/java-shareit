package ru.practicum.shareit.user.service;

import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {
    UserDto createUser(UserDto userDto);

    @Transactional
    UserDto updateUser(UserDto userDto, Long id);

    @Transactional
    UserDto deleteUser(Long id);

    UserDto getUser(Long id);

    List<UserDto> getUsers();
}
