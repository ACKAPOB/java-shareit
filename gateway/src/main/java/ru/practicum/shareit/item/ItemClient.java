package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Map;

@Slf4j
@Service
public class ItemClient extends BaseClient {
    private static final String API_PREFIX = "/items";

    @Autowired
    public ItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> createItem (Long userId, ItemDto itemDto) {
        log.info("ItemClient.createItem, itemDto = {}, userDto = {} ", itemDto, userId);
        return post("", userId, itemDto);
    }

    public ResponseEntity<Object> updateItem (Long userId, ItemDto itemDto, Long id) {
        log.info("ItemClient.createItem, itemDto = {}, userDto = {}, id = {} ", itemDto, userId, id);
        return patch("/" + id, userId, itemDto);
    }

    public ResponseEntity<Object> deleteItem (Long userId, Long id) {
        log.info("ItemClient.deleteItem, id = {}, userDto = {} ", id, userId);
        return delete("/" + id, userId); //(userId, id);
    }

    public ResponseEntity<Object> getAllItemsOwner(Long userId, Integer from, Integer size) {
        Map<String, Object> parameters = Map.of(
                "from", from,
                "size", size
        );
        log.info("ItemClient.getAllItemsOwner, userId = {},  from = {}, size = {} ",userId, from, size);
        return get("?from={from}&size={size}", userId, parameters);
    }

    public ResponseEntity<Object> getItemById(Long userId, Long id) {
        log.info("ItemClient.getItemById, userId = {},  id = {} ",userId, id);
        return get("/" + id, userId);
    }

    public ResponseEntity<Object> getItemByIdSearch(Long userId, String text, Integer from, Integer size) {
        Map<String, Object> parameters = Map.of(
                "text", text,
                "from", from,
                "size", size
        );
        log.info("ItemClient.getItemByIdSearch, userId = {},  from = {}, size = {},  text = {}",userId, from, size, text);
        return get("/search" + "?text={text}&from={from}&size={size}", userId, parameters);
    }

    public ResponseEntity<Object> createComment (Long userId, Long itemId, CommentDto commentDto) {
        return post("/" + itemId +"/comment", userId, commentDto);
    }
}
