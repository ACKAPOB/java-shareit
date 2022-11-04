package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.exception.MessageFailedException;

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
			@RequestParam(value = "state", defaultValue = "ALL") String state) throws MessageFailedException{
		BookingState bookingState = BookingState.from(state)
				.orElseThrow(MessageFailedException::new);
		log.info("BookingController.getBookingsStateGateWay, userId = {}, from = {}, size = {}, state = {}", userId, from, size, state);
		return bookingClient.getBookingsState(userId, from, size, bookingState);
	}

	@GetMapping("/owner")
	protected ResponseEntity<Object> getBookingsOwnerStateGateWay(@RequestHeader("X-Sharer-User-Id") Long userId,
			@PositiveOrZero @RequestParam(value = "from", defaultValue = "0") Integer from,
			@Positive @RequestParam(value = "size", defaultValue = "10") Integer size,
			@RequestParam(value = "state", required = false, defaultValue = "ALL") String state) throws MessageFailedException {
		BookingState bookingState = BookingState.from(state)
				.orElseThrow(MessageFailedException::new);
		log.info("BookingController.getBookingsOwnerStateGateWay, userId = {}, from = {}, size = {}, state = {}", userId, from, size, state);
		return bookingClient.getBookingsOwnerState(userId, from, size, bookingState);
	}
}
