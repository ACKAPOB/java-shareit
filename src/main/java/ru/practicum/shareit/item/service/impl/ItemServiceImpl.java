package ru.practicum.shareit.item.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dao.ItemStorage;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemMapper;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dao.UserStorage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemStorage itemStorage;
    private final UserStorage userStorage;
    private final ItemMapper itemMapper;

    @Override
    public ItemDto createItem(ItemDto itemDto, long userId) {
        if (!userStorage.alreadyExists(userId)) {
            throw new NotFoundException(
                    "Некорректный запрос пользователь ->" + userId + " не существует" );
        }
        log.info("Запрос на добавление " + userId + " " + itemDto);
        itemStorage.createItem(itemMapper.toItem(itemDto, itemStorage.genId(), userId));
        return itemMapper.toItemDto(itemStorage.getItem(itemDto.getName()));
    }

    @Override
    public ItemDto updateItem(ItemDto itemDto, long id, long userId) {
        if (itemStorage.getItem(id).getOwner() != userId) {
            throw new NotFoundException(
                    "Некорректный запрос пользователь указан неверно" );
        }
        itemStorage.updateItem(itemMapper.toItem(itemDto, id, userId));
        return itemMapper.toItemDto(itemStorage.getItem(id));
    }

    @Override
    public ItemDto getItem(long id) {
        return itemMapper.toItemDto(itemStorage.getItem(id));
    }

    @Override
    public List<ItemDto> getItems(long userId) {
        List <ItemDto> itemDtoList = new ArrayList<>();
        for (Item out : itemStorage.getItems()) {
            if (out.getOwner() == userId) {
                itemDtoList.add(itemMapper.toItemDto(out));
            }
        }
        return itemDtoList;
    }

    @Override
    public List<ItemDto> searchItems(String search) {
        if (search.isBlank()) {
            return Collections.emptyList();
        }
          List <ItemDto> out = new ArrayList<>();
          for (Item item : itemStorage.searchItems(search)) {
              out.add(itemMapper.toItemDto(item));
          }
        return out;
    }
}
