package ru.practicum.shareit.item;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dto.BookingDtoById;
import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.booking.dto.BookingDtoOut;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.comment.repository.CommentRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoOut;
import ru.practicum.shareit.item.exception.BadRequestException;
import ru.practicum.shareit.item.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.item.service.impl.ItemServiceImpl;
import ru.practicum.shareit.requests.dto.ItemRequestDto;
import ru.practicum.shareit.requests.model.ItemRequest;
import ru.practicum.shareit.requests.repository.ItemRequestRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.*;

@ExtendWith(MockitoExtension.class)
public class ItemServiceImplTest {
    private final BookingRepository bookingRepository = Mockito.mock(BookingRepository.class);
    private final ItemRepository itemRepository = Mockito.mock(ItemRepository.class);
    private final UserRepository userRepository = Mockito.mock(UserRepository.class);
    private final CommentRepository commentRepository = Mockito.mock(CommentRepository.class);
    private final ItemRequestRepository itemRequestRepository = Mockito.mock(ItemRequestRepository.class);

    private final ItemService itemService = new ItemServiceImpl(itemRepository, userRepository,
            commentRepository, bookingRepository, itemRequestRepository);

    private Item item;
    private ItemDto itemDto;
    private CommentDto commentDto;
    private Comment comment;
    private User user, userNew;
    private UserDto userDto;
    private BookingDtoById bookingDtoById;
    private List<Booking> list;
    private List<ItemDto> listItemDto;

    private List<BookingDtoOut> bookingList;
    private ItemRequest itemRequest;

    @BeforeEach
    void setUp() throws Exception {
        user = new User(1L, "user1", "user1@mail.com");
        userNew = new User(2L,"user2","user2@mail.com");
        userDto = new UserDto(1L,"user1","user1@mail.com");
        item = new Item(1L,"item","desc",true, userNew, itemRequest);
        itemDto = new ItemDto(1L,"item","desc",true, userDto,1L);
        comment = new Comment(1L, item, user, "text", LocalDateTime.now());
        commentDto = new CommentDto(1L,"text", itemDto, "user1", LocalDateTime.now());
        LocalDateTime localDateTime = LocalDateTime.now();

        ItemDtoOut itemDtoLastNext = new ItemDtoOut(1L, "user1", "description", true, bookingDtoById,
                new BookingDtoById(1L, 1L, localDateTime, localDateTime.plusMonths(2), Status.WAITING),
                List.of(new CommentDto("comment")),
                new ItemRequestDto(1L, "desc", userDto, localDateTime.minusMonths(2), listItemDto));

        Booking booking = new Booking(1L, LocalDateTime.now(), localDateTime.plusMonths(2), item, userNew, Status.REJECTED);

        bookingDtoById = new BookingDtoById(1L, 1L, localDateTime,
                localDateTime.plusMonths(2), Status.WAITING);

        BookingDtoIn bookingDtoIn = new BookingDtoIn(1L, localDateTime, localDateTime.plusMonths(2));

        itemRequest = new ItemRequest(1L,"description", user, localDateTime.minusMonths(2));
        list = Collections.singletonList(booking);
        List<Item> listItem = Collections.singletonList(item);
        listItemDto = Collections.singletonList(itemDto);
        List<CommentDto> listCommentDto = Collections.singletonList(commentDto);
        String state = "state";
        Integer from = 1;
        Integer size = 4;
    }

