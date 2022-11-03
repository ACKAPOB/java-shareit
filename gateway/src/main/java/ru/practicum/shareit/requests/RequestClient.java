package ru.practicum.shareit.requests;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.requests.dto.ItemRequestDto;

import java.util.Map;

@Slf4j
@Service
public class RequestClient extends BaseClient {

    private static final String API_PREFIX = "/bookings";

    @Autowired
    public RequestClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> createItemRequest (Long userId, ItemRequestDto itemRequestDto) {
        log.info("RequestClient.createItemRequest, userId = {}, itemRequestDto = {}", userId, itemRequestDto.toString());
        return post("", userId, itemRequestDto);
    }

    public ResponseEntity<Object> getAllItemRequest (Long userId) {
        log.info("RequestClient.getAllItemRequest, userId = {} ", userId);
        return get("", userId);
    }

    public ResponseEntity<Object> getItemRequestById (Long userId, Long requestId) {
        log.info("RequestClient.getItemRequestById, userId = {} ", userId);
        return get("/" + requestId, userId);
    }

    public ResponseEntity<Object> getItemRequestPageable (Long userId, Integer from, Integer size) {
        Map<String, Object> parameters = Map.of(
                "from", from,
                "size", size
        );
        return get("?from={from}&size={size}", userId, parameters);
    }
}
