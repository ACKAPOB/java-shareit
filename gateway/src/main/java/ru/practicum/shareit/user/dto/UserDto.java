package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserDto {
    private Long id;
    @NotBlank(message = "GateWay UserDto - Name cannot be empty or null")
    private String name;

    @NotNull(message = "GateWay UserDto - Email cannot be null")
    @Email(message = "GateWay UserDto - Email is mandatory")
    private String email;
}
