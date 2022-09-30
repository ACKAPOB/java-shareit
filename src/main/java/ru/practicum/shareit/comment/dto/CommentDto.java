package ru.practicum.shareit.comment.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.Create;
import ru.practicum.shareit.user.model.User;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CommentDto {
    private long id;
    @NotBlank(groups = {Create.class}, message = "Name cannot be empty or null")
    private String text;
    @NotNull(groups = {Create.class},message = "item cannot be null")
    private Item item;
    @NotNull(groups = {Create.class},message = "user cannot be null")
    private User user;
}
