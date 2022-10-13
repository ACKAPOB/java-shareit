package ru.practicum.shareit.requests.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.requests.model.ItemRequest;

import java.util.List;
import java.util.Optional;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {

    @Query("select i from ItemRequest i where i.id = ?1")
    Optional<ItemRequest> findById(Long id);

    List<ItemRequest> findByRequestorIdOrderByCreatedDesc(Long idUser);

    @Query("SELECT i FROM ItemRequest i "
            + "WHERE i.requestor.id <> ?1 ORDER BY i.created DESC")
    Page<ItemRequest> findByItemRequestListRequestor(Long idUser, Pageable pageable);


}
