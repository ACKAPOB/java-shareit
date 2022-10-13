package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.model.Status;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class BookingDtoById {
    private Long id;
    private Long bookerId;
    private LocalDateTime start;
    private LocalDateTime end;
    private Status status;
}
