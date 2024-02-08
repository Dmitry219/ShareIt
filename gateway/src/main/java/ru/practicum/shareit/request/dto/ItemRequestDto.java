package ru.practicum.shareit.request.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Data
@Builder
public class ItemRequestDto {
    private Long id;
    @NotBlank
    private String description;
    @FutureOrPresent
    private LocalDateTime created;
}
