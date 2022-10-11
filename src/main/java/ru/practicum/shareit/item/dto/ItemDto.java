package ru.practicum.shareit.item.dto;

import lombok.*;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.user.dto.UserDto;
import javax.validation.constraints.NotBlank;

import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ItemDto {

    public ItemDto (Long id, String name, String description, Boolean available, UserDto owner, Long requestId){
        this.id = id;
        this.name = name;
        this.description = description;
        this.available = available;
        this.owner = owner;
        this.requestId = requestId;
    }

    private Long id;
    @NotBlank
    private String name;
    @NotBlank
    private String description;
    @NotBlank
    private Boolean available;
    private UserDto owner;
    private List<CommentDto> comments;
    private Long requestId;
}
