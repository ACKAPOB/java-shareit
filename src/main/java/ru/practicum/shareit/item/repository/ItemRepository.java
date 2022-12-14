package ru.practicum.shareit.item.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.requests.model.ItemRequest;

import java.util.List;
import java.util.Optional;

public interface ItemRepository extends JpaRepository<Item, Long> {

    List<Item> findByOwner_IdOrderById(Long idUser);

    @Query("select i from Item i " +
            "where i.request.id = ?1")
    Optional<List<Item>> findByRequestIdOrderByCreated(Long id);

    @Query("select i from Item i " +
            "where upper(i.name) like upper(concat('%', ?1, '%')) " +
            " or upper(i.description) like upper(concat('%', ?1, '%')) " +
            "and i.available is true")
    List<Item> searchListItem(String text);

    @Query("SELECT i FROM ItemRequest i "
            + "WHERE i.requestor.id <> ?1 ORDER BY i.created DESC")
    List<ItemRequest> getByItemRequestListRequestor(Long idUser, Pageable pageable);

    @Query("select i from Item i " +
            "where upper(i.name) like upper(concat('%', ?1, '%')) " +
            " or upper(i.description) like upper(concat('%', ?1, '%')) " +
            "and i.available is true")
    List<Item> searchListItemText(String text);
}