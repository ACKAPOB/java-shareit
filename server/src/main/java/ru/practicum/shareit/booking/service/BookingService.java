package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.booking.dto.BookingDtoOut;
import ru.practicum.shareit.booking.exception.BadRequestException;
import ru.practicum.shareit.booking.exception.MessageFailedException;

import javax.validation.constraints.PositiveOrZero;
import java.util.List;
import java.util.Optional;

public interface BookingService {

    BookingDtoOut createBooking(Optional<Long> userId, Optional<BookingDtoIn> bookingDtoIn);

    BookingDtoOut updateStatus(Optional<Long> userId, Optional<Long> bookingId, Boolean approved);

    BookingDtoOut getBookingById(Optional<Long> userId, Optional<Long> bookingId);

    List<BookingDtoOut> getBookingsState(Optional<Long> userId, Optional<Integer> from, Optional<Integer> size, String state)
            throws BadRequestException, MessageFailedException;

    List<BookingDtoOut> getBookingsOwnerState(Optional<Long> userId,
                                              @PositiveOrZero Optional<Integer> from,
                                              @PositiveOrZero Optional<Integer> size,
                                              String state)
            throws MessageFailedException;

}
