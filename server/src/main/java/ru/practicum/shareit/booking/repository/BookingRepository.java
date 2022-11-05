package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    Optional<List<Booking>> findByItem_Id(Long itemId);

    Optional<List<Booking>> findByItem_IdAndBooker_id(Long aLong, Long aLong1);

    Page<Booking> findBookingsByBookerIdOrderByStartDesc(Long userId, Pageable pageable);

    @Query("select b from Booking b where b.booker.id = ?1 and ?2 between b.start and b.end order by b.start desc")
    Page<Booking> findByBookingsListStateCurrent(Long idUser, LocalDateTime time, Pageable pageable);

    @Query("select b from Booking b where b.booker.id = ?1 and b.end < ?2 and b.status <> 'REJECTED' order by b.start desc")
    Page<Booking> findByBookingsListStatePast(Long idUser, LocalDateTime time, Pageable pageable);

    @Query("select b from Booking b where b.booker.id = ?1 order by b.start desc")
    Page<Booking> findByBookingsListStateFuture(Long idUser, Pageable pageable);

    @Query("select b from Booking b where b.booker.id = ?1 and b.status = 'WAITING' order by b.start desc")
    Page<Booking> findByBookingsListStateWaiting(Long idUser, Pageable pageable);

    @Query("select b from Booking b where b.booker.id = ?1 and b.status = 'REJECTED' order by b.start desc")
    Page<Booking> findByBookingsListStateRejected(Long idUser, Pageable pageable);

    @Query("select b from Booking b where b.item.owner.id = ?1")
    Page<Booking> findByBookingsOwnerListStateAll(Long idUser, Pageable pageable);

    @Query("select b from Booking b where b.item.owner.id = ?1 and ?2 between b.start and b.end")
    Page<Booking> findByBookingsOwnerListStateCurrent(Long idUser, LocalDateTime time, Pageable pageable);

    @Query("select b from Booking b where b.item.owner.id = ?1 and b.end < ?2 and b.status <> 'REJECTED'")
    Page<Booking> findByBookingsOwnerListStatePast(Long idUser, LocalDateTime time, Pageable pageable);

    @Query("select b from Booking b where b.item.owner.id = ?1")
    Page<Booking> findByBookingsOwnerListStateFuture(Long idUser, Pageable pageable);

    @Query("select b from Booking b where b.item.owner.id = ?1 and b.status = 'WAITING'")
    Page<Booking> findByBookingsOwnerListStateWaiting(Long idUser, Pageable pageable);

    @Query("select b from Booking b where b.item.owner.id = ?1 and b.status = 'REJECTED'")
    Page<Booking> findByBookingsOwnerListStateRejected(Long idUser, Pageable pageable);

}
