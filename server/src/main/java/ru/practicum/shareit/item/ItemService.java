package ru.practicum.shareit.item;

import ru.practicum.shareit.comment.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {

    ItemDto createItem(ItemDto itemDto, long userId);

    ItemDto updateItem(ItemDto itemDto, long itemId, long userId);

    ItemDto getByIdItem(long itemId, long userId);

    List<ItemDto> getListItemsByIdUser(long userId, int from, int size);

    List<ItemDto> getListItemsByText(String text, long userId, int from, int size);

    CommentDto createComment(CommentDto commentDto, long bookerId, long itemId);
}
