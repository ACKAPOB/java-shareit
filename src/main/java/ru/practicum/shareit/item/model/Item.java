package ru.practicum.shareit.item.model;

import lombok.*;

/**
 * TODO Sprint add-controllers.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Item {

    @EqualsAndHashCode.Exclude
    private long id;

    @EqualsAndHashCode.Include // Непонятно как Item сравнивать, поусть пока что будет только по имени
    private String name;

    @EqualsAndHashCode.Exclude
    private String description; //максимальная длина описания — 200 символов;

    @EqualsAndHashCode.Exclude
    private Boolean available; //статус о том, доступна или нет вещь для аренды

    @EqualsAndHashCode.Exclude
    private long owner; //владелец вещи

    @EqualsAndHashCode.Exclude
    private Long request; //если вещь была создана по запросу другого пользователя, то в этом поле будет храниться ссылка на соответствующий запрос
}
