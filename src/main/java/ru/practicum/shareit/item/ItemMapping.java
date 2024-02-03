package ru.practicum.shareit.item;

import ru.practicum.shareit.booking.dto.BookingDtoShort;
import ru.practicum.shareit.comment.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemDbForRequest;

import java.util.ArrayList;
import java.util.List;

public class ItemMapping {

    public static ItemDto mapToItemDto(Item item) {

        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .available(item.isAvailable())
                .description(item.getDescription())
                .comments(new ArrayList<>())
                .build();
    }

    public static Item mapToItem(ItemDto itemDto) {

        return Item.builder()
                .id(itemDto.getId())
                .name(itemDto.getName())
                .available(itemDto.getAvailable())
                .description(itemDto.getDescription())
                .build();
    }

    public static ItemDto mapToItemDtoBookingByIdOwner(Item item,
                                                       BookingDtoShort lastBooking,
                                                       BookingDtoShort nextBooking,
                                                       List<CommentDto> comments) {

        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .available(item.isAvailable())
                .description(item.getDescription())
                .lastBooking(lastBooking)
                .nextBooking(nextBooking)
                .comments(comments)
                .build();
    }

    public static ItemDbForRequest mapToItemDbForRequest(Item item) {

        return ItemDbForRequest.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.isAvailable())
                .requestId(item.getRequestId().getId())
                .build();
    }

}
