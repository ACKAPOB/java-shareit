package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;

@RestController
@RequestMapping("/items")
@Slf4j
@RequiredArgsConstructor
public class ItemController {
    private final ItemClient itemClient;

    @PostMapping()
    protected ResponseEntity<Object> createItemGateWay(@Valid @RequestHeader("X-Sharer-User-Id") Long userId,
                                                       @RequestBody ItemDto itemDto) {
        log.info("ItemController.createItemGateWay  userId = {}", userId);
        return itemClient.createItem(userId, itemDto);
    }

    @PatchMapping("/{id}")
    protected ResponseEntity<Object> updateItemGateWay(@RequestHeader("X-Sharer-User-Id") Long userId,
                                 @PathVariable Long id,
                                 @RequestBody ItemDto itemDto) {
        log.info("ItemController.updateItemGateWay, userId = {}, itemid = {}, itemDto = {}", userId, id, itemDto);
        return itemClient.updateItem(userId, itemDto, id);
    }

    @DeleteMapping("/{id}")
    protected ResponseEntity<Object> deleteItemGareWay(@RequestHeader("X-Sharer-User-Id") Long userId,
                                 @PathVariable Long id) {
        log.info("ItemController.deleteItemGareWay, userId = {}, itemid = {}", userId, id);
        return itemClient.deleteItem(userId, id);
    }

    @GetMapping()
    protected ResponseEntity<Object> getAllItemsOwnerGateWay(@RequestHeader("X-Sharer-User-Id") Long userId,
                 @RequestParam(value = "from", defaultValue = "0") Integer from,
                 @RequestParam(value = "size", defaultValue = "10") Integer size) {
        log.info("ItemController.getAllItemsOwnerGateWay, userId = {}", userId);
        return itemClient.getAllItemsOwner(userId, from, size);
    }

    @GetMapping("/{id}")
    protected ResponseEntity<Object> getItemByIdGateWay(@RequestHeader("X-Sharer-User-Id") Long userId,
                                     @PathVariable Long id) {
        log.info("ItemController.getItemByIdGateWay, userId = {}, itemid = {}", userId, id);
        return itemClient.getItemById(userId, id);
    }

    @GetMapping("/search")
    protected ResponseEntity<Object> getItemByIdSearchGateWay(@RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestParam("text") String text,
            @RequestParam(value = "from", defaultValue = "0") Integer from,
            @RequestParam(value = "size", defaultValue = "10") Integer size) {
        log.info("ItemController.getItemByIdSearchGateWay, userId = {}, text = {}", userId, text);
        return itemClient.getItemByIdSearch(userId, text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    protected ResponseEntity<Object> createCommentGateWay(@Valid @RequestHeader("X-Sharer-User-Id") Long userId,
                                       @PathVariable Long itemId,
                                       @RequestBody CommentDto commentDto) {
        log.info("ItemController.createCommentGateWay, userId = {}, itemid = {}, commentDto = {}", userId, itemId, commentDto);
        return itemClient.createComment(userId, itemId, commentDto);
    }
}
