package ru.practicum.shareit.requests;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.BookingClient;
import ru.practicum.shareit.requests.dto.ItemRequestDto;

import javax.validation.Valid;
import javax.validation.ValidationException;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemRequestController {
    private final RequestClient requestClient;

    @PostMapping()
    protected ResponseEntity<Object> createItemRequestGateWay(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                              @Valid @RequestBody ItemRequestDto itemRequestDto) {
        log.info("ItemRequestController.createItemRequestGateWay, userId = {}", userId);
        return requestClient.createItemRequest(userId, itemRequestDto);
    }

    @GetMapping()
    protected ResponseEntity<Object> getAllRequestGateWay(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Получение Requests, ItemRequestController.getAllRequest, userId = {}", userId);
        return requestClient.getAllItemRequest(userId);
    }

    @GetMapping("/{requestId}")
    protected ResponseEntity<Object> getItemRequestGateWay(@RequestHeader("X-Sharer-User-Id") Long userId,
                                            @PathVariable Long requestId) {
        return requestClient.getItemRequestById(userId, requestId);
    }

    @GetMapping("/all")
    protected ResponseEntity<Object> findItemRequestGateWay(@RequestHeader("X-Sharer-User-Id") Long idUser,
                                                   @RequestParam(value = "from", required = false) Integer from,
                                                   @RequestParam(value = "size", required = false) Integer size)
            throws ValidationException {
        return requestClient.getItemRequestPageable(idUser, from, size);
    }
}
