package ru.practicum.shareit.user.dao;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserStorage {
    User createUser(User user);

    User updateUser(User user);

    void deleteUser(User user);

    User getUser(String email);

    User getUser(long id);

    List<User> getUsers();

    boolean alreadyExists(String email);

    boolean alreadyExists(long id);

    long genId();
}
