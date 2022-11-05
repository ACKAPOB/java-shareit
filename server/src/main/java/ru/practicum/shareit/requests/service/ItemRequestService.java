package ru.practicum.shareit.requests.service;

import ru.practicum.shareit.requests.dto.ItemRequestDto;

import java.util.List;
import java.util.Optional;

public interface ItemRequestService {
    List<ItemRequestDto> getAllItemRequest(Optional<Long> userId);

    ItemRequestDto createItemRequest(Optional<Long> userId, ItemRequestDto itemRequestDto);

    ItemRequestDto getItemRequestById(Optional<Long> userId, Long id);

    List<ItemRequestDto> getItemRequestPageable(Optional<Long> idUser, Optional<Integer> from, Optional<Integer> size);
}
