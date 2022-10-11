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
    public  Booking(LocalDateTime start, LocalDateTime end) {
        this.start = start;
        this.end = end;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "start_date", nullable = false)
    private LocalDateTime start;
    @Column(name = "end_date", nullable = false)
    private LocalDateTime end;
    @ManyToOne(fetch=FetchType.EAGER,
            cascade=CascadeType.ALL)
    @JoinColumn(name="item_id")
    private Item item;
    @ManyToOne(fetch=FetchType.EAGER,
            cascade=CascadeType.ALL)
    @JoinColumn(name="booker_id")
    private User booker;
    @Enumerated(EnumType.STRING)
    private Status status;
}