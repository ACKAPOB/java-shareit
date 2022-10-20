package ru.practicum.shareit.requests.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.requests.dto.ItemRequestDto;
import ru.practicum.shareit.requests.dto.ItemRequestMapper;
import ru.practicum.shareit.requests.exceptions.RequestsBadRequestException;
import ru.practicum.shareit.requests.model.ItemRequest;
import ru.practicum.shareit.requests.repository.ItemRequestRepository;
import ru.practicum.shareit.requests.service.ItemRequestService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Autowired
    public ItemRequestServiceImpl(ItemRequestRepository itemRequestRepository,
                                  UserRepository userRepository, ItemRepository itemRepository) {
        this.itemRequestRepository = itemRequestRepository;
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
    }

    public ItemRequestDto createItemRequest(Long idUser, ItemRequestDto itemRequestDto) {
        User user = validationUser(idUser);
        if (itemRequestDto.getDescription().isBlank())
            throw new RequestsBadRequestException("Ошибка isBlank, ItemRequestServiceImpl.createItemRequest()");
        ItemRequest itemRequest = ItemRequestMapper.toItemRequest(itemRequestDto);
        itemRequest.setRequestor(user);
        itemRequest.setCreated(LocalDateTime.now());
        itemRequestRepository.save(itemRequest);
        log.info("Создание Request, ItemRequestController.createItemRequest, itemRequest = {}", itemRequest);
        return ItemRequestMapper.toItemRequestDto(itemRequest);
    }

    public User validationUser(Long idUser) {
        Optional<User> user = Optional.ofNullable(userRepository.findById(idUser).orElseThrow(() ->
                new RequestsBadRequestException("Ошибка user isEmpty, ItemRequestServiceImpl.validationUser()")));
        log.info("Проверка User ItemRequestServiceImpl.validationUser, user = {},", user);
        return user.get();
    }

    public List<ItemRequestDto> getAllItemRequest(Long idUser) {
        User user = validationUser(idUser);
        List<ItemRequestDto> list = ItemRequestMapper.toListItemRequestDto(
                itemRequestRepository.findByRequestorIdOrderByCreatedDesc(user.getId()));
        for (ItemRequestDto itemRequestDto : list) {
            itemRequestDto.setItems(ItemMapper.toListItemDto(
                    itemRepository.findByRequestIdOrderByCreated(itemRequestDto.getId()).get()));
        }
        log.info("Поручение Requests, ItemRequestServiceImpl.getAllItemRequest, list = {},", list);
        return list;
    }

    public ItemRequestDto getItemRequestById(Long idUser, Long id) {
        validationUser(idUser);
        if (id == null) {
            throw new RequestsBadRequestException("Ошибка id isEmpty, ItemRequestServiceImpl.getItemRequestById()");
        }
        Optional<ItemRequest> itemRequest = itemRequestRepository.findById(id);
        if (itemRequest.isEmpty()) {
            throw new RequestsBadRequestException("Ошибка itemRequest isEmpty, ItemRequestServiceImpl.getItemRequestById()");
        }
        ItemRequestDto itemRequestDto = ItemRequestMapper.toItemRequestDto(itemRequest.get());
        itemRequestDto.setItems(ItemMapper.toListItemDto(itemRepository.findByRequestIdOrderByCreated(id).get()));
        log.info("Поручение Request, ItemRequestServiceImpl.getAllItemRequest, itemRequestDto = {},", itemRequestDto);
        return itemRequestDto;
    }
}
