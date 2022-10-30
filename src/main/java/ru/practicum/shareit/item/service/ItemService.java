package ru.practicum.shareit.item.service;

import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoOut;

import java.util.List;
import java.util.Optional;

public interface ItemService {

    List<ItemDtoOut> getAllItemsOwner(Optional<Long> idUser, Optional<Integer> from, Optional<Integer> size);

    List<ItemDto> getItemByIdSearch(Optional<Long> idUser,String text,Optional<Integer> from, Optional<Integer> size);

    CommentDto createComment(Long idUser, Long itemId, CommentDto text);

    ItemDtoOut getItemById(Long idUser, Long id);

    ItemDto updateItem(Long idUser, ItemDto itemDto, Long id);

    ItemDto createItem(Long idUser, ItemDto itemDto);

    ItemDto deleteItem(Long idUser, Long id);
}
