package ru.practicum.shareit.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

/**
 * TODO Sprint add-item-requests.
 */
@Data
public class ItemRequest {
    private Long id;
    @NotBlank
    private String description;
    private String requestor;
    private LocalDateTime created;
}
