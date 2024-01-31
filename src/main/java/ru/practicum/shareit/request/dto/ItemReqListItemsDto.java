package ru.practicum.shareit.request.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemDbForRequest;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class ItemReqListItemsDto {
    private Long id;
    @NotBlank
    private String description;
    private LocalDateTime created;
    private List<ItemDbForRequest> items;
}
