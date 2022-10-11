package ru.practicum.shareit.booking.service;

import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDtoById;
import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.booking.dto.BookingDtoOut;
import ru.practicum.shareit.booking.exception.MessageFailedException;

import javax.validation.constraints.PositiveOrZero;
import java.util.List;
import java.util.Optional;

public interface BookingService {
    public BookingDtoById createBooking(Optional<Long> idUser, Optional<BookingDtoIn> bookingDtoIn);

    BookingDtoOut updateStatus(Optional<Long> idUser, Optional<Long> approved, Boolean bookingId);

    BookingDtoOut getBookingById(Optional<Long> idUser, Optional<Long> bookingId);

    List<BookingDtoOut> getBookingsState(Optional<Long> idUser, Optional<Integer> from, Optional<Integer> size, String state)
    throws MessageFailedException;

    List<BookingDtoOut> getBookingsOwnerState(Optional<Long> idUser, @PositiveOrZero Optional<Integer> from, @PositiveOrZero Optional<Integer> size, String state)
    throws MessageFailedException;

    List<BookingDtoOut> getBookingsAllById(Optional<Long> userId);
}
