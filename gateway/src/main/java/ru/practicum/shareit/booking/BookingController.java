package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.booking.dto.BookingState;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;


@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {
	private final BookingClient bookingClient;

	@PostMapping()
	protected ResponseEntity<Object> createBookingGateWay(@Valid @RequestHeader("X-Sharer-User-Id") Long userId,
										  @RequestBody BookingDtoIn bookingDtoIn) {
		log.info("BookingController.createBookingGateWay, userId = {}", userId);
		return bookingClient.createBooking(userId, bookingDtoIn);
	}

	@PatchMapping("/{bookingId}")
	protected ResponseEntity<Object> updateStatusGateWay(@RequestHeader("X-Sharer-User-Id") Long userId,
										 @PathVariable Long bookingId, @RequestParam(value = "approved") Boolean approved) {
		log.info("BookingController.updateStatusGateWay, userId = {}, bookingId = {}, approved = {} ", userId, bookingId, approved);
		return bookingClient.updateStatus(userId, bookingId, approved);
	}

	@GetMapping("/{bookingId}")
	protected ResponseEntity<Object> getBookingByIdGateWay(@RequestHeader("X-Sharer-User-Id") Long userId,
										   @PathVariable(value = "bookingId", required = false) Long bookingId) {
		log.info("BookingController.getBookingByIdGateWay, userId = {}", userId);
		return bookingClient.getBookingById(userId, bookingId);
	}

	@GetMapping()
	protected ResponseEntity<Object> getBookingsStateGateWay(@RequestHeader("X-Sharer-User-Id") Long userId,
			@PositiveOrZero @RequestParam(value = "from", defaultValue = "0") Integer from,
			@Positive @RequestParam(value = "size", defaultValue = "10") Integer size,
			@RequestParam(value = "state", defaultValue = "ALL") String state) {
		BookingState bookingState = BookingState.from(state)
				.orElseThrow(() -> new IllegalArgumentException("Unknown state: " + state));
		log.info("BookingController.getBookingsStateGateWay, userId = {}, from = {}, size = {}, state = {}", userId, from, size, state);
		return bookingClient.getBookingsState(userId, from, size, bookingState);
	}

	@GetMapping("/owner")
	protected ResponseEntity<Object> getBookingsOwnerStateGateWay(@RequestHeader("X-Sharer-User-Id") Long userId,
			@PositiveOrZero @RequestParam(value = "from", defaultValue = "0") Integer from,
			@Positive @RequestParam(value = "size", defaultValue = "10") Integer size,
			@RequestParam(value = "state", required = false, defaultValue = "ALL") String state) {
		BookingState bookingState = BookingState.from(state)
				.orElseThrow(() -> new IllegalArgumentException("Unknown state: " + state));//Unknown state: UNSUPPORTED_STATUS
		log.info("BookingController.getBookingsOwnerStateGateWay, userId = {}, from = {}, size = {}, state = {}", userId, from, size, state);
		return bookingClient.getBookingsOwnerState(userId, from, size, bookingState);
	}

/*

	/////////////////////////

	@GetMapping
	public ResponseEntity<Object> getBookings(@RequestHeader("X-Sharer-User-Id") long userId,
			@RequestParam(name = "state", defaultValue = "all") String stateParam,
			@PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
			@Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
		BookingState state = BookingState.from(stateParam)
				.orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stateParam));
		log.info("Get booking with state {}, userId={}, from={}, size={}", stateParam, userId, from, size);
		return bookingClient.getBookings(userId, state, from, size);
	}

	@PostMapping
	public ResponseEntity<Object> bookItem(@RequestHeader("X-Sharer-User-Id") long userId,
			@RequestBody @Valid BookItemRequestDto requestDto) {
		log.info("Creating booking {}, userId={}", requestDto, userId);
		return bookingClient.bookItem(userId, requestDto);
	}

	@GetMapping("/{bookingId}")
	public ResponseEntity<Object> getBooking(@RequestHeader("X-Sharer-User-Id") long userId,
			@PathVariable Long bookingId) {
		log.info("Get booking {}, userId={}", bookingId, userId);
		return bookingClient.getBooking(userId, bookingId);
	} */
}
