package ru.practicum.shareit.user.dao.impl;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.dao.UserStorage;

import java.util.*;

@Component
public class UserStorageImpl implements UserStorage {

    long id = 0;
    private final Map<Long, User> userList = new HashMap<>();

    @Override
    public User createUser(User user) {
        userList.put(user.getId(), user);
        return user;
    }

    @Override
    public User updateUser(User user) {
        if (user.getName()!= null) {
            userList.get(user.getId()).setName(user.getName());
        }
        if (user.getEmail() != null) {
            userList.get(user.getId()).setEmail(user.getEmail());
        }
        return userList.get(user.getId());
    }

    @Override
    public void deleteUser(User user) {
        userList.remove(user.getId());
    }

    @Override
    public User getUser (String email) {
        for (User user : userList.values()) {
            if (Objects.equals(user.getEmail(), email)) {
                return user;
            }
        }
        return null;
    }

    @Override
    public User getUser(long id) {
        return userList.get(id);
    }

    @Override
    public List<User> getUsers () {
            return new ArrayList<>(userList.values());
    }

    @Override
    public boolean alreadyExists(String email) {
        return getUser(email) != null;
    }

    @Override
    public boolean alreadyExists(long id) {
        return getUser(id) != null;
    }

    @Override
    public long genId() {
        id++;
        return id;
    }

}
