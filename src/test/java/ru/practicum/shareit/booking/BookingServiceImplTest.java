package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.booking.exception.BadRequestException;
import ru.practicum.shareit.booking.exception.BookingNotFoundException;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.booking.service.impl.BookingServiceImpl;


import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.requests.model.ItemRequest;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {

    private BookingService bookingService;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;

    private Booking booking;
    private User user;
    private static User booker;
    private Item item;
    private static BookingDtoIn bookingDtoIn;


    @BeforeEach
    void beforeEach() {
        bookingService = new BookingServiceImpl(bookingRepository, itemRepository, userRepository);
        user = new User(1L, "User", "user@email.ru");
        booker = new User(2L, "Booker", "Booker@email.ru");
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(1L);
        item = new Item(1L, "Item", "Desc", true, user, null);
        item.setRequest(itemRequest);
        booking = new Booking(1L, LocalDateTime.now(), LocalDateTime.now().plusMonths(2),item, booker, Status.WAITING);

        bookingDtoIn = new BookingDtoIn(
                1L,
                booking.getStart(),
                booking.getEnd()
        );

    }

    @Test
    void bookingCreateFailedByNotFoundItemIdTest() {
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.empty());
        Exception exc = assertThrows(BookingNotFoundException.class,
                () -> bookingService.createBooking(Optional.of(2L), Optional.ofNullable(bookingDtoIn)));
        assertEquals("Ошибка item.isEmpty, BookingServiceImpl.validationItem()", exc.getMessage());
    }

    @Test
    void bookingCreateFailedByWrongUserIdTest() { //Booking create failed by wrong userId
        when(itemRepository.findById(1L))
                .thenReturn(Optional.of(item));
        Exception exc = assertThrows(BookingNotFoundException.class,
                () -> bookingService.createBooking(Optional.of(100L), Optional.ofNullable(bookingDtoIn)));
        assertEquals("Ошибка user не найден, createBooking.updateStatus", exc.getMessage());
    }

    @Test
    void bookingCreateFiledByEndInBeforeTest() { //BBooking create failed by end before start
        BookingDtoIn failedBookingDtoIn = new BookingDtoIn(
                1L,
                booking.getStart().plusMonths(2),
                booking.getEnd().minusMonths(2)
        );
        when(itemRepository.findById(1L))
                .thenReturn(Optional.of(item));
        when(userRepository.findById(2L))
                .thenReturn(Optional.ofNullable(booker));
        Exception exc = assertThrows(BadRequestException.class,
                () -> bookingService.createBooking(Optional.of(2L), Optional.of(failedBookingDtoIn)));
        assertEquals("Ошибка 3, BookingServiceImpl.createBooking", exc.getMessage());
    }

    @Test
    void bookingCreateFiledByStartInBeforeTimeTodayTest() {
        BookingDtoIn failedBookingDtoIn_2 = new BookingDtoIn(
                1L,
                booking.getStart().minusMonths(3),
                booking.getEnd().plusMonths(1)
        );
        when(itemRepository.findById(1L))
                .thenReturn(Optional.of(item));
        when(userRepository.findById(2L))
                .thenReturn(Optional.ofNullable(booker));
        Exception exc = assertThrows(BadRequestException.class,
                () -> bookingService.createBooking(Optional.of(2L), Optional.of(failedBookingDtoIn_2)));
        assertEquals("Ошибка 5, BookingServiceImpl.createBooking", exc.getMessage());
    }

    @Test
    void bookingCreateFiledByEndInBeforeTimeTodayTest() {
        BookingDtoIn failedBookingDtoIn_2 = new BookingDtoIn(
                1L,
                booking.getStart().minusMonths(3),
                booking.getEnd().minusMonths(2)
        );
        when(itemRepository.findById(1L))
                .thenReturn(Optional.of(item));
        when(userRepository.findById(2L))
                .thenReturn(Optional.ofNullable(booker));
        Exception exc = assertThrows(BadRequestException.class,
                () -> bookingService.createBooking(Optional.of(2L), Optional.of(failedBookingDtoIn_2)));
        assertEquals("Ошибка 4, BookingServiceImpl.createBooking", exc.getMessage());
    }

    @Test
            //bookingCreateFromUser1ToItem1UnavailableTest
    void bookingCreateOwnerEqualsBookerTest() {
        when(userRepository.findById(2L))
                .thenReturn(Optional.of(user));
        when(itemRepository.findById(1L))
                .thenReturn(Optional.of(item));
        Exception exc = assertThrows(BookingNotFoundException.class,
                () -> bookingService.createBooking(Optional.of(2L), Optional.of(bookingDtoIn)));
        assertEquals("Ошибка 1, BookingServiceImpl.createBooking", exc.getMessage());
    }

    @Test
    void bookingCreateFromUser1ToItem1UnavailableTest() { // Booking create from user1 to item1 unavailable
        item.setAvailable(false);
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(booker));
        RuntimeException exc = assertThrows(BadRequestException.class,
                () -> bookingService.createBooking(Optional.of(2L), Optional.of(bookingDtoIn)));
        assertEquals("Ошибка 2, BookingServiceImpl.createBooking", exc.getMessage());
    }

    @Test
    void shouldThrowEntityNotFoundExceptionIfBookingAccessDenied() {
        User user3 = new User(3L, "User3", "user3@email.ru");
        when(bookingRepository.findById(1L))
                .thenReturn(Optional.of(booking));
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user3));
        Exception exc = assertThrows(BookingNotFoundException.class,
                () -> bookingService.getBookingById(Optional.of(3L), Optional.of(1L)));
        assertEquals("Ошибка, BookingServiceImpl.getBookingById", exc.getMessage());
    }

    @Test
    void bookingGetForWrongUserTest() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());
        Exception exc = assertThrows(BookingNotFoundException.class,
                () -> bookingService.getBookingById(Optional.of(1L), Optional.of(1L)));
        assertEquals("Ошибка user не найден, createBooking.updateStatus", exc.getMessage());
    }

    @Test
    void bookingGetForWrongBookerTest() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(bookingRepository.findById(1L))
                .thenReturn(Optional.empty());
        Exception exc = assertThrows(BookingNotFoundException.class,
                () -> bookingService.getBookingById(Optional.of(1L), Optional.of(1L)));
        assertEquals("Ошибка booking.isEmpty, BookingServiceImpl.validationBooking()", exc.getMessage());
    }

    @Test //updateStatus
    void updateStatusBookingIsEmptyTest() {
        when(bookingRepository.findById(1L))
                .thenReturn(Optional.empty());
        Exception exc = assertThrows(BadRequestException.class,
                () -> bookingService.updateStatus(Optional.of(1L), Optional.of(1L), true));
        assertEquals("Ошибка booking, createBooking.updateStatus", exc.getMessage());
    }

    @Test
    void updateStatusUserIsEmptyTest() {
        when(bookingRepository.findById(1L))
                .thenReturn(Optional.of(booking));
        when(userRepository.findById(1L))
                .thenReturn(Optional.empty());
        Exception exc = assertThrows(BadRequestException.class,
                () -> bookingService.updateStatus(Optional.of(1L), Optional.of(1L), true));
        assertEquals("Ошибка user, createBooking.updateStatus", exc.getMessage());
    }

    @Test
    void updateStatusItemIsEmptyTest() {
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.empty());
        when(bookingRepository.findById(1L))
                .thenReturn(Optional.of(booking));
        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));
        Exception exc = assertThrows(BadRequestException.class,
                () -> bookingService.updateStatus(Optional.of(1L), Optional.of(1L), true));
        assertEquals("Ошибка item, createBooking.updateStatus", exc.getMessage());
    }

    @Test
    void updateStatusOwnerNotEqualsTest() {
        User user3 = new User(3L, "User3", "user3@email.ru");
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));
        when(bookingRepository.findById(1L))
                .thenReturn(Optional.of(booking));
        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user3));
        Exception exc = assertThrows(BookingNotFoundException.class,
                () -> bookingService.updateStatus(Optional.of(1L), Optional.of(1L), true));
        assertEquals("Ошибка equals, BookingServiceImpl.updateStatus", exc.getMessage());
    }

    @Test
    void updateStatusAlreadyApprovedTest() {
        Booking wrongBooking = new Booking(1L, LocalDateTime.now(), LocalDateTime.now().plusMonths(2),item, booker, Status.APPROVED);
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));
        when(bookingRepository.findById(1L))
                .thenReturn(Optional.of(wrongBooking));
        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));
        Exception exc = assertThrows(BadRequestException.class,
                () -> bookingService.updateStatus(Optional.of(1L), Optional.of(1L), true));
        assertEquals("Ошибка Status.APPROVED, BookingServiceImpl.updateStatus", exc.getMessage());
    }
}