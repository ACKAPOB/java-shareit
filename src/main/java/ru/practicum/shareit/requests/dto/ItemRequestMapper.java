    package ru.practicum.shareit.requests.dto;

    import ru.practicum.shareit.requests.model.ItemRequest;
    import ru.practicum.shareit.user.dto.UserMapper;

    import java.util.List;
    import java.util.stream.Collectors;

    public class ItemRequestMapper {

        public static ItemRequestDto toItemRequestDto(ItemRequest itemRequest) {
            return new ItemRequestDto(
                    itemRequest.getId(),
                    itemRequest.getDescription(),
                    UserMapper.toUserDto(itemRequest.getRequestor()),
                    itemRequest.getCreated()
            );
        }

        public static ItemRequest toItemRequest(ItemRequestDto itemRequestDto) {
            return new ItemRequest(
                    itemRequestDto.getId(),
                    itemRequestDto.getDescription(),
                    itemRequestDto.getRequestor(),
                    itemRequestDto.getCreated());
        }
        public static List<ItemRequestDto> toListItemRequestDto(List<ItemRequest> listItemRequest) {
            return listItemRequest.stream().map(ItemRequestMapper::toItemRequestDto).collect(Collectors.toList());
        }

    }
