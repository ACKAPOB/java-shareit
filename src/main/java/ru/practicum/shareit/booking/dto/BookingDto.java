package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.Create;
import ru.practicum.shareit.user.model.User;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

/**
 * TODO Sprint add-bookings.
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class BookingDto {

    private Long id;
    @NotBlank(groups = {Create.class}, message = "startDate cannot be empty or null")
    private LocalDateTime startDate;
    @NotBlank(groups = {Create.class}, message = "endDate cannot be empty or null")
    private LocalDateTime endDate;
    @NotBlank(groups = {Create.class}, message = "item cannot be empty or null")
    private Item item;
    @NotBlank(groups = {Create.class}, message = "user cannot be empty or null")
    private User user;
    @NotBlank(groups = {Create.class}, message = "status cannot be empty or null")
    private Status status;
}
