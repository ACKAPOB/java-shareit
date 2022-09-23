package ru.practicum.shareit.item.dao.impl;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dao.ItemStorage;
import ru.practicum.shareit.item.model.Item;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class ItemStorageImpl implements ItemStorage {

    long id = 0;
    private final Map<Long, Item> itemList = new HashMap<>();

    @Override
    public Item createItem(Item item) {
        itemList.put(item.getId(), item);
        return itemList.get(item.getId());
    }

    @Override
    public Item updateItem(Item item) {
        if (item.getName()!= null) {
            itemList.get(item.getId()).setName(item.getName());
        }

        if (item.getDescription()!= null) {
            itemList.get(item.getId()).setDescription(item.getDescription());
        }

        if (item.getAvailable() != null) {
            itemList.get(item.getId()).setAvailable(item.getAvailable());
        }

        if (item.getOwner() != -1) {
            itemList.get(item.getId()).setOwner(item.getOwner());
        }

        return itemList.get(item.getId());
    }

    @Override
    public void deleteItem(Item item) {
        itemList.remove(item.getId());
    }

    @Override
    public Item getItem(long id) {
        return itemList.get(id);
    }

    @Override
    public Item getItem(String name) {
        for (Item out : itemList.values()) {
            if (Objects.equals(out.getName(), name)) {
                return out;
            }
        }
        return null;
    }

    @Override
    public List<Item> getItems() {
        return new ArrayList<>(itemList.values());
    }


    @Override
    public List<Item> searchItems(String search) {
        return itemList.values().stream()
                .filter(Item::getAvailable)
                .filter(item -> item.getName().toLowerCase().contains(search.toLowerCase())
                        || item.getDescription().toLowerCase().contains(search.toLowerCase()))
                .collect(Collectors.toList());
    }

    @Override
    public boolean alreadyExists(long id) {
        return getItem(id) != null;
    }

    @Override
    public long genId() {
        id++;
        return id;
    }

}
