package ru.practicum.shareit.request;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@RestController
@RequestMapping(path = "/requests")
@Slf4j
public class ItemRequestController {
    private static final String X_SHARER_USER_ID = "X-Sharer-User-Id";
    private final ItemRequestClient itemRequestClient;

    public ItemRequestController(ItemRequestClient itemRequestClient) {
        this.itemRequestClient = itemRequestClient;
    }

    @PostMapping
    public ResponseEntity<Object> createItemRequest(@Valid @RequestBody ItemRequestDto itemRequestDto,
                                                    @RequestHeader(value = X_SHARER_USER_ID)
                                                    @Positive(message = "Id не может быть <= 0")
                                                    @NotNull(message = "Id не может быть null") Long userId) {
        log.info("Проверка контроллер метод createItemRequest itemRequestDto {}", itemRequestDto);
        log.info("Проверка контроллер метод createItemRequest userId {}", userId);

        return itemRequestClient.createItemRequest(itemRequestDto, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getListItemRequestDtoByIdUser(@RequestHeader(X_SHARER_USER_ID)
                                                                    @Positive(message = "Id не может быть <= 0")
                                                                    @NotNull(message = "Id не может быть null") Long userId) {
        log.info("Проверка контроллер метод getListItemRequestDtoByIdUser userId {}", userId);
        return itemRequestClient.getListItemRequestDtoByIdUser(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getListAllItemRequestDtoExpectByIdUser(@RequestHeader(X_SHARER_USER_ID)
                                                                             @Positive(message = "Id не может быть <= 0")
                                                                             @NotNull(message = "Id не может быть null") Long userId,
                                                                         @RequestParam(value = "from", defaultValue = "0")
                                                                             @PositiveOrZero(message = "From не может быть < 0") Integer from,
                                                                         @RequestParam(value = "size", defaultValue = "10")
                                                                             @Positive(message = "Size не может быть <= 0") Integer size) {
        log.info("Проверка контроллер метод getListAllItemRequestDtoExpectByIdUser userId {}", userId);
        return itemRequestClient.getListAllItemRequestDtoExpectByIdUser(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getItemRequestById(@PathVariable
                                                         @Positive(message = "Id не может быть <= 0")
                                                         @NotNull(message = "Id не может быть null") Long requestId,
                                                     @RequestHeader(value = X_SHARER_USER_ID)
                                                     @Positive(message = "Id не может быть <= 0")
                                                     @NotNull(message = "Id не может быть null") Long userId) {
        return itemRequestClient.getItemRequestById(requestId, userId);
    }
}
