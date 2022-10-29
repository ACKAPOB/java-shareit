package ru.practicum.shareit.item.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.comment.repository.CommentRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoOut;
import ru.practicum.shareit.item.exception.BadRequestException;
import ru.practicum.shareit.item.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.requests.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.LocalDateTime;
import java.util.*;

@Service
@Slf4j
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {
    private final ItemRepository repository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final BookingRepository bookingRepository;
    private final ItemRequestRepository itemRequestRepository;
    @PersistenceContext
    public EntityManager em;

    @Autowired
    public ItemServiceImpl(ItemRepository itemRepository, UserRepository userRepository,
                           CommentRepository commentRepository, BookingRepository bookingRepository, ItemRequestRepository itemRequestRepository) {
        this.repository = itemRepository;
        this.userRepository = userRepository;
        this.commentRepository = commentRepository;
        this.bookingRepository = bookingRepository;
        this.itemRequestRepository = itemRequestRepository;
    }

    @Override
    @Transactional
    public ItemDto createItem(Optional<Long> userId, ItemDto itemDto) {
        validationUser(userId);
        Item item = ItemMapper.toItem(itemDto);
        if (itemDto.getRequestId() != null)
            item.setRequest(itemRequestRepository.findById(itemDto.getRequestId()).get());
        if (item.getName() == null || Objects.equals(item.getName(), ""))
            throw new BadRequestException("Ошибка данных, ItemServiceImpl.createItem, item.getName");
        if (item.getDescription() == null || Objects.equals(item.getDescription(), ""))
            throw new BadRequestException("Ошибка данных, ItemServiceImpl.createItem, item.getDescription");
        if (item.getAvailable() == null)
            throw new BadRequestException("Ошибка данных, ItemServiceImpl.createItem, item.getAvailable");
        item.setOwner(userRepository.findById(userId.get()).get());
        repository.save(item);
        log.info("Создание Item ItemServiceImpl.createItem, userId = {}, item = {}", userId, item);
        return ItemMapper.toItemDto(item);
    }

    @Override
    @Transactional
    public ItemDto updateItem(Optional<Long> userId, ItemDto itemDto, Optional<Long> id) {
        Item item = ItemMapper.toItem(itemDto);
        if (id.isEmpty())
            throw new NotFoundException("Ошибка отсутствует id Item, ItemServiceImpl.updateItem");
        validationUser(userId);
        Item itemUpd = repository.findById(id.get()).get();
        if (!Objects.equals(itemUpd.getOwner().getId(), userId.get()))
            throw new NotFoundException("Ошибка владелец указан неверно, ItemServiceImpl.updateItem");
        if (!(item.getName() == null || item.getName().isEmpty())) itemUpd.setName(item.getName());
        if (!(item.getDescription() == null || Objects.equals(item.getDescription(), "")))
            itemUpd.setDescription(item.getDescription());
        if (item.getAvailable() != null) itemUpd.setAvailable(item.getAvailable());
        repository.save(itemUpd);
        log.info("Обновление Item ItemServiceImpl.updateItem, userId = {}, itemId = {}, itemDto = {}", userId, id, itemDto);
        return ItemMapper.toItemDto(itemUpd);
    }

    @Override
    @Transactional
    public ItemDto deleteItem(Optional<Long> userId, Optional<Long> id) {
        validationUser(userId);
        if (id.isPresent()) {
            Item item = repository.findById(id.get()).get();
            if (!Objects.equals(item.getOwner().getId(), userId.get()))
                throw new NotFoundException("Ошибка getOwne, ItemServiceImpl.deleteItem()");
            repository.delete(item);
            log.info("Удаление Item, ItemServiceImpl.deleteItem item = {}.", item);
            return ItemMapper.toItemDto(item);
        }
        throw new NotFoundException("Ошибка Id ItemServiceImpl.deleteItem()");
    }

    @Override
    public List<ItemDtoOut> getAllItemsOwner(Optional<Long> idUser, Optional<Integer> from, Optional<Integer> size) {
        validationUser(idUser);
        List<Item> listItem;
        if (from.isEmpty() || size.isEmpty()) {
            listItem = repository.findByOwner_IdOrderById(idUser.get());
        }
        else if (from.get() < 0 || size.get() <= 0) {
            throw new BadRequestException("Ошибка 1 ItemServiceImpl.getAllItemsOwner()");
        } else {
            listItem = em.createQuery("SELECT i FROM Item i WHERE i.owner.id = ?1 ORDER BY i.id", Item.class)
                    .setParameter(1, idUser.get())
                    .setFirstResult(from.get() - 1)
                    .setMaxResults(size.get())
                    .getResultList();
        }
        if (listItem.isEmpty()) throw new BadRequestException("Ошибка 2 ItemServiceImpl.getAllItemsOwner()");

        List<ItemDtoOut> list = new ArrayList<>();
        for (Item item : listItem) {
            list.add(findLastNextBooking(item));
        }
        //listItem.stream().map(x -> list.add(findLastNextBooking(x))); // доделать бы, но тут дедлайн
        log.info("Поиск всех Item ItemServiceImpl.getAllItemsOwner, userId = {}, list = {}", idUser, list);
        return list;
    }


    @Override
    public ItemDtoOut getItemById(Optional<Long> userId, Optional<Long> id) {
        validationUser(userId);
        validationItem(id);
        Item item = repository.findById(id.get()).get();
        ItemDtoOut itemDto;
        if (Objects.equals(item.getOwner().getId(), userId.get())) {
            itemDto = findLastNextBooking(item);
        } else {
            itemDto = ItemMapper.toItemDtoOut(item);
            itemDto.setComments(ItemMapper.toListCommentDto(commentRepository.findByItemId(item.getId())));
        }
        log.info("Поиск всех Item ItemServiceImpl.getItemById, userId = {}, itemId = {}, itemDto = {}", userId, id, itemDto);
        return itemDto;
    }

    public ItemDtoOut findLastNextBooking(Item item) {
        ItemDtoOut itemDtoOut = ItemMapper.toItemDtoOut(item);
        Optional<List<Booking>> listBooking = bookingRepository.findByItem_Id(item.getId());
        if ((listBooking.get().size() - 1) >= 0) {
            itemDtoOut.setNextBooking(
                    BookingMapper.toBookingDtoByIdTime(listBooking.get().get(listBooking.get().size() - 1)));
        }
        if ((listBooking.get().size() - 2) >= 0) {
            itemDtoOut.setLastBooking(
                    BookingMapper.toBookingDtoByIdTime(listBooking.get().get(listBooking.get().size() - 2)));
        }
        itemDtoOut.setComments(ItemMapper.toListItemDtoLastNext((commentRepository.findByItemId(item.getId()))));
        log.info("Отработал ItemDtoLastNext ItemServiceImpl.ItemDtoLastNext, item = {}", item);
        return itemDtoOut;
    }

    @Override
    public List<ItemDto> getItemByIdSearch(Optional<Long> idUser, String text, Optional<Integer> from, Optional<Integer> size) {
        validationUser(idUser);
        if (text == null || text.length() == 0) return Collections.emptyList();
        List<Item> listItem = repository.searchListItem(text);
        if (from.isEmpty() || size.isEmpty()) {
            listItem = repository.searchListItemText(text);
            if (listItem.isEmpty()) {
                throw new BadRequestException("Ошибка! ItemServiceImpl.getItemByIdSearch ()");
            }
        }
        log.info("Поиск Item по Text ItemServiceImpl.getItemByIdSearch, userId = {}, text = {}", idUser, text);
        return ItemMapper.toListItemDto(listItem);
    }

    public Optional<User> validationUser(Optional<Long> userId) {
        if (userId.isEmpty()) {
            throw new BadRequestException("Ошибка userId, ItemServiceImpl.validationUser()");
        }
        Optional<User> user = userRepository.findById(userId.get());
        if (user.isEmpty())
            throw new NotFoundException("Ошибка user ItemServiceImpl.validationUser");
        log.info("Проверка User ItemServiceImpl.validationUser, user = {},", user);
        return user;
    }

    public Optional<Item> validationItem(Optional<Long> id) {
        if (id.isEmpty()) {
            System.out.println("PRINT ID " + id);
            throw new BadRequestException("Ошибка id, ItemServiceImpl.validationItem()");
        }
        Optional<Item> item = repository.findById(id.get());
        if (item.isEmpty())
            throw new NotFoundException("Ошибка item, ItemServiceImpl.validationItem()");
        log.info("Проверка item, ItemServiceImpl.validationUser, Item = {},", item);
        return item;
    }

    @Override
    @Transactional
    public CommentDto createComment(Optional<Long> userId, Optional<Long> id, CommentDto commentDto) {
        if (commentDto.getText().isEmpty())
            throw new BadRequestException("Ошибка Comment - isEmpty, ItemServiceImpl.createComment");
        Optional<User> user = validationUser(userId);
        Optional<Item> item = validationItem(id);
        Optional<List<Booking>> bookings = bookingRepository.findByItem_IdAndBooker_id(id.get(), userId.get());
        if (bookings.isEmpty())
            throw new BadRequestException("Ошибка bookings - isEmpty, ItemServiceImpl.createComment");
        LocalDateTime time = LocalDateTime.now();
        int count = 0;
        for (Booking booking : bookings.get()) {
            if (booking.getStart().isAfter(time)) ++count;
        }
        if ((bookings.get().size()) == count)
            throw new BadRequestException("Ошибка! ItemServiceImpl.createComment()");
        Comment comment = ItemMapper.toComment(commentDto);
        comment.setItem(item.get());
        comment.setAuthor(user.get());
        comment.setCreationTime(time);
        commentRepository.save(comment);
        log.info("Создан комментарий номер {} ", comment.getId());
        return ItemMapper.toCommentDto(comment);
    }
}