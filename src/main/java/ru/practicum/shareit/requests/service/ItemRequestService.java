package ru.practicum.shareit.requests.service;

import ru.practicum.shareit.requests.dto.ItemRequestDto;

import javax.validation.ValidationException;
import java.util.List;
import java.util.Optional;

public interface ItemRequestService {
    List<ItemRequestDto> getAllItemRequest(Optional<Long> userId);

    ItemRequestDto createItemRequest(Optional<Long> userId, ItemRequestDto itemRequestDto);


    List<ItemRequestDto> getItemRequestPageable(Optional<Long> userId, Optional<Integer> from, Optional<Integer> size);

    ItemRequestDto getItemRequestById(Optional<Long> userId, Optional<Long> id);
}
