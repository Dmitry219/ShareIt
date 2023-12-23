package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {

    ItemDto createItem(ItemDto itemDto, long userId);

    ItemDto updateItem(ItemDto itemDto, long itemId, long userId);

    ItemDto getByIdItem(long itemId);

    List<ItemDto> getListItemsByIdUser(long userId);

    List<ItemDto> getListItemsByText(String text, long userId);
}
