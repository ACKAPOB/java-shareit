package ru.practicum.shareit.item.model;

import lombok.*;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;

/**
 * TODO Sprint add-controllers.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "items")
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String description;
    private Boolean available; //статус о том, доступна или нет вещь для аренды

    @ManyToOne
    @JoinColumn(name = "owner_id")
    private User owner; //владелец вещи

    @ManyToOne
    @JoinColumn(name = "request_id")
    private Request request;
    //если вещь была создана по запросу другого пользователя, то в этом поле будет храниться ссылка на соответствующий запрос
}
