package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findAllByOwner_id(long userId);

    @Query(value = "SELECT * FROM ITEMS i , USERS u WHERE i.ID = ?1 AND u.ID = i.OWNER_ID ;", nativeQuery = true)
    Item getItemById(long itemId);

    @Query(value = "SELECT * FROM ITEMS i WHERE OWNER_ID = ?1;", nativeQuery = true)
    List<Item> getAllItemByOwnerId(long userId);

}
