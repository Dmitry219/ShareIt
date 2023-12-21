package ru.practicum.shareit.item;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

@Service
public interface ItemService {

    ItemDto createItem(ItemDto itemDto, long userId);

    ItemDto updateItem(ItemDto itemDto, long itemId, long userId);

    ItemDto getByIdItem(long itemId);

    List<ItemDto> getListItemsByIdUser(long userId);

    List<ItemDto> getListItemsByText(String text, long userId);
}
