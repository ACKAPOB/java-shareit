package ru.practicum.shareit.booking;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDtoById;
import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.booking.dto.BookingDtoOut;
import ru.practicum.shareit.booking.exception.MessageFailedException;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

/**
 * TODO Sprint add-bookings.
 */
@RestController
@RequestMapping("/bookings")
@Slf4j
public class BookingController {

    BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping()
    protected BookingDtoById createBooking(@Valid @RequestHeader("X-Sharer-User-Id") Optional<Long> userId,
        @RequestBody Optional<BookingDtoIn> bookingDtoIn) {
        log.info("Создание Booking BookingController.createBooking, userId = {}", userId);
        return bookingService.createBooking(userId, bookingDtoIn);
    }

    @PatchMapping("/{bookingId}")
    protected BookingDtoOut updateStatus(@RequestHeader("X-Sharer-User-Id") Optional<Long> userId,
        @PathVariable Optional<Long> bookingId, @RequestParam(value="approved") Boolean approved) {
        log.info("Обновление Booking BookingController.updateStatus, userId = {}", userId);
        return bookingService.updateStatus(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    protected BookingDtoOut getBookingById(@RequestHeader("X-Sharer-User-Id") Optional<Long> userId,
        @PathVariable(value = "bookingId", required = false) Optional<Long> bookingId) {
        log.info("Поиск Booking BookingController.getBookingById, userId = {}", userId);
        return bookingService.getBookingById(userId, bookingId);
    }

    @GetMapping()
    protected List<BookingDtoOut> getBookingsState(@RequestHeader("X-Sharer-User-Id") Optional<Long> userId,
            @RequestParam(value = "state", required = false, defaultValue = "ALL") String state)
            throws MessageFailedException {
        log.info("Поиск Bookings BookingController.getBookingsState, userId = {}", userId);
        return bookingService.getBookingsState(userId, state);
    }

    @GetMapping("/owner")
    protected List<BookingDtoOut> getBookingsOwnerState(@RequestHeader("X-Sharer-User-Id") Optional<Long> userId,
            @RequestParam(value = "state", required = false, defaultValue = "ALL") String state)
            throws MessageFailedException {
        log.info("Поиск Bookings BookingController.getBookingsOwnerState, userId = {}, state = {}", userId, state);
        return bookingService.getBookingsOwnerState(userId, state);
    }
}
