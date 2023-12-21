package ru.practicum.shareit.item;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

@Service
public class ItemMapping {
    //из entity в dto
    public ItemDto mapToItemDto(Item item){
//        ItemDto itemDto = new ItemDto();
//
//        itemDto.setId(item.getId());
//        itemDto.setName(item.getName());
//        //itemDto.setOwner(item.getOwner());
//        //itemDto.setRequest(item.getRequest());
//        itemDto.setAvailable(item.isAvailable());
//        itemDto.setDescription(item.getDescription());

        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .available(item.getAvailable())
                .description(item.getDescription())
                .build();
    }

    public Item mapToItem(ItemDto itemDto){
//        Item item =new Item();
//
//        item.setId(itemDto.getId());
//        item.setName(itemDto.getName());
//        //item.setOwner(itemDto.getOwner());
//        //item.setRequest(itemDto.getRequest());
//        item.setAvailable(itemDto.getAvailable());
//        item.setDescription(itemDto.getDescription());

        return Item.builder()
                .id(itemDto.getId())
                .name(itemDto.getName())
                .available(itemDto.getAvailable())
                .description(itemDto.getDescription())
                .build();
    }
}
