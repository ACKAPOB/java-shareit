package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDtoOut;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.booking.model.Status.WAITING;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(controllers = BookingController.class)
public class BookingControllerTest {
    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private MockMvc mvc;
    @MockBean
    BookingService bookingService;
    BookingDtoOut bookingDtoOut;
    Boolean approved;
    private List<BookingDtoOut> bookingList;
    String state;
    Integer from;
    Integer size;
    LocalDateTime localDateTime;

    @BeforeEach
    void beforeEach() {
        mapper.findAndRegisterModules();
        localDateTime = LocalDateTime.now();

        UserDto userDto1 = new UserDto(1L, "user_1", "user_1@mail.com");
        ItemDto itemDto1 = new ItemDto(1L, "user_1", "user_1 description", true, userDto1, 4L);
        bookingDtoOut = new BookingDtoOut(1L, localDateTime, localDateTime.plusMonths(2), WAITING, userDto1, itemDto1);

        approved = true;
        bookingList = new ArrayList<>();
        state = "state";
        from = 1;
        size = 4;
    }

    @Test
    void createBookingTest() throws Exception {
        when(bookingService.createBooking(any(), any())).thenReturn(bookingDtoOut);

        mvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(bookingDtoOut))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(bookingDtoOut)));
    }

    @Test
    void updateStatusTest() throws Exception {
        when(bookingService.updateStatus(any(), any(), any()))
                .thenReturn(bookingDtoOut);

        mvc.perform(patch("/bookings/1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(mapper.writeValueAsString(bookingDtoOut))
                        .param("approved", String.valueOf(approved))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(bookingDtoOut)));
    }

    @Test
    void findBookingIdTest() throws Exception {
        when(bookingService.getBookingById(any(), any()))
                .thenReturn(bookingDtoOut);

        mvc.perform(get("/bookings/1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(bookingDtoOut)));
    }

    @Test
    void findBookingTest() throws Exception {
        when(bookingService.getBookingsState(any(), any(), any(), any()))
                .thenReturn(bookingList);

        mvc.perform(get("/bookings")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size))
                        .param("state", state)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(bookingList)));
    }

    @Test
    void findBookingOwnerTest() throws Exception {
        when(bookingService.getBookingsOwnerState(any(), any(), any(), any()))
                .thenReturn(bookingList);

        mvc.perform(get("/bookings/owner")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size))
                        .param("state", state)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(bookingList)));
    }
}
