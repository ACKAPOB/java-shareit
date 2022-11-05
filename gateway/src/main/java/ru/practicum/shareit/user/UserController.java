package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.Valid;

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping(path = "/users")
public class UserController {
    UserClient userClient;

    @Autowired
    public UserController(UserClient userClient) {
        this.userClient = userClient;
    }

    @PostMapping()
    public ResponseEntity<Object> createUserGateway(@Valid @RequestBody UserDto userDto) {
        log.info("create user, userDto {}", userDto);
        return userClient.createUser(userDto);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object>  getUserGateWay(@PathVariable Long id) {
        log.info("UserController.getUserGateWay Get user id = {} ", id);
        return userClient.getUser(id);
    }

    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> updateUserGateWay(@RequestBody UserDto userDto, @PathVariable Long id) {
        log.info("UserController.updateUserGateWay userDto Email = {}, Name = {}", userDto.getEmail(), userDto.getName());
        return userClient.updateUser(userDto, id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> deleteUserGateway(@PathVariable Long id) {
        log.info("UserController.deleteUserGateway user id = {} ", id);
        return userClient.deleteUser(id);
    }

    @GetMapping()
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> getUsersGateWay() {
        log.info("UserController.getUsersGateWay");
        return userClient.getAllUser();
    }

}
