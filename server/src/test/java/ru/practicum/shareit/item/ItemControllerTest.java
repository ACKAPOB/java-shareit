    package ru.practicum.shareit.item;

    import com.fasterxml.jackson.databind.ObjectMapper;
    import org.junit.jupiter.api.BeforeEach;
    import org.junit.jupiter.api.Test;
    import org.junit.jupiter.api.extension.ExtendWith;
    import org.mockito.junit.jupiter.MockitoExtension;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
    import org.springframework.boot.test.mock.mockito.MockBean;
    import org.springframework.http.MediaType;
    import org.springframework.test.web.servlet.MockMvc;
    import ru.practicum.shareit.booking.dto.BookingDtoById;
    import ru.practicum.shareit.booking.model.Status;
    import ru.practicum.shareit.comment.dto.CommentDto;
    import ru.practicum.shareit.item.dto.ItemDto;
    import ru.practicum.shareit.item.dto.ItemDtoOut;
    import ru.practicum.shareit.item.service.ItemService;
    import ru.practicum.shareit.requests.dto.ItemRequestDto;
    import ru.practicum.shareit.user.dto.UserDto;

    import java.nio.charset.StandardCharsets;
    import java.time.LocalDateTime;
    import java.util.Collections;
    import java.util.List;

    import static org.mockito.ArgumentMatchers.any;
    import static org.mockito.Mockito.when;
    import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
    import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
    import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

    @ExtendWith(MockitoExtension.class)
    @WebMvcTest(controllers = ItemController.class)
    class ItemControllerTest {

        @Autowired
        private ObjectMapper mapper;
        @Autowired
        private MockMvc mvc;
        @MockBean
        ItemService itemService;
        private ItemDto itemDto;
        private CommentDto commentDto;
        private ItemDtoOut itemDtoLastNext;
        private List<ItemDto> list;
        private List<ItemDtoOut> lastNextList;
        String text;

        @BeforeEach
        void beforeEach() {
            mapper.findAndRegisterModules();
            UserDto userDto = new UserDto(1L, "user1", "user1@mail.com");
            itemDto = new ItemDto(1L,"user1","user1 desc",true, userDto, 4L);
            commentDto = new CommentDto(3L, "норм", itemDto, "user1", LocalDateTime.now());

            itemDtoLastNext = new ItemDtoOut(2L, "user1", "description", true,
                    new BookingDtoById(1L, 2L, LocalDateTime.now(), LocalDateTime.now().plusMonths(2), Status.WAITING),
                    new BookingDtoById(3L, 4L, LocalDateTime.now(), LocalDateTime.now().plusMonths(3), Status.WAITING),
                    List.of(new CommentDto("comment")),
                    new ItemRequestDto(1L, "description", userDto, LocalDateTime.now(), list));
            list = Collections.singletonList(itemDto);
            lastNextList = List.of(itemDtoLastNext);
            text = "text";
        }

        @Test
        void createItemTest() throws Exception {
            when(itemService.createItem(any(), any()))
                    .thenReturn(itemDto);
            mvc.perform(post("/items")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(itemDto))
                            .characterEncoding(StandardCharsets.UTF_8)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().json(mapper.writeValueAsString(itemDto)));
        }

        @Test
        void getItemByIdTest() throws Exception {
            when(itemService.getItemById(any(), any()))
                    .thenReturn(itemDtoLastNext);
            mvc.perform(get("/items/2")
                            .characterEncoding(StandardCharsets.UTF_8)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().json(mapper.writeValueAsString(itemDtoLastNext)));
        }

        @Test
        void deleteItemTest() throws Exception {
            when(itemService.deleteItem(any(), any()))
                    .thenReturn(itemDto);
            mvc.perform(delete("/items/1")
                            .characterEncoding(StandardCharsets.UTF_8)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().json(mapper.writeValueAsString(itemDto)));
        }

        @Test
        void findAllItemsTest() throws Exception {
            when(itemService.getAllItemsOwner(any(), any(), any()))
                    .thenReturn(lastNextList);
            mvc.perform(get("/items")
                            .characterEncoding(StandardCharsets.UTF_8)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().json(mapper.writeValueAsString(lastNextList)));
        }

        @Test
        void findItemByIdSearchTest() throws Exception {
            when(itemService.getItemByIdSearch(any(), any(), any(), any()))
                    .thenReturn(list);
            mvc.perform(get("/items/search")
                            .param("text", text)
                            .characterEncoding(StandardCharsets.UTF_8)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().json(mapper.writeValueAsString(list)));
        }

        @Test
        void putTest() throws Exception {
            when(itemService.updateItem(any(), any(), any()))
                    .thenReturn(itemDto);
            mvc.perform(patch("/items/1")
                            .content(mapper.writeValueAsString(itemDto))
                            .characterEncoding(StandardCharsets.UTF_8)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().json(mapper.writeValueAsString(itemDto)));
        }

        @Test
        void createTest() throws Exception {
            when(itemService.createComment(any(), any(), any()))
                    .thenReturn(commentDto);
            mvc.perform(post("/items/{id}/comment", 3)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(commentDto))
                            .characterEncoding(StandardCharsets.UTF_8)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().json(mapper.writeValueAsString(commentDto)));
        }
    }