package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    Optional<List<Booking>> findByItem_Id(Long itemId);

    Optional<List<Booking>> findByItem_IdAndBooker_id(Long aLong, Long aLong1);

    List<Booking> findBookingsByBookerIdOrderByStartDesc(Long userId);

    @Query("select b from Booking b where b.booker.id = ?1 and ?2 between b.start and b.end order by b.start desc")
    List<Booking> findByBookingsListStateCurrent(Long idUser, LocalDateTime time);

    @Query("select b from Booking b where b.booker.id = ?1 and b.end < ?2 and b.status <> 'REJECTED' order by b.start desc")
    List<Booking> findByBookingsListStatePast(Long idUser, LocalDateTime time);

    @Query("select b from Booking b where b.booker.id = ?1 order by b.start desc")
    List<Booking> findByBookingsListStateFuture(Long idUser);

    @Query("select b from Booking b where b.booker.id = ?1 and b.status = 'WAITING' order by b.start desc")
    List<Booking> findByBookingsListStateWaiting(Long idUser);

    @Query("select b from Booking b where b.booker.id = ?1 and b.status = 'REJECTED' order by b.start desc")
    List<Booking> findByBookingsListStateRejected(Long idUser);

    @Query("select b from Booking b where b.item.owner.id = ?1 order by b.start desc")
    List<Booking> findByBookingsOwnerListStateAll(Long idUser);

    @Query("select b from Booking b where b.item.owner.id = ?1 and ?2 between b.start and b.end order by b.start desc")
    List<Booking> findByBookingsOwnerListStateCurrent(Long idUser, LocalDateTime time);

    @Query("select b from Booking b where b.item.owner.id = ?1 and b.end < ?2 and b.status <> 'REJECTED'  order by b.start desc")
    List<Booking> findByBookingsOwnerListStatePast(Long idUser, LocalDateTime time);

    @Query("select b from Booking b where b.item.owner.id = ?1 order by b.start desc")
    List<Booking> findByBookingsOwnerListStateFuture(Long idUser);

    @Query("select b from Booking b where b.item.owner.id = ?1 and b.status = 'WAITING' order by b.start desc")
    List<Booking> findByBookingsOwnerListStateWaiting(Long idUser);

    @Query("select b from Booking b where b.item.owner.id = ?1 and b.status = 'REJECTED' order by b.start desc")
    List<Booking> findByBookingsOwnerListStateRejected(Long idUser);

}
