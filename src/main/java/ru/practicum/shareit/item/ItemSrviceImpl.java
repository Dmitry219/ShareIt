package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ru.practicum.shareit.exception.AuthorizationFailureException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationUserException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ItemSrviceImpl implements ItemService {
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final ItemMapping itemMapping;

    @Autowired
    public ItemSrviceImpl(UserRepository userRepository, ItemRepository itemRepository, ItemMapping itemMapping) {
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
        this.itemMapping = itemMapping;
    }

    @Override
    public ItemDto createItem(ItemDto itemDto, long userId) {
        log.info("Проверка сервис метод createItem itemDto {}", itemDto);
        log.info("Проверка сервис метод createItem item {}", userId);
        Item item = itemMapping.mapToItem(itemDto);
        checkIdUser(userId);
        item.setOwner(userId);
        itemRepository.createItem(item);
        log.info("Проверка сервис метод createItem item {}", item);
        log.info("Проврка сервиса метод createItem проверяем хранилище {}", itemRepository.getByIdItem(item.getId()));
        return itemMapping.mapToItemDto(item);
    }

    @Override
    public ItemDto updateItem(ItemDto itemDto, long itemId, long userId) {
        log.info("Проверка сервис метод updateItem itemDto {}", itemDto);
        log.info("Проверка сервис метод updateItem itemId {}", itemId);
        log.info("Проверка сервис метод updateItem userId {}", userId);
        log.info("Проверка сервис метод updateItem прорка правильной item {}", itemRepository.getByIdItem(itemId));

        checkIdItem(itemId);
        log.info("Проверка сервис метод updateItem проверка правильного user {}", userRepository.getByIdUser(userId));
        checkIdOwnerItem(itemId, userId);
        checkIdUser(userId);

        Item item = itemMapping.mapToItem(itemDto);
        Item newItem = itemRepository.getByIdItem(itemId);
        if (item.getName() != null) {
            newItem.setName(item.getName());
        }
        if (item.getDescription() != null) {
            newItem.setDescription(item.getDescription());
        }
        if (item.getAvailable() != null) {
            newItem.setAvailable(item.getAvailable());
        }

        itemRepository.updateItem(newItem, itemId, userId);

        log.info("Проверка сервиса метод updateItem проверка правильного хранилища {}",
                itemRepository.getByIdItem(item.getId()));
        return itemMapping.mapToItemDto(newItem);
    }

    @Override
    public ItemDto getByIdItem(long itemId) {
       return itemMapping.mapToItemDto(itemRepository.getByIdItem(itemId));
    }

    @Override
    public List<ItemDto> getListItemsByIdUser(long userId) {
        return itemRepository.getStorageItems().stream()
                .filter(item -> item.getOwner().equals(userId))
                .map(itemMapping::mapToItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> getListItemsByText(String text, long userId) {
        log.info("Проверка сервис метод getListItemsByText проверка text {} , userId {}", text, userId);
        if (text.isBlank()) {
            return new ArrayList<>();
        }
        return itemRepository.getStorageItems().stream()
                .filter(item -> item.getDescription().toLowerCase().contains(text.toLowerCase()) &&
                        item.getAvailable().equals(true))
                .map(itemMapping::mapToItemDto)
                .collect(Collectors.toList());
    }

    private void checkIdItem(long itemId) {
        log.info("Проверка сервис метод checkIdItem проверка itemId");
        if (itemId <= 0) {
            throw new ValidationUserException("ItemId не может быть меньше нуля или равен нулю!");
        } else if (itemRepository.getByIdItem(itemId) == null) {
            throw new NotFoundException("Item с таким Id не существует!");
        }
    }

    private void checkIdUser(long userId) {
        log.info("Проверка сервис метод checkIdUser проверка userId");
        if (userId <= 0) {
            throw new ValidationUserException("userId не может быть меньше нуля или равен нулю!");
        } else if (userRepository.getByIdUser(userId) == null) {
            throw new NotFoundException("User с таким Id не существует!");
        }
    }

    private void checkIdOwnerItem(long itemId, long userId) {
        log.info("Проверка сервис метод checkIdOwnerItem проверка владельца");
        if (itemRepository.getByIdItem(itemId).getOwner() != userId) {
            throw new AuthorizationFailureException("Вещь с таким ID имеет владельца!");
        }
    }
}
