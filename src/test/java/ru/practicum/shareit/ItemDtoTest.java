package ru.practicum.shareit;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
class ItemDtoTest {
    @Autowired
    private JacksonTester<ItemDto> json;

    @Test
    void testItemDto() throws IOException {
        ItemDto itemDto = new ItemDto(
                1L,
                2L,
                "item",
                "description",
                true,
                3L,
                new BookingShortDto(4L, 5L),
                new BookingShortDto(6L, 7L),
                List.of(new CommentDto(8L, "comment", LocalDateTime.of(2022, 1, 1, 1, 1), "author"))
        );

        JsonContent<ItemDto> result = json.write(itemDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathNumberValue("$.requestId").isEqualTo(2);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("item");
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("description");
        assertThat(result).extractingJsonPathBooleanValue("$.available").isEqualTo(true);
        assertThat(result).extractingJsonPathNumberValue("$.owner").isEqualTo(3);
        assertThat(result).extractingJsonPathNumberValue("$.lastBooking.id").isEqualTo(4);
        assertThat(result).extractingJsonPathNumberValue("$.lastBooking.bookerId").isEqualTo(5);
        assertThat(result).extractingJsonPathNumberValue("$.nextBooking.id").isEqualTo(6);
        assertThat(result).extractingJsonPathNumberValue("$.nextBooking.bookerId").isEqualTo(7);
        assertThat(result).extractingJsonPathNumberValue("$.comments[0].id").isEqualTo(8);
        assertThat(result).extractingJsonPathStringValue("$.comments[0].text").isEqualTo("comment");
        assertThat(result).extractingJsonPathStringValue("$.comments[0].created").isEqualTo("2022-01-01T01:01:00");
        assertThat(result).extractingJsonPathStringValue("$.comments[0].authorName").isEqualTo("author");
    }
}