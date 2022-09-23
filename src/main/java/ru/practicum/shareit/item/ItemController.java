package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.Create;
import ru.practicum.shareit.user.Update;

import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping("/items")
@Slf4j
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @PostMapping()
    ItemDto createItem(@RequestHeader("X-Sharer-User-Id") long userId,
                       @Validated({Create.class}) @RequestBody ItemDto itemDto) {
        log.info("Post itemDto Name = {}", itemDto.getName());
        return itemService.createItem(itemDto, userId);
    }

    @PatchMapping("/{itemId}")
    ItemDto updateItem(@RequestHeader("X-Sharer-User-Id") long userId,
                              @PathVariable long itemId,
                              @Validated({Update.class}) @RequestBody ItemDto itemDto) {
        log.info("Put itemDto Name = {}", itemDto + " /userId " + userId + " /itemId " + itemId);
        return itemService.updateItem(itemDto, itemId, userId);
    }

    @GetMapping("/{id}")
    public ItemDto getItem (@PathVariable long id) {
        log.info("Get item id = {} ", id);
        return itemService.getItem(id);
    }

    @GetMapping()
    public List<ItemDto> getItems (@RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Get items");
        return itemService.getItems(userId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItems (@RequestHeader("X-Sharer-User-Id") long userId, @RequestParam String text) {
        log.info("search items");
        return itemService.searchItems(text);
    }


}
