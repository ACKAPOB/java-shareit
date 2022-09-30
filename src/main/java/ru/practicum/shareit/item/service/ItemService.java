package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto createItem(ItemDto itemDto, Long userId);

    ItemDto updateItem(ItemDto itemDto, Long id, Long userId);

    ItemDto getItem(Long id);

    List<ItemDto> getItems(Long userId);

    List<ItemDto> searchItems(String search);
}
