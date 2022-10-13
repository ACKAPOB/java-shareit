package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingDtoById;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.requests.dto.ItemRequestDto;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ItemDtoOut {

    public ItemDtoOut(Long id, String name, String description, Boolean available) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.available = available;
    }

    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private BookingDtoById lastBooking;
    private BookingDtoById nextBooking;
    private List<CommentDto> comments;
    private ItemRequestDto request;
}
