package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.dto.BookingDtoById;
import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.booking.dto.BookingDtoOut;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BookingMapperTest {
    private static Booking booking;
    private static BookingDtoById bookingDtoById;
    private static BookingDtoIn bookingDtoIn;
    private static BookingDtoOut toBookingDto;


    @BeforeAll
    static void beforeAll() {
        User user = new User(1L, "User", "user@email.ru");
        User booker = new User(2L, "Booker", "Booker@email.ru");
        Item item = new Item(1L, "Item", "Desc", true, user, null);
        booking = new Booking(1L, LocalDateTime.now(), LocalDateTime.now().plusMonths(2), item, booker, Status.WAITING);
        bookingDtoById = new BookingDtoById(
                booking.getId(),
                booking.getBooker().getId(),
                booking.getStart(),
                booking.getEnd(),
                booking.getStatus()
        );

        bookingDtoIn = new BookingDtoIn(
                1L,
                booking.getStart(),
                booking.getEnd()
        );

        toBookingDto = new BookingDtoOut(
                booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                booking.getStatus(),
                UserMapper.toUserDto(booking.getBooker()),
                ItemMapper.toItemDto(booking.getItem())
        );

    }

    @Test
    void toBookingDtoTest() {
        BookingDtoOut result = BookingMapper.toBookingDto(booking);
        assertEquals(toBookingDto.getId(), result.getId());
        assertEquals(toBookingDto.getStart(), result.getStart());
        assertEquals(toBookingDto.getEnd(), result.getEnd());
        assertEquals(toBookingDto.getBooker().getName(), result.getBooker().getName());
        assertEquals(toBookingDto.getItem().getName(), result.getItem().getName());
    }

    @Test
    void toBookingDtoByIdTest() {
        BookingDtoById result = BookingMapper.toBookingDtoByIdTime(booking);
        assertEquals(bookingDtoById, result);
    }

    @Test
    void toBookingTest() {
        Booking result = BookingMapper.toBooking(bookingDtoIn);
        assertEquals(booking.getStart(), result.getStart());
        assertEquals(booking.getEnd(), result.getEnd());
    }


    @Test
    void toListBookingDtoTest() {
        List<BookingDtoOut> expected = List.of(toBookingDto);
        List<BookingDtoOut> result = BookingMapper.toListBookingDto(List.of(booking));
        assertEquals(expected.get(0).getId(), result.get(0).getId());
    }
}