package ru.practicum.shareit.item.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dao.ItemStorage;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.exception.NotFoundException;
import ru.practicum.shareit.item.model.ItemMapper;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dao.UserStorage;

import java.util.Collections;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
@Slf4j
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemStorage itemStorage;
    private final UserStorage userStorage;
    private final ItemMapper itemMapper;

    @Override
    public ItemDto createItem(ItemDto itemDto, long userId) {
        log.info("Запрос на добавление " + userId + " " + itemDto);
        if (!userStorage.alreadyExists(userId)) {
            throw new NotFoundException(
                    "Некорректный запрос пользователь ->" + userId + " не существует");
        }
        itemStorage.createItem(itemMapper.toItem(itemDto, itemStorage.genId(), userId));
        return itemMapper.toItemDto(itemStorage.getItem(itemDto.getName()));
    }

    @Override
    public ItemDto updateItem(ItemDto itemDto, long id, long userId) {
        log.info("Запрос на обновление " + userId + " " + itemDto);
        if (!userStorage.alreadyExists(userId)) {
            throw new NotFoundException(
                    "Некорректный запрос пользователь ->" + userId + " не существует");
        }
        if (itemStorage.getItem(id).getOwner() != userId) {
            throw new NotFoundException(
                    "Некорректный запрос пользователь указан неверно");
        }

        if (itemDto.getName() != null) {
            itemStorage.getItem(id).setName(itemDto.getName());
        }

        if (itemDto.getDescription() != null) {
            itemStorage.getItem(id).setDescription(itemDto.getDescription());
        }

        if (itemDto.getAvailable() != null) {
            itemStorage.getItem(id).setAvailable(itemDto.getAvailable());
        }

        itemStorage.getItem(id).setOwner(userId);

        return itemMapper.toItemDto(itemStorage.getItem(id));
    }

    @Override
    public ItemDto getItem(long id) {
        return itemMapper.toItemDto(itemStorage.getItem(id));
    }

    @Override
    public List<ItemDto> getItems(long userId) {
        return itemStorage.getItems().stream().filter(x->x.getOwner() == userId).map(itemMapper::toItemDto).collect(toList());
    }

    @Override
    public List<ItemDto> searchItems(String search) {
        if (search.isBlank()) {
            return Collections.emptyList();
        }
          return itemStorage.searchItems(search).stream().map(itemMapper::toItemDto).collect(toList());

    }
}