    @Test
    void createItemTest() {
        Mockito
                .when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));
        Mockito
                .when(itemRequestRepository.findById(1L))
                .thenReturn(Optional.of(itemRequest));
        Mockito
                .when(itemRepository.save(item))
                .thenReturn(item);
        ItemDto itemDtoTest = itemService.createItem(Optional.of(1L), itemDto);
        Assertions.assertEquals(1L, itemDtoTest.getId());
        Assertions.assertEquals("item", itemDtoTest.getName());
        Assertions.assertEquals("user1", itemDtoTest.getOwner().getName());
    }

    @Test
    void saveItemWithoutNameTest() {
        Mockito
                .when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));
        Mockito
                .when(itemRequestRepository.findById(1L))
                .thenReturn(Optional.of(itemRequest));
        Mockito
                .when(itemRepository.save(item))
                .thenReturn(item);
        Assertions.assertThrows(BadRequestException.class, () -> itemService.createItem(Optional.of(1L),
                new ItemDto(1L,"item",null,true, userDto,1L)));
    }

    @Test
    void saveItemWithoutNDescTest() {
        Mockito
                .when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        Mockito
                .when(itemRequestRepository.findById(1L)).thenReturn(Optional.of(itemRequest));
        Mockito
                .when(itemRepository.save(item)).thenReturn(item);
        Assertions.assertThrows(BadRequestException.class, () -> itemService.createItem(Optional.of(1L),
                new ItemDto(1L,"item",null,true, userDto, 1L)));
    }

    @Test
    void saveItemWithoutAvailableTest() {
        Mockito
                .when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        Mockito
                .when(itemRequestRepository.findById(1L)).thenReturn(Optional.of(itemRequest));
        Mockito
                .when(itemRepository.save(item)).thenReturn(item);
        Assertions.assertThrows(BadRequestException.class, () -> itemService.createItem(Optional.of(1L),
                new ItemDto(1L,"item","null",null, userDto, 1L)));
    }

    @Test
    void getItemTest() {
        Mockito
                .when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));
        Mockito
                .when(userRepository.findById(2L))
                .thenReturn(Optional.of(userNew));
        Mockito
                .when(itemRepository.findById(1L))
                .thenReturn(Optional.ofNullable(item));
        Mockito
                .when(commentRepository.findByItemId(1L))
                .thenReturn(List.of(new Comment(1L, item, user,"text", LocalDateTime.now())));
        Mockito
                .when(bookingRepository.findByItem_Id(1L)).thenReturn(Optional.of(list));
        ItemDtoOut itemTest = itemService.getItemById(Optional.of(1L), Optional.of(1L));
        Assertions.assertEquals(1L, itemTest.getId());
        Assertions.assertEquals("item", itemTest.getName());
        Assertions.assertEquals("desc", itemTest.getDescription());
        Assertions.assertEquals(1L, itemTest.getComments().get(0).getId());

        ItemDtoOut itemTest2 = itemService.getItemById(Optional.of(2L), Optional.of(1L));
        Assertions.assertEquals(1L, itemTest2.getId());
        Assertions.assertEquals("item", itemTest2.getName());
        Assertions.assertEquals("desc", itemTest2.getDescription());
    }

    @Test
    void getItemWrongNullTest() {
        Mockito
                .when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        Mockito
                .when(itemRepository.findById(1L)).thenReturn(Optional.ofNullable(item));
        Assertions.assertThrows(BadRequestException.class, () -> itemService.getItemById(Optional.of(1L), Optional.empty()));
    }

    @Test
    void getItemWrongIdTest() {
        Mockito
                .when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        Mockito
                .when(itemRepository.findById(1L)).thenReturn(Optional.ofNullable(item));
        Assertions.assertThrows(NotFoundException.class, () -> itemService.getItemById(Optional.of(1L), Optional.of(100L)));
    }

    @Test
    void updateItemWrongOwnerTest() {
        Mockito
                .when(userRepository.findById(2L)).thenReturn(Optional.of(userNew));
        Mockito
                .when(itemRepository.findById(1L)).thenReturn(Optional.ofNullable(item));
        Mockito
                .when(itemRepository.save(item)).thenReturn(item);
        Assertions.assertThrows(NotFoundException.class, () -> itemService.updateItem(Optional.of(1L),
                itemDto, Optional.of(1L)));
    }

    @Test
    void deleteItemTest() {
        Mockito
                .when(userRepository.findById(2L))
                .thenReturn(Optional.of(userNew));
        Mockito
                .when(itemRepository.findById(1L))
                .thenReturn(Optional.ofNullable(item));
        itemService.deleteItem(Optional.of(2L), Optional.of(1L));
        Mockito
                .verify(itemRepository, Mockito.times(1))
                .delete(item);
    }

    @Test
    void deleteItemWrongIdTest() {
        Mockito
                .when(userRepository.findById(2L)).thenReturn(Optional.of(userNew));
        Mockito
                .when(itemRepository.findById(1L)).thenReturn(Optional.ofNullable(item));
        Assertions.assertThrows(NotFoundException.class, () -> itemService.deleteItem(Optional.of(2L),
                Optional.empty()));
    }

    @Test
    void deleteItemWrongOwnerTest() {
        Mockito
                .when(userRepository.findById(2L)).thenReturn(Optional.of(userNew));
        Mockito
                .when(itemRepository.findById(1L)).thenReturn(Optional.ofNullable(item));
        Assertions.assertThrows(NotFoundException.class, () -> itemService.deleteItem(Optional.of(1L),
                Optional.of(1L)));
    }

    @Test
    void createCommentTest() {
        Mockito
                .when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        Mockito
                .when(itemRepository.findById(1L)).thenReturn(Optional.ofNullable(item));
        Mockito
                .when(bookingRepository.findByItem_IdAndBooker_id(1L, 1L)).thenReturn(Optional.of(list));
        Mockito
                .when(commentRepository.save(comment)).thenReturn(comment);
        CommentDto commentDtoTest = itemService.createComment(Optional.of(1L), Optional.of(1L), commentDto);
        Assertions.assertEquals(1L, commentDtoTest.getId());
        Assertions.assertEquals("item", commentDtoTest.getItem().getName());
        Assertions.assertEquals("text", commentDtoTest.getText());
    }

    @Test
    void createCommentWithoutTextTest() {
        Mockito
                .when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));
        Mockito
                .when(itemRepository.findById(1L))
                .thenReturn(Optional.ofNullable(item));
        Mockito
                .when(bookingRepository.findByItem_IdAndBooker_id(1L, 1L))
                .thenReturn(Optional.of(list));
        Mockito
                .when(commentRepository.save(comment))
                .thenReturn(comment);
        Assertions.assertThrows(BadRequestException.class, () -> itemService.createComment(Optional.of(1L),
                Optional.of(1L), new CommentDto(1L,"", itemDto,"user1", LocalDateTime.now())));
    }

    @Test
    void createCommentWrongBookingTest() {
        Mockito
                .when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        Mockito
                .when(itemRepository.findById(1L)).thenReturn(Optional.ofNullable(item));
        Mockito
                .when(bookingRepository.findByItem_IdAndBooker_id(1L, 1L)).thenReturn(Optional.empty());
        Mockito
                .when(commentRepository.save(comment)).thenReturn(comment);
        Assertions.assertThrows(BadRequestException.class, () -> itemService.createComment(Optional.of(1L),
                Optional.of(1L), commentDto));
    }
}
