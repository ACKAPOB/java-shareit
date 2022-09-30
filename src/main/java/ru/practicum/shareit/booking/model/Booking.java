package ru.practicum.shareit.booking.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "bookings")
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "start_date")
    private LocalDateTime startDate;
    @Column(name = "end_date")
    private LocalDateTime endDate;
    @ManyToOne
    @JoinColumn(name = "item_id")
    private Item item;
    @ManyToOne
    @JoinColumn(name = "booker_id")
    private User user;
    @Enumerated(EnumType.STRING)
    private Status status;
}

/*
    start_date  TIMESTAMP WITHOUT TIME ZONE                     NOT NULL,
    end_date    TIMESTAMP WITHOUT TIME ZONE                     NOT NULL,
    item_id     BIGINT                                          NOT NULL,
    booker_id BIGINT                                            NOT NULL,
    status VARCHAR(255)                                         NOT NULL,
 */