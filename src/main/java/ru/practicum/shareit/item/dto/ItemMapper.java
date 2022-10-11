package ru.practicum.shareit.item.dto;

import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.requests.dto.ItemRequestMapper;
import ru.practicum.shareit.user.dto.UserMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor
@Component
public class ItemMapper {

        public static ItemDto toItemDto(Item item) {
            return new ItemDto(
                    item.getId(),
                    item.getName(),
                    item.getDescription(),
                    item.getAvailable(),
                    UserMapper.toUserDto(item.getOwner()),
                    item.getRequest() != null ? item.getRequest().getId() : item.getId()
            );
        }
        public static List<CommentDto> toListCommentDto (List<Comment> comments) {
            return comments.stream().map(ItemMapper::toCommentDto).collect(Collectors.toList());
        }
        public static Item toItem(ItemDto itemDto) {
            return new Item (
                    itemDto.getId(),
                    itemDto.getName(),
                    itemDto.getDescription(),
                    itemDto.getAvailable()
            );
        }

        public static List<ItemDto> toListItemDto (List<Item> item) {
            return item.stream().map(ItemMapper::toItemDto).collect(Collectors.toList());
        }

        public static ItemDtoLastNext toItemDtoLastNext(Item item) {
            return new ItemDtoLastNext(
                    item.getId(),
                    item.getName(),
                    item.getDescription(),
                    item.getAvailable());
        }

        public static List<CommentDto> toListItemDtoLastNext(List<Comment> comments) {
            return comments.stream().map(ItemMapper::toCommentDto).collect(Collectors.toList());
        }

        public static Comment toComment(CommentDto commentDto) {
            return new Comment (
                    commentDto.getId(),
                    commentDto.getText()
            );
        }
        public static CommentDto toCommentDto(Comment comment) {
            return new CommentDto (
                    comment.getId(),
                    comment.getText(),
                    toItemDto(comment.getItem()),
                    comment.getAuthor().getName(),
                    comment.getCreationTime()
            );
        }
    }