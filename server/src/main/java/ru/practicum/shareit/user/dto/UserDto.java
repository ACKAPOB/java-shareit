package ru.practicum.shareit.user.dto;

import lombok.*;
import ru.practicum.shareit.user.Create;
import ru.practicum.shareit.user.Update;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserDto {
    private Long id;
    @NotBlank(groups = {Create.class}, message = "Name cannot be empty or null")
    private String name;

    @NotNull(groups = {Create.class}, message = "Email cannot be null")
    @Email(groups = {Create.class, Update.class}, message = "Email is mandatory")
    private String email;
}
