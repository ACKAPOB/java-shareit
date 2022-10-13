package ru.practicum.shareit.booking.service;

import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDtoById;
import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.booking.dto.BookingDtoOut;
import ru.practicum.shareit.booking.exception.BadRequestException;
import ru.practicum.shareit.booking.exception.MessageFailedException;

import java.util.List;
import java.util.Optional;

public interface BookingService {

    @Transactional
    BookingDtoById createBooking(Optional<Long> userId, Optional<BookingDtoIn> bookingDtoIn);

    BookingDtoOut updateStatus(Optional<Long> userId, Optional<Long> bookingId, Boolean approved);

    BookingDtoOut getBookingById(Optional<Long> userId, Optional<Long> bookingId);

    List<BookingDtoOut> getBookingsState(Optional<Long> userId, String state)
            throws BadRequestException, MessageFailedException;

    List<BookingDtoOut> getBookingsOwnerState(Optional<Long> userId, String state) throws
            MessageFailedException;
}
