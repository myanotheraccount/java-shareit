package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.model.Comment;

import java.time.LocalDateTime;

public class CommentMapper {
    public static Comment dtoToComment(CommentDto commentDto, Long itemId, Long userId, LocalDateTime dt) {
        return new Comment(
                commentDto.getId(),
                commentDto.getText(),
                itemId,
                userId,
                dt
        );
    }

    public static CommentDto commentToDto(Comment comment, String name) {
        return new CommentDto(
                comment.getId(),
                comment.getText(),
                comment.getCreated(),
                name
        );
    }
}
