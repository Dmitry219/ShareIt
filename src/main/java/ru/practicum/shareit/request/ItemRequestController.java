package ru.practicum.shareit.request;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemReqListItemsDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.List;

/**
 * TODO Sprint add-item-requests.
 */
@RestController
@RequestMapping(path = "/requests")
@Slf4j
public class ItemRequestController {
    private static final String X_SHARER_USER_ID = "X-Sharer-User-Id";
    private final ItemRequestService itemRequestService;

    public ItemRequestController(ItemRequestService itemRequestService) {
        this.itemRequestService = itemRequestService;
    }

    @PostMapping
    public ItemRequestDto createItemRequest(@Valid @RequestBody ItemRequestDto itemRequestDto,
                                 @RequestHeader(value = X_SHARER_USER_ID) long userId) {
        log.info("Проверка контроллер метод createItemRequest itemRequestDto {}", itemRequestDto);
        log.info("Проверка контроллер метод createItemRequest userId {}", userId);

        return itemRequestService.createItemRequest(itemRequestDto, userId);
    }

    @GetMapping
    public List<ItemReqListItemsDto> getListItemRequestDtoByIdUser(@RequestHeader(X_SHARER_USER_ID) long userId) {
        log.info("Проверка контроллер метод getListItemRequestDtoByIdUser userId {}", userId);
        return itemRequestService.getListItemRequestDtoByIdUser(userId);
    }

    @GetMapping("/all")
    public List<ItemReqListItemsDto> getListAllItemRequestDtoExpectByIdUser(@RequestHeader(X_SHARER_USER_ID) long userId,
                                                                            @RequestParam(value = "from", defaultValue = "0") @Positive int from,
                                                                            @RequestParam(value = "size", defaultValue = "10") @Positive int size) {
        log.info("Проверка контроллер метод getListAllItemRequestDtoExpectByIdUser userId {}", userId);
        return itemRequestService.getListAllItemRequestDtoExpectByIdUser(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ItemReqListItemsDto getItemRequestById(@PathVariable long requestId,
                               @RequestHeader(value = X_SHARER_USER_ID)  long userId) {
        return itemRequestService.getByItemRequestId(requestId, userId);
    }
}
