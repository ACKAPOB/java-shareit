package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping("/items")
@Slf4j
@RequiredArgsConstructor
public class ItemController {
    ItemService itemService;

    @Autowired
    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @PostMapping()
    protected ItemDto createItem(@Valid @RequestHeader("X-Sharer-User-Id") Optional<Long> userId,
                                 @RequestBody ItemDto itemDto) {
        log.info("Создание Item ItemController.updateItem, userId = {}", userId);
        return itemService.createItem(userId, itemDto);
    }

    @PatchMapping("/{id}")
    protected ItemDto updateItem(@RequestHeader("X-Sharer-User-Id") Optional<Long> userId,
                                 @PathVariable Optional<Long> id,
                                 @RequestBody ItemDto itemDto) {
        log.info("Обновление Item ItemController.updateItem, userId = {}, itemid = {}, itemDto = {}", userId, id, itemDto);
        return itemService.updateItem(userId, itemDto, id);
    }

    @DeleteMapping("/{id}")
    protected ItemDto deleteItem(@RequestHeader("X-Sharer-User-Id") Optional<Long> userId,
                                 @PathVariable Optional<Long> id) {
        log.info("Удаление Item ItemController.updateItem, userId = {}, itemid = {}", userId, id);
        return itemService.deleteItem(userId, id);
    }

    @GetMapping()
    protected List<ItemDtoOut> getAllItemsOwner(@RequestHeader("X-Sharer-User-Id") Optional<Long> userId,
                                                @RequestParam(value = "from") Optional<Integer> from,
                                                @RequestParam(value = "size") Optional<Integer> size) {
        log.info("Поиск всех Item ItemController.getAllItemsOwner, userId = {}", userId);
        return itemService.getAllItemsOwner(userId, from, size);
    }

    @GetMapping("/{id}")
    protected ItemDtoOut getItemById(@RequestHeader("X-Sharer-User-Id") Optional<Long> userId,
                                     @PathVariable Optional<Long> id) {
        log.info("Поиск Item ItemController.getItemById, userId = {}, itemid = {}", userId, id);
        return itemService.getItemById(userId, id);
    }


    @GetMapping("/search")
    protected List<ItemDto> getItemByIdSearch(
            @RequestHeader("X-Sharer-User-Id") Optional<Long> userId,
            @RequestParam("text") String text,
            @RequestParam(value = "from", required = false) Optional<Integer> from,
            @RequestParam(value = "size", required = false) Optional<Integer> size) {
        log.info("Поиск всех Item ItemController.getAllItems, userId = {}, text = {}", userId, text);
        return itemService.getItemByIdSearch(userId, text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    protected CommentDto createComment(@Valid @RequestHeader("X-Sharer-User-Id") Optional<Long> userId,
                                       @PathVariable Optional<Long> itemId,
                                       @RequestBody CommentDto commentDto) {
        log.info("Создание Сomment ItemController.createComment, userId = {}, itemid = {}, commentDto = {}", userId, itemId, commentDto);
        return itemService.createComment(userId, itemId, commentDto);
    }
}
