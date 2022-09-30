package ru.practicum.shareit.item.dto;

import lombok.*;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.user.Create;
import ru.practicum.shareit.user.model.User;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * TODO Sprint add-controllers.
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ItemDto {

    private Long id;

    @NotBlank(groups = {Create.class}, message = "Name cannot be empty or null")
    private String name;

    @NotBlank(groups = {Create.class}, message = "Description cannot be empty or null")
    private String description; //максимальная длина описания — 200 символов;

    @NotNull(groups = {Create.class}, message = "Available cannot be null")
    private Boolean available; //статус о том, доступна или нет вещь для аренды

    //@NotNull(groups = {Create.class}, message = "Owner cannot be null") // убрал проверку, при создании ДТО пользователь отдельно прилетает
    private User owner; //владелец вещи

   // private Request request; //если вещь была создана по запросу другого пользователя, то в этом поле будет храниться ссылка на соответствующий запрос

}
