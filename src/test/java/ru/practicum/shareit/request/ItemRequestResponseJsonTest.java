package ru.practicum.shareit.request;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.model.ItemDbForRequest;
import ru.practicum.shareit.request.dto.ItemReqListItemsDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class ItemRequestResponseJsonTest {
    @Autowired
    JacksonTester<ItemReqListItemsDto> json;

    @Test
    @SneakyThrows
    void testSerialize() {
        ItemDbForRequest itemDbForRequest = ItemDbForRequest.builder()
                .id(1L)
                .name("nnnn")
                .description("dddd")
                .available(true)
                .requestId(1L)
                .build();

        ItemReqListItemsDto itemReqListItemsDto = ItemReqListItemsDto.builder()
                .id(1L)
                .description("dddd")
                .created(LocalDateTime.now())
                .items(List.of(itemDbForRequest))
                .build();

        JsonContent<ItemReqListItemsDto> result = json.write(itemReqListItemsDto);

        assertThat(result).hasJsonPath("$.id");
        assertThat(result).hasJsonPath("$.description");
        assertThat(result).hasJsonPath("$.created");
        assertThat(result).hasJsonPath("$.items[0].id");
        assertThat(result).hasJsonPath("$.items[0].name");
        assertThat(result).hasJsonPath("$.items[0].description");
        assertThat(result).hasJsonPath("$.items[0].available");

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("dddd");
        assertThat(result).hasJsonPathValue("$.created");

        assertThat(result).extractingJsonPathNumberValue("$.items[0].id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.items[0].name").isEqualTo("nnnn");
        assertThat(result).extractingJsonPathStringValue("$.items[0].description").isEqualTo("dddd");
        assertThat(result).extractingJsonPathBooleanValue("$.items[0].available").isEqualTo(true);
    }
}
