package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoOut;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.requests.model.ItemRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ItemMapperTest {
    private final LocalDateTime localDateTime = LocalDateTime.now();
    private Item item;
    private ItemDto itemDto;
    private List<Item> list;
    private CommentDto commentDto;
    private List<Comment> comments;
    private Comment comment;

    @BeforeEach
    void beforeEach() {
        User user1 = new User(1L, "user1", "user1@mail.com");
        User user2 = new User(2L, "user2", "user2@mail.com");
        ItemRequest itemRequest = new ItemRequest(1L, "description", user1, localDateTime.minusMonths(2));
        item = new Item(1L,"item","desc",true, user2, itemRequest);
        UserDto userDto = new UserDto(user2.getId(), user2.getName(), user2.getEmail());

        itemDto = new ItemDto(item.getId(), item.getName(), item.getDescription(), item.getAvailable(),
                userDto, item.getRequest().getId());

        comment = new Comment(1L, item, user1, "text", localDateTime);
        comments = Collections.singletonList(comment);
        commentDto = new CommentDto(comment.getId(), comment.getText(), itemDto,
                comment.getAuthor().getName(), comment.getCreationTime());
        list = Collections.singletonList(item);
    }

    @Test
    void toItemDtoTest() {
        ItemDto result = ItemMapper.toItemDto(item);
        assertEquals(itemDto.getId(), result.getId());
        assertEquals(itemDto.getName(), result.getName());
        assertEquals(itemDto.getDescription(), result.getDescription());
        assertEquals(itemDto.getAvailable(), result.getAvailable());
        assertEquals(itemDto.getOwner().getName(), result.getOwner().getName());
        assertEquals(itemDto.getRequestId(), result.getRequestId());
    }

    @Test
    void toItemTest() {
        Item result = ItemMapper.toItem(itemDto);
        assertEquals(result.getId(), itemDto.getId());
        assertEquals(result.getName(), itemDto.getName());
        assertEquals(result.getDescription(), itemDto.getDescription());
        assertEquals(result.getAvailable(), itemDto.getAvailable());
    }

    @Test
    void toListItemDtoTest() {
        List<ItemDto> result = ItemMapper.toListItemDto(list);
        assertEquals(result.size(), 1);
        assertEquals(result.get(0).getId(), 1L);
    }

    @Test
    void toItemDtoOutTest() {
        ItemDtoOut result = ItemMapper.toItemDtoOut(item);
        assertEquals(result.getId(), item.getId());
        assertEquals(result.getName(), item.getName());
        assertEquals(result.getDescription(), item.getDescription());
        assertEquals(result.getAvailable(), item.getAvailable());
    }

    @Test
    void toListItemDtoLastNextTest() {
        List<CommentDto> result = ItemMapper.toListItemDtoLastNext(comments);
        assertEquals(result.size(), comments.size());
        assertEquals(result.get(0).getId(), comments.get(0).getId());
        assertEquals(result.get(0).getItem().getName(), comments.get(0).getItem().getName());
        assertEquals(result.get(0).getText(), comments.get(0).getText());
    }

    @Test
    void toCommentTest() {
        Comment result = ItemMapper.toComment(commentDto);
        assertEquals(result.getId(), commentDto.getId());
        assertEquals(result.getText(), commentDto.getText());
    }

    @Test
    void toCommentDtoTest() {
        CommentDto result = ItemMapper.toCommentDto(comment);
        assertEquals(result.getId(), commentDto.getId());
        assertEquals(result.getItem().getName(), commentDto.getItem().getName());
        assertEquals(result.getText(), commentDto.getText());
        assertEquals(result.getAuthorName(), commentDto.getAuthorName());
    }

}
