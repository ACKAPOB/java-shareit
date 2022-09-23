package ru.practicum.shareit.user.model;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.dto.UserDto;

@Component
public class UserMapper {

    public User toUser (UserDto userDto, long id) {
        return new User(id, userDto.getName(), userDto.getEmail());
    }

    public UserDto toUserDto (User user) {
        return new UserDto(user.getId(), user.getName(), user.getEmail());
    }
}
