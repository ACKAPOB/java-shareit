package ru.practicum.shareit.item.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemMapper;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;


import static java.util.stream.Collectors.toList;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {

    private final ItemMapper itemMapper;

    private final ItemRepository repository;
    private final UserRepository userRepository;

    @Transactional
    @Override
    public ItemDto createItem(ItemDto itemDto, Long userId) {
        log.info("Запрос на добавление " + userId + " " + itemDto);
        Item item;
        if (userRepository.existsById(userId)) {
            item = repository.save(itemMapper.toItem(itemDto, userRepository.findById(userId).get()));
        } else
            throw new NotFoundException(
                    "Некорректный запрос пользователь ->" + userId + " не существует");
        return itemMapper.toItemDto(item);
    }

    @Override
    @Transactional
    public ItemDto updateItem(ItemDto itemDto, Long id, Long userId) {
        log.info("Запрос на обновление userId" + userId + " itemsId " + id + " " + itemDto);
        if (repository.findById(id).get().getOwner().getId() != userId) {
            throw new NotFoundException("Ошибка пользователя");
        }

        Optional<Item> item = repository.findById(id);

        if (repository.existsById(id)) {
            if (itemDto.getName() != null) {
                //repository.findById(id).get().setName(itemDto.getName());
                item.get().setName(itemDto.getName());
            }
            if (itemDto.getDescription() != null) {
                //repository.findById(id).get().setDescription(itemDto.getDescription());
                item.get().setDescription(itemDto.getDescription());
            }
            if (itemDto.getAvailable() != null) {
                //repository.findById(id).get().setAvailable(itemDto.getAvailable());
                item.get().setAvailable(itemDto.getAvailable());
            }
            item.get().setId(id);
            repository.save(item.get());
        } else {
            throw new NotFoundException(
                    "Некорректный запрос пользователь ->" + userId + " не существует");
        }
        return itemMapper.toItemDto(repository.findById(id).get());
    }

    @Override
    public ItemDto getItem(Long id) {
        if (repository.findById(id).isPresent()) {
            return itemMapper.toItemDto(repository.findById(id).get());
        } else
            throw new NotFoundException("Некорректный запрос, запись не найдена - > " + id);
    }

    @Override
    public List<ItemDto> getItems(Long userId) {
        return repository.findAll().stream().filter(x -> x.getOwner() == userRepository.findById(userId).get()).map(itemMapper::toItemDto).collect(toList());
    }

    @Override
    public List<ItemDto> searchItems(String search) {
        if (search.isBlank()) {
            return Collections.emptyList();
        }
        return repository
                .findAll()
                .stream()
                .filter(Item::getAvailable)
                .filter(item -> item.getName().toLowerCase().contains(search.toLowerCase())
                        || item.getDescription().toLowerCase().contains(search.toLowerCase()))
                .map(itemMapper::toItemDto)
                .collect(toList());
    }
}