package ru.practicum.shareit.user.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.exception.NotFoundException;
import ru.practicum.shareit.user.dao.UserStorage;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.exception.AlreadyExistsException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.UserMapper;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {
    private long id = 0;
    private final UserStorage userStorage;
    private final UserMapper userMapper;
    private final UserRepository repository;

    @Transactional
    @Override
    public UserDto createUser(UserDto userDto) {
        log.info("Запрос на добавление " + userDto);
        User user = repository.save(userMapper.toUserDB(userDto));
        log.info("Добавление пользователя, создан USer - >  " + user);
        return userMapper.toUserDtoDB(user);
    }

    @Override
    @Transactional
    public UserDto updateUser(UserDto userDto, long id) {
        log.info("Запрос на обновление " + userDto + " , id пользователя " + id);
        if (!repository.existsById(id)) {
            throw new AlreadyExistsException(
                    "Некорректный запрос " + userDto);
        }
        Optional<User> user = repository.findById(id);

        if (userDto.getName() != null && user.isPresent()) {
            user.get().setName(userDto.getName());
        }
        if (userDto.getEmail() != null&& user.isPresent()) {
            user.get().setEmail(userDto.getEmail());
        }
        repository.save(user.get());
        return userMapper.toUserDtoDB(user.get());
    }

    @Override
    @Transactional
    public void deleteUser(long id) {
        log.info("Запрос на удаление пользователя " + id);
        if (!repository.existsById(id)) {
            throw new AlreadyExistsException(
                    "Некорректный запрос, пользователь не существует ");
        }
        repository.delete(repository.findById(id).get());
    }
    @Override
    public UserDto getUser(long id) {
        if (repository.findById(id).isPresent()) {
            return userMapper.toUserDtoDB(repository.findById(id).get());
        } else
            throw new NotFoundException("пользователь не существует ");
    }

    @Override
    public List<UserDto> getUsers() {
        return repository.findAll().stream().map(userMapper::toUserDtoDB).collect(toList());
    }
}
