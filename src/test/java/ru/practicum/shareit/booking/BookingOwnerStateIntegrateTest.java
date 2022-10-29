package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.booking.dto.BookingDtoOut;
import ru.practicum.shareit.booking.exception.BadRequestException;
import ru.practicum.shareit.booking.exception.MessageFailedException;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.booking.service.impl.BookingServiceImpl;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.requests.model.ItemRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static ru.practicum.shareit.booking.model.Status.WAITING;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Transactional
@ExtendWith(MockitoExtension.class)
public class BookingOwnerStateIntegrateTest {
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BookingService bookingService = new BookingServiceImpl(bookingRepository, itemRepository,
            userRepository);

    private final LocalDateTime start = LocalDateTime.now().plusMonths(1);
    private final LocalDateTime end = LocalDateTime.now().plusMonths(2);
    BookingDtoOut bookingDtoByIdTest;

    @BeforeEach
    void beforeEach() {
        User user1 = new User(1L, "User1", "User1@mail.com");
        User user2 = new User(2L, "User2", "User2@mail.ru");
        UserDto userDto = new UserDto(1L, "User1", "User1@mail.com");
        ItemRequest itemRequest = new ItemRequest(1L, "description", user1,
                LocalDateTime.of(2022, 4, 4, 4, 4, 4));
        Item item = new Item(1L, "отвертка", "хорошая", true, user2, itemRequest);
        ItemDto itemDto = new ItemDto(1L, "отвертка", "хорошая", true, userDto, 1L);
        BookingDtoIn bookingDtoIn = new BookingDtoIn(1L, start, end);
        BookingDtoOut bookingDtoOut = new BookingDtoOut(1L, start, end, WAITING, userDto, itemDto);
        userRepository.save(user1);
        userRepository.save(user2);
        itemRepository.save(item);

        bookingDtoByIdTest = bookingService.createBooking(Optional.of(1L), Optional.of(bookingDtoIn));
    }

    @Test
    void createBookingTest() throws BadRequestException {
        Assertions.assertEquals(1L, bookingDtoByIdTest.getId());
        Assertions.assertEquals(start, bookingDtoByIdTest.getStart());
        Assertions.assertEquals(end, bookingDtoByIdTest.getEnd());
    }

    @Test
    void getBookingsStateTest() throws BadRequestException, MessageFailedException {
        List<BookingDtoOut> bookingDtoOutListTest = bookingService.getBookingsState(Optional.of(1L),
                Optional.of(0), Optional.of(2), "ALL");
        Assertions.assertEquals(1L, bookingDtoOutListTest.get(0).getId());
        Assertions.assertEquals(start.getMinute(), bookingDtoOutListTest.get(0).getStart().getMinute());
        Assertions.assertEquals(end.getMinute(), bookingDtoOutListTest.get(0).getEnd().getMinute());
    }
}
