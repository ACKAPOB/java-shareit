package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

public interface ItemRepository extends JpaRepository<Item, Long> {

     List<Item> findItemsByOwner(User owner);



    List<Item> findByOwner_IdOrderById(Long idUser);
    @Query("select i from Item i " +
            "where i.request.id = ?1")
    Optional<List<Item>> findByRequestIdOrderByCreated(Long id);
    @Query("select i from Item i " +
            "where upper(i.name) like upper(concat('%', ?1, '%')) " +
            " or upper(i.description) like upper(concat('%', ?1, '%')) " +
            "and i.available is true")
    List<Item> searchListItem(String text);
}
