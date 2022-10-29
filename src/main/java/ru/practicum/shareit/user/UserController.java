package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@Slf4j
@Validated
@RequestMapping(path = "/users")
public class UserController {

    private final UserService userService;
    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping()
    public UserDto createUser(@Validated({Create.class}) @RequestBody UserDto userDto) {
        log.info("Post userDto Email = {}, Name = {}", userDto.getEmail(), userDto.getName());
        return userService.createUser(userDto);
    }

    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public UserDto updateUser(@Validated({Update.class}) @RequestBody UserDto userDto, @PathVariable long id) {
        log.info("Put userDto Email = {}, Name = {}", userDto.getEmail(), userDto.getName());
        return userService.updateUser(userDto, id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteUser(@PathVariable long id) {
        log.info("Delete user id = {} ", id);
        userService.deleteUser(id);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public UserDto getUser(@PathVariable long id) {
        log.info("Get user id = {} ", id);
        return userService.getUser(id);
    }

    @GetMapping()
    @ResponseStatus(HttpStatus.OK)
    public List<UserDto> getUsers() {
        log.info("Get users");
        return userService.getUsers();
    }

}
