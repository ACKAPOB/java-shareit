package ru.practicum.shareit.requests.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.FromSizeRequest;
import ru.practicum.shareit.booking.exception.BadRequestException;
import ru.practicum.shareit.booking.exception.BookingNotFoundException;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.requests.dto.ItemRequestDto;
import ru.practicum.shareit.requests.dto.ItemRequestMapper;
import ru.practicum.shareit.requests.exceptions.RequestsBadRequestException;
import ru.practicum.shareit.requests.exceptions.RequestsNotFoundException;
import ru.practicum.shareit.requests.model.ItemRequest;
import ru.practicum.shareit.requests.repository.ItemRequestRepository;
import ru.practicum.shareit.requests.service.ItemRequestService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
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

    public ItemRequestDto createItemRequest(Optional<Long> idUser, ItemRequestDto itemRequestDto) {
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

    public User validationUser(Optional<Long> userId) {
        if (userId.isEmpty()) {
            throw new BadRequestException("Ошибка userId.isEmpty, BookingServiceImpl.validationUser()");
        }
        log.info("Проверка User BookingServiceImpl.validationUser, userId = {}", userId);
        return userRepository.findById(userId.get())
                .orElseThrow(() -> new BookingNotFoundException("Ошибка user не найден, createBooking.updateStatus"));
    }

    public List<ItemRequestDto> getAllItemRequest(Optional<Long> idUser) {
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

    public ItemRequestDto getItemRequestById(Optional<Long> idUser, Long id) {
        validationUser(idUser);
        if (id == null) {
            throw new RequestsNotFoundException("Ошибка id isEmpty, ItemRequestServiceImpl.getItemRequestById()");
        }
        Optional<ItemRequest> itemRequest = itemRequestRepository.findById(id);
        if (itemRequest.isEmpty()) {
            throw new RequestsNotFoundException("Ошибка itemRequest isEmpty, ItemRequestServiceImpl.getItemRequestById()");
        }
        ItemRequestDto itemRequestDto = ItemRequestMapper.toItemRequestDto(itemRequest.get());
        itemRequestDto.setItems(ItemMapper.toListItemDto(itemRepository.findByRequestIdOrderByCreated(id).get()));
        log.info("Поручение Request, ItemRequestServiceImpl.getItemRequestById, itemRequestDto = {},", itemRequestDto);
        return itemRequestDto;
    }

    @Override
    public List<ItemRequestDto> getItemRequestPageable(Optional<Long> idUser, Optional<Integer> from, Optional<Integer> size) {
        User user = validationUser(idUser);
        if (from.isEmpty() || size.isEmpty()) {
            return Collections.emptyList();
        }
        final Pageable pageable = FromSizeRequest.of(from.get(), size.get(), Sort.by("start").descending());
        List<ItemRequest> itemRequests = itemRepository.getByItemRequestListRequestor(idUser.get(), pageable);
        List<ItemRequestDto> itemRequestDtos = ItemRequestMapper.toListItemRequestDto(itemRequests);

        for (ItemRequestDto itemRequest: itemRequestDtos) {
            if (itemRepository.findByRequestIdOrderByCreated(itemRequest.getId()).isPresent())
                itemRequest.setItems(ItemMapper.toListItemDto(itemRepository
                        .findByRequestIdOrderByCreated(itemRequest.getId()).get()));
        }
        log.info("Поручение List Request, ItemRequestServiceImpl.getItemRequestPageable, idUser = {}, from = {}, size = {}", idUser, from, size);
        return itemRequestDtos;
    }
}
