package ru.practicum.shareit.request;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;

import ru.practicum.shareit.item.ItemMapping;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.ItemDbForRequest;
import ru.practicum.shareit.request.dto.ItemReqListItemsDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional(readOnly = true)
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Autowired
    public ItemRequestServiceImpl(ItemRequestRepository itemRequestRepository, ItemRepository itemRepository, UserRepository userRepository) {
        this.itemRequestRepository = itemRequestRepository;
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    @Override
    public ItemRequestDto createItemRequest(ItemRequestDto itemRequestDto, Long userId) {
        log.info("Проверка сервис метод createItemRequest itemRequestDto {}", itemRequestDto);
        log.info("Проверка сервис метод createItemRequest userId {}", userId);
        checkIdUser(userId);
        User user = userRepository.findById(userId).get();
        ItemRequest itemRequest = ItemRequestMapping.mapToItemRequest(itemRequestDto, user);
        return ItemRequestMapping.mapToItemRequestDto(itemRequestRepository.save(itemRequest));
    }

    @Override
    public List<ItemReqListItemsDto> getListItemRequestDtoByIdUser(long requestorId) {
        checkIdUser(requestorId);
        List<ItemReqListItemsDto> itemReqListItemsDto = new ArrayList<>();
        List<ItemRequest> itemRequest = itemRequestRepository.findAllByRequestorId(requestorId);
        log.info("Проверка сервис метод getListItemRequestDtoByIdUser itemRequest {}", itemRequest);

        if (!itemRequest.isEmpty()) {
            for (ItemRequest request : itemRequest) {
                List<ItemDbForRequest> items = itemRepository.getAllItemsByRequestId(request.getId()).stream()
                        .map(ItemMapping::mapToItemDbForRequest)
                        .collect(Collectors.toList());
                log.info("Проверка сервис метод getListItemRequestDtoByIdUser items {}", items);

                    if (!items.isEmpty()) {
                        ItemReqListItemsDto itemReqListItemsDtoNew = ItemRequestMapping.mapToItemReqListItemsDto(request, items);
                        log.info("Проверка сервис метод getListItemRequestDtoByIdUser itemReqListItemsDtoNew {}", itemReqListItemsDtoNew);
                        itemReqListItemsDto.add(itemReqListItemsDtoNew);
                    } else {
                        ItemReqListItemsDto itemReqListItemsDtoNew = ItemRequestMapping.mapToItemReqListItemsDto(request, items);
                        itemReqListItemsDto.add(itemReqListItemsDtoNew);
                    }
                }
            }
        return itemReqListItemsDto;
    }

    @Override
    public List<ItemReqListItemsDto> getListAllItemRequestDtoExpectByIdUser(long requestorId, int from, int size) {
        checkIdUser(requestorId);
        PageRequest pageRequest = PageRequest.of(from, size);
        List<ItemReqListItemsDto> itemReqListItemsDto = new ArrayList<>();
        List<ItemRequest> itemRequest = itemRequestRepository.findAllByRequestorIdIsNotOrderByCreatedDesc(requestorId, pageRequest);
        log.info("Проверка сервис метод getListItemRequestDtoByIdUser itemRequest {}", itemRequest);

        if (!itemRequest.isEmpty()) {
            for (ItemRequest request : itemRequest) {
                List<ItemDbForRequest> items = itemRepository.getAllItemsByRequestId(request.getId()).stream()
                        .map(ItemMapping::mapToItemDbForRequest)
                        .collect(Collectors.toList());
                log.info("Проверка сервис метод getListAllItemRequestDtoExpectByIdUser items {}", items);

                if (!items.isEmpty()) {
                    ItemReqListItemsDto itemReqListItemsDtoNew = ItemRequestMapping.mapToItemReqListItemsDto(request, items);
                    log.info("Проверка сервис метод getListAllItemRequestDtoExpectByIdUser itemReqListItemsDtoNew {}", itemReqListItemsDtoNew);
                    itemReqListItemsDto.add(itemReqListItemsDtoNew);
                } else {
                    ItemReqListItemsDto itemReqListItemsDtoNew = ItemRequestMapping.mapToItemReqListItemsDto(request, items);
                    itemReqListItemsDto.add(itemReqListItemsDtoNew);
                }
            }
        }
        return itemReqListItemsDto;
    }

    public ItemReqListItemsDto getByItemRequestId(long requestId, long userId) {
        checkIdUser(userId);
        checkIdItemRequest(requestId);
        ItemRequest itemRequest = itemRequestRepository.findById(requestId).get();
        List<ItemDbForRequest> items = itemRepository.getAllItemsByRequestId(itemRequest.getId()).stream()
                .map(ItemMapping::mapToItemDbForRequest)
                .collect(Collectors.toList());
        log.info("Проверка сервис метод getListAllItemRequestDtoExpectByIdUser items {}", items);

        return ItemRequestMapping.mapToItemReqListItemsDto(itemRequest, items);
    }

//-----------------------------методы для проверки индификаторов
    private void checkIdUser(long userId) {
        log.info("Проверка сервис метод checkIdUser проверка userId");
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("User с таким Id не существует!");
        }
    }

    private void checkIdItemRequest(long requestId) {
        log.info("Проверка сервис метод checkIdItemRequest проверка requestId");
        if (!itemRequestRepository.existsById(requestId)) {
            throw new NotFoundException("Request с таким Id не существует!");
        }
    }
}
