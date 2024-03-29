package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.comment.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping("/items")
@Slf4j
public class ItemController {
    private final ItemService itemService;
    private static final String X_SHARER_USER_ID = "X-Sharer-User-Id";

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @PostMapping
    public ItemDto createItem(@Valid @RequestBody ItemDto itemDto,
                           @RequestHeader(value = X_SHARER_USER_ID) long userId) {
        log.info("Проверка контроллер метод createItem itemDto {}", itemDto);
        log.info("Проверка контроллер метод createItem userId {}", userId);

        return itemService.createItem(itemDto, userId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestBody ItemDto itemDto,
                              @PathVariable long itemId,
                              @RequestHeader(X_SHARER_USER_ID) long userId) {
        log.info("Проверка контроллер метод updateItem itemDto {}", itemDto);
        log.info("Проверка контроллер метод updateItem itemId {}", itemId);
        log.info("Проверка контроллер метод updateItem userId {}", userId);

        return itemService.updateItem(itemDto, itemId, userId);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItemById(@PathVariable long itemId,
                               @RequestHeader(value = X_SHARER_USER_ID)  long ownerId) {
        return itemService.getByIdItem(itemId, ownerId);
    }

    @GetMapping
    public List<ItemDto> getListItemsByIdUser(@RequestHeader(X_SHARER_USER_ID) long userId,
                                              @RequestParam(value = "from", defaultValue = "0") @Positive int from,
                                              @RequestParam(value = "size", defaultValue = "10") @Positive int size) {
        return itemService.getListItemsByIdUser(userId, from, size);
    }

    @GetMapping("/search")
    public List<ItemDto> getListItemsByText(@RequestParam String text,
                                            @RequestHeader(X_SHARER_USER_ID) long userId,
                                            @RequestParam(value = "from", defaultValue = "0") @Positive int from,
                                            @RequestParam(value = "size", defaultValue = "10") @Positive int size) {
        return itemService.getListItemsByText(text, userId, from, size);
    }

    //Создание сомментариев
    @PostMapping("/{itemId}/comment")
    public CommentDto createComment(@PathVariable long itemId,
                                    @RequestHeader(X_SHARER_USER_ID) long userId,
                                    @Valid @RequestBody CommentDto commentDto) {
        return itemService.createComment(commentDto, userId, itemId);
    }
}
