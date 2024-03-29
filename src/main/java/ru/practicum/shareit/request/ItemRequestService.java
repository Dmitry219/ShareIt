package ru.practicum.shareit.request;

import ru.practicum.shareit.request.dto.ItemReqListItemsDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDto createItemRequest(ItemRequestDto itemRequestDto, Long userId);

    List<ItemReqListItemsDto> getListItemRequestDtoByIdUser(long userId);

    List<ItemReqListItemsDto> getListAllItemRequestDtoExpectByIdUser(long requestorId, int from, int size);

    ItemReqListItemsDto getByItemRequestId(long requestId, long userId);
}
