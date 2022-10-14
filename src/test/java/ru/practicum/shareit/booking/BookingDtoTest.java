package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;


@JsonTest
class BookingDtoTest {

    @Autowired
    private JacksonTester<BookingDto> json;

    @Test
    void testBookingDto() throws IOException {
        BookingDto bookingDto = new BookingDto(
                1L,
                2L,
                LocalDateTime.of(2022, 1, 1, 1, 1),
                LocalDateTime.of(2022, 1, 2, 1, 1),
                BookingStatus.ALL,
                new UserDto(3L, "name", "name@mail.ru"),
                new BookingDto.Item(
                        4L,
                        "item",
                        "description",
                        true,
                        5L,
                        new BookingShortDto(6L, 7L),
                        new BookingShortDto(8L, 9L),
                        List.of(new CommentDto(10L, "comment", LocalDateTime.of(2022, 1, 1, 1, 1), "author"))
                )
        );

        JsonContent<BookingDto> result = json.write(bookingDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathNumberValue("$.itemId").isEqualTo(2);
        assertThat(result).extractingJsonPathStringValue("$.start").isEqualTo("2022-01-01T01:01:00");
        assertThat(result).extractingJsonPathStringValue("$.end").isEqualTo("2022-01-02T01:01:00");
        assertThat(result).extractingJsonPathStringValue("$.status").isEqualTo("ALL");
        assertThat(result).extractingJsonPathNumberValue("$.booker.id").isEqualTo(3);
        assertThat(result).extractingJsonPathStringValue("$.booker.name").isEqualTo("name");
        assertThat(result).extractingJsonPathStringValue("$.booker.email").isEqualTo("name@mail.ru");
        assertThat(result).extractingJsonPathNumberValue("$.item.id").isEqualTo(4);
        assertThat(result).extractingJsonPathStringValue("$.item.name").isEqualTo("item");
        assertThat(result).extractingJsonPathStringValue("$.item.description").isEqualTo("description");
        assertThat(result).extractingJsonPathBooleanValue("$.item.available").isEqualTo(true);
        assertThat(result).extractingJsonPathNumberValue("$.item.owner").isEqualTo(5);
        assertThat(result).extractingJsonPathNumberValue("$.item.lastBooking.id").isEqualTo(6);
        assertThat(result).extractingJsonPathNumberValue("$.item.lastBooking.bookerId").isEqualTo(7);
        assertThat(result).extractingJsonPathNumberValue("$.item.nextBooking.id").isEqualTo(8);
        assertThat(result).extractingJsonPathNumberValue("$.item.nextBooking.bookerId").isEqualTo(9);
        assertThat(result).extractingJsonPathNumberValue("$.item.comments[0].id").isEqualTo(10);
        assertThat(result).extractingJsonPathStringValue("$.item.comments[0].text").isEqualTo("comment");
        assertThat(result).extractingJsonPathStringValue("$.item.comments[0].created").isEqualTo("2022-01-01T01:01:00");
        assertThat(result).extractingJsonPathStringValue("$.item.comments[0].authorName").isEqualTo("author");
    }
}