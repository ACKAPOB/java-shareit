package ru.practicum.shareit.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDate;

/**
 * TODO Sprint add-item-requests.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemRequest {

    @EqualsAndHashCode.Exclude
    private long id;  //уникальный идентификатор запроса;

    @NotBlank(message = "Description cannot be empty or null")
    @Size(min = 2, max = 200, message = "Description must be between 1 and 200 characters")
    private String description; //текст запроса, содержащий описание требуемой вещи;

    @NotBlank(message = "requestor cannot be empty or null")
    private long requestor;  //пользователь, создавший запрос;

    @NotBlank(message = "created cannot be empty or null")
    private LocalDate created;  //дата и время создания запроса.

}