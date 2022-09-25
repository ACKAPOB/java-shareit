package ru.practicum.shareit.item.dao;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemStorage {

    Item createItem(Item item);

    void deleteItem(Item item);

    Item getItem(long id);

    Item getItem(String name);

    List<Item> getItems();

    List<Item> searchItems(String search);

    boolean alreadyExists(long id);

    long genId();
}
