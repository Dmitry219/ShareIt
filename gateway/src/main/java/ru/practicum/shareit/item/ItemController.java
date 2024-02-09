package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.comment.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@RestController
@RequestMapping("/items")
@Slf4j
public class ItemController {
    private final ItemClient itemClient;
    private static final String X_SHARER_USER_ID = "X-Sharer-User-Id";

    public ItemController(ItemClient itemClient) {
        this.itemClient = itemClient;
    }

    @PostMapping
    public ResponseEntity<Object> createItem(@Valid @RequestBody ItemDto itemDto,
                                             @RequestHeader(value = X_SHARER_USER_ID)
                                                 @Positive(message = "Id не может быть <= 0")
                                                 @NotNull(message = "Id не может быть null") Long userId) {
        log.info("Проверка контроллер метод createItem itemDto {}", itemDto);
        log.info("Проверка контроллер метод createItem userId {}", userId);

        return itemClient.createItem(itemDto, userId);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@RequestBody ItemDto itemDto,
                                             @PathVariable
                                             @Positive(message = "Id не может быть <= 0")
                                             @NotNull(message = "Id не может быть null") Long itemId,
                                             @RequestHeader(X_SHARER_USER_ID)
                                                 @Positive(message = "Id не может быть <= 0")
                                                 @NotNull(message = "Id не может быть null") Long userId) {
        log.info("Проверка контроллер метод updateItem itemDto {}", itemDto);
        log.info("Проверка контроллер метод updateItem itemId {}", itemId);
        log.info("Проверка контроллер метод updateItem userId {}", userId);

        return itemClient.updateItem(itemDto, itemId, userId);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItemById(@PathVariable
                                                  @Positive(message = "Id не может быть <= 0")
                                                  @NotNull(message = "Id не может быть null") Long itemId,
                                              @RequestHeader(value = X_SHARER_USER_ID)
                                                  @Positive(message = "Id не может быть <= 0")
                                                  @NotNull(message = "Id не может быть null") Long ownerId) {
        return itemClient.getItemById(itemId, ownerId);
    }

    @GetMapping
    public ResponseEntity<Object> getListItemsByIdUser(@RequestHeader(X_SHARER_USER_ID)
                                                           @Positive(message = "Id не может быть <= 0")
                                                           @NotNull(message = "Id не может быть null") Long userId,
                                                       @RequestParam(value = "from", defaultValue = "0")
                                                           @PositiveOrZero(message = "From не может быть < 0") Integer from,
                                                       @RequestParam(value = "size", defaultValue = "10")
                                                           @Positive(message = "Size не может быть <= 0") Integer size) {
        return itemClient.getListItemsByIdUser(userId, from, size);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> getListItemsByText(@RequestParam String text,
                                                     @RequestHeader(X_SHARER_USER_ID)
                                                     @Positive(message = "Id не может быть <= 0")
                                                     @NotNull(message = "Id не может быть null") Long userId,
                                                     @RequestParam(value = "from", defaultValue = "0")
                                                         @PositiveOrZero(message = "From не может быть < 0") Integer from,
                                                     @RequestParam(value = "size", defaultValue = "10")
                                                         @Positive(message = "Size не может быть <= 0") Integer size) {
        return itemClient.getListItemsByText(text, userId, from, size);
    }

    //Создание сомментариев
    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> createComment(@PathVariable
                                                    @Positive(message = "Id не может быть <= 0")
                                                    @NotNull(message = "Id не может быть null") Long itemId,
                                                @RequestHeader(X_SHARER_USER_ID)
                                                @Positive(message = "Id не может быть <= 0")
                                                @NotNull(message = "Id не может быть null") Long userId,
                                                @Valid @RequestBody CommentDto commentDto) {
        return itemClient.createComment(commentDto, userId, itemId);
    }
}
