package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class BookingDtoIn {
    @NotBlank
    private Long itemId;
    @NotBlank
    private LocalDateTime start;
    @NotBlank
    private LocalDateTime end;
}
