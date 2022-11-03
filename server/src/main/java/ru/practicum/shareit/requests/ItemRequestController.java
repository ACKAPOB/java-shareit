package ru.practicum.shareit.requests;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.requests.dto.ItemRequestDto;
import ru.practicum.shareit.requests.service.ItemRequestService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.ValidationException;
import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping(path = "/requests")
public class ItemRequestController {

    private final ItemRequestService itemRequestService;

    @Autowired
    public ItemRequestController(ItemRequestService itemRequestService) {
        this.itemRequestService = itemRequestService;
    }

    @RequestMapping(value = "/", produces = "application/json")
    public String getURLValue(HttpServletRequest request) {
        return request.getRequestURI();
    }

    @PostMapping()
    protected ItemRequestDto createItemRequest(@RequestHeader("X-Sharer-User-Id") Optional<Long> userId,
                                               @Valid @RequestBody ItemRequestDto itemRequestDto) {
        log.info("Создание Request, ItemRequestController.createItemRequest, userId = {}", userId);
        return itemRequestService.createItemRequest(userId, itemRequestDto);
    }

    @GetMapping()
    protected List<ItemRequestDto> getAllRequest(@RequestHeader("X-Sharer-User-Id") Optional<Long> userId) {
        log.info("Получение Requests, ItemRequestController.getAllRequest, userId = {}", userId);
        return itemRequestService.getAllItemRequest(userId);
    }

    @GetMapping("/{requestId}")
    protected ItemRequestDto getItemRequest(@RequestHeader("X-Sharer-User-Id") Optional<Long> userId,
                                            @PathVariable Optional<Long> requestId) {
        return itemRequestService.getItemRequestById(userId, requestId.get());
    }

    @GetMapping("/all")
    protected List<ItemRequestDto> findItemRequest(@RequestHeader("X-Sharer-User-Id") Optional<Long> idUser,
                                                   @RequestParam(value = "from", required = false) Optional<Integer> from,
                                                   @RequestParam(value = "size", required = false) Optional<Integer> size)
            throws ValidationException {
        return itemRequestService.getItemRequestPageable(idUser, from, size);
    }

}
