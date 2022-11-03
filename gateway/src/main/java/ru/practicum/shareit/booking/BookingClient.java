package ru.practicum.shareit.booking;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.client.BaseClient;

import java.util.Map;
@Slf4j
@Service
public class BookingClient extends BaseClient {
    private static final String API_PREFIX = "/bookings";

    @Autowired
    public BookingClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> createBooking (Long userId, BookingDtoIn bookingDtoIn) {
        log.info("BookingClient.createBooking, bookingDtoIn = {} ", bookingDtoIn);
        return post("",userId, bookingDtoIn);
    }

    public ResponseEntity<Object> updateStatus(Long userId, Long bookingId, Boolean approved) {
        Map<String, Object> parameters = Map.of(
                "approved", approved);
        log.info("UserClient.updateBooking user id = {}, bookingId = {}, approved = {}", userId, bookingId, approved);
        return patch("/" + bookingId + "?approved={approved}", userId, parameters, null);
    }

    public ResponseEntity<Object> getBookingById(Long userId, Long bookingId) {
        log.info("BookingClient.getBookingById, userId = {},  bookingId = {} ",userId, bookingId);
        return get("/" + bookingId, userId);
    }

    public ResponseEntity<Object> getBookingsState(Long userId, Integer from, Integer size, BookingState state) {
        Map<String, Object> parameters = Map.of(
                "state", state.name(),
                "from", from,
                "size", size
        );
        log.info("BookingClient.getBookingsState, userId = {},  from = {}, size = {},  state = {}",userId, from, size, state);
        return get("?state={state}&from={from}&size={size}", userId, parameters);
    }

    public ResponseEntity<Object> getBookingsOwnerState(Long userId, Integer from, Integer size, BookingState state) {
        Map<String, Object> parameters = Map.of(
                "state", state.name(),
                "from", from,
                "size", size
        );
        log.info("BookingClient.getBookingsOwnerState, userId = {},  from = {}, size = {},  state = {}",userId, from, size, state);
        return get("?state={state}&from={from}&size={size}", userId, parameters);
    }
}
