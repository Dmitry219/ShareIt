package ru.practicum.shareit.item;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
//-----------------------новафя реализация по запросам
    @Query(value = "SELECT * FROM ITEMS WHERE REQUEST_ID = ?1 ; ", nativeQuery = true)
    List<Item> getAllItemsByRequestId(long requestId);

    @Query(value = "select i from Item i where lower(i.name) like lower(concat('%', ?1, '%')) or lower(i.description) like lower(concat('%', ?1, '%')) and i.available = TRUE")
    List<Item> findAllByText(String text, PageRequest pageRequest);
//------------------------конец новой реализации
    List<Item> findAllByOwner_id(long userId, PageRequest pageRequest);

    @Query(value = "SELECT * FROM ITEMS i , USERS u WHERE i.ID = ?1 AND u.ID = i.OWNER_ID ;", nativeQuery = true)
    Item getItemById(long itemId);

    @Query(value = "SELECT * FROM ITEMS i WHERE OWNER_ID = ?1;", nativeQuery = true)
    List<Item> getAllItemByOwnerId(long userId);

}
