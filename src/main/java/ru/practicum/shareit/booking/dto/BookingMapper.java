package ru.practicum.shareit.booking.dto;

import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.user.dto.UserMapper;

import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor
@Component
public class BookingMapper {
    public static BookingDtoOut toBookingDto(Booking booking) {
        return new BookingDtoOut(
                booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                booking.getStatus(),
                UserMapper.toUserDto(booking.getBooker()),
                ItemMapper.toItemDto(booking.getItem())

        );
    }

    public static BookingDtoById toBookingDtoById(Booking booking) {
        return new BookingDtoById(
                booking.getId(),
                booking.getBooker().getId(),
                booking.getStart(),
                booking.getEnd(),
                booking.getStatus()
        );
    }

    public static BookingDtoById toBookingDtoByIdTime(Booking booking) {
        return new BookingDtoById(
                booking.getId(),
                booking.getBooker().getId(),
                booking.getStart(),
                booking.getEnd(),
                booking.getStatus()
        );
    }

    public static Booking toBooking(BookingDtoIn bookingDtoIn) {
        return new Booking(
                bookingDtoIn.getStart(),
                bookingDtoIn.getEnd()
        );
    }

    public static List<BookingDtoOut> toListBookingDto(List<Booking> listBooking) {
        return listBooking.stream().map(BookingMapper::toBookingDto).collect(Collectors.toList());
    }
}