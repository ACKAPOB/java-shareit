package ru.practicum.shareit.item.model;

import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.ItemDto;

@NoArgsConstructor
@Component
public class ItemMapper {

    public Item toItem (ItemDto itemDto, long id, long userId) {
        return new Item(
                id
                ,itemDto.getName()
                ,itemDto.getDescription()
                ,itemDto.getAvailable()
                ,userId
                ,itemDto.getRequest() != null ? itemDto.getRequest() : null
        );
    }

   public ItemDto toItemDto (Item item) {
        return new ItemDto(
                item.getId()
                ,item.getName()
                ,item.getDescription()
                ,item.getAvailable()
                ,item.getOwner()
                ,item.getRequest() != null ? item.getRequest() : null
        );
    }
}
