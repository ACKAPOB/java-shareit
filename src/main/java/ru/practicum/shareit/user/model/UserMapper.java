package ru.practicum.shareit.user.model;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.dto.UserDto;

@Component
@Slf4j
public class UserMapper {

    public User toUser(UserDto userDto, long id) {
        return new User(id, userDto.getName(), userDto.getEmail());
    }

    public UserDto toUserDto(User user) {
        return new UserDto(user.getId(), user.getName(), user.getEmail());
    }

    public User toUserDB(UserDto userDto) {
        log.info("Преобразование userDto в user " + userDto);
        return new User(userDto.getId(), userDto.getName(), userDto.getEmail());
    }

    public UserDto toUserDtoDB(User user) {
        return new UserDto(user.getId(), user.getName(), user.getEmail());
    }
}
