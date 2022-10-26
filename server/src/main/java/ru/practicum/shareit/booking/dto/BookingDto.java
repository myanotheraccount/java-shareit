package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingDto {
    private Long id;
    private Long itemId;
    private LocalDateTime start;
    private LocalDateTime end;
    private BookingStatus status;
    private UserDto booker;
    private Item item;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Item {
        private Long id;
        private String name;
        private String description;
        private Boolean available;
        private Long owner;
        private BookingShortDto lastBooking;
        private BookingShortDto nextBooking;
        private List<CommentDto> comments;
    }
}
