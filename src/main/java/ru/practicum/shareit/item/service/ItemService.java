package ru.practicum.shareit.item.service;

import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoOut;

import java.util.List;
import java.util.Optional;

public interface ItemService {

    List<ItemDtoOut> getAllItemsOwner (Optional<Long> idUser);
    List<ItemDto> getItemByIdSearch (Optional<Long> idUser, String text);
    CommentDto createComment(Optional<Long> idUser, Optional<Long> itemId, CommentDto text);
    ItemDtoOut getItemById(Optional<Long> idUser, Optional<Long> id);
    ItemDto updateItem(Optional<Long> idUser, ItemDto itemDto, Optional<Long> id);
    ItemDto createItem(Optional<Long> idUser, ItemDto itemDto);
    ItemDto deleteItem(Optional<Long> idUser, Optional<Long> id);
}
