package ru.practicum.shareit.item.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ItemDbForRequest {
    private Long id;
    private String name;
    private String description;
    private boolean available;
    private Long requestId;
}
