package ru.practicum.shareit.item;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Repository
@Data
@Slf4j
public class ItemRepository {
    private final Map<Long, Item> storageItems = new HashMap<>();
    private long generateId = 0;

    private long getGenerateId() {
        return ++generateId;
    }

    public Collection<Item> getStorageItem() {
        return storageItems.values();
    }

    public Item createItem(Item item) {
        item.setId(getGenerateId());
        storageItems.put(item.getId(), item);
        return item;
    }

    public Item updateItem(Item item, long itemId,long userId) {
        log.info("Проверка метод updateItem storageItems{}", getStorageItems());

        if (!storageItems.containsKey(itemId)) {
            return null;
        }

        storageItems.put(itemId, item);
        return item;
    }

    public Item getByIdItem(Long itemId) {
        log.info("Проверка репозитория метод getItemById itemId {}", itemId);
        log.info("Проверка репозитория метод getItemById storageItems {}", storageItems.get(itemId));

        if (!storageItems.containsKey(itemId)) {
            log.info("зашли в if ");
            return null;
        }
        log.info("вышли из if");
        return storageItems.get(itemId);
    }

    public void deleteItem(long itemId) {
        if (storageItems.containsKey(itemId)) {
            return;
        }

        storageItems.remove(itemId);
    }

}
