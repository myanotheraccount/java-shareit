package ru.practicum.shareit.itemRequest;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
class ItemRequestDtoTest {
    @Autowired
    private JacksonTester<ItemRequestDto> json;

    @Test
    void testItemRequsetDto() throws IOException {
        ItemRequestDto itemRequestDto = new ItemRequestDto(
                1L,
                2L,
                "description",
                LocalDateTime.of(2022, 1, 1, 1, 1),
                List.of(new ItemRequestDto.Item(
                        3L,
                        "name",
                        "item description",
                        true,
                        4L
                ))
        );

        JsonContent<ItemRequestDto> result = json.write(itemRequestDto);
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathNumberValue("$.authorId").isEqualTo(2);
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("description");
        assertThat(result).extractingJsonPathStringValue("$.created").isEqualTo("2022-01-01T01:01:00");
        assertThat(result).extractingJsonPathNumberValue("$.items[0].id").isEqualTo(3);
        assertThat(result).extractingJsonPathStringValue("$.items[0].name").isEqualTo("name");
        assertThat(result).extractingJsonPathStringValue("$.items[0].description").isEqualTo("item description");
        assertThat(result).extractingJsonPathBooleanValue("$.items[0].available").isEqualTo(true);
        assertThat(result).extractingJsonPathNumberValue("$.items[0].requestId").isEqualTo(4);
    }
}