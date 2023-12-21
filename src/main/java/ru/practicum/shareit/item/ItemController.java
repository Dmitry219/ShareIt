package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping("/items")
@Slf4j
public class ItemController {
    private final ItemService itemService;

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @PostMapping
    public ItemDto createItem(@Valid @RequestBody ItemDto itemDto,
                           @RequestHeader(value = "X-Sharer-User-Id") long userId){
        log.info("Проверка контроллер метод createItem itemDto {}", itemDto);
        log.info("Проверка контроллер метод createItem userId {}", userId);

        return itemService.createItem(itemDto, userId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestBody ItemDto itemDto,
                              @PathVariable long itemId,
                              @RequestHeader("X-Sharer-User-Id") long userId){
        log.info("Проверка контроллер метод updateItem itemDto {}", itemDto);
        log.info("Проверка контроллер метод updateItem itemId {}", itemId);
        log.info("Проверка контроллер метод updateItem userId {}", userId);
        log.info("Проверка контроллер метод createItem MAP {}", itemService.getByIdItem(itemId));

        return itemService.updateItem(itemDto, itemId, userId);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItemById(@PathVariable long itemId){
        return itemService.getByIdItem(itemId);
    }

    @GetMapping
    public List<ItemDto> getListItemsByIdUser(@RequestHeader("X-Sharer-User-Id") long userId){
        return itemService.getListItemsByIdUser(userId);
    }

    @GetMapping("/search")
    public List<ItemDto> getListItemsByText(@RequestParam String text,
                                            @RequestHeader("X-Sharer-User-Id") long userId){
        return itemService.getListItemsByText(text, userId);
    }
}
