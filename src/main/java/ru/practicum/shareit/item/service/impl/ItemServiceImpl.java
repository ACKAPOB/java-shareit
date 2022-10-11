package ru.practicum.shareit.item.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.comment.repository.CommentRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoLastNext;
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
    private ItemRepository repository;
    private UserRepository userRepository;
    private CommentRepository commentRepository;
    private BookingRepository bookingRepository;
    private ItemRequestRepository itemRequestRepository;
    @PersistenceContext
    public EntityManager em;

    @Autowired
    public ItemServiceImpl (ItemRepository itemRepository, UserRepository userRepository,
                            CommentRepository commentRepository, BookingRepository bookingRepository, ItemRequestRepository itemRequestRepository){
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
        if (itemDto.getRequestId() != null) item.setRequest(itemRequestRepository.findById(itemDto.getRequestId()).get());
        if (item.getName() == null || item.getName() == "")
            throw new BadRequestException("Ошибка данных, ItemServiceImpl.createItem, item.getName");
        if (item.getDescription() == null || item.getDescription() == "")
            throw new BadRequestException ("Ошибка данных, ItemServiceImpl.createItem, item.getDescription");
        if (item.getAvailable() == null)
            throw new BadRequestException ("Ошибка данных, ItemServiceImpl.createItem, item.getAvailable");
        item.setOwner(userRepository.findById(userId.get()).get());
        repository.save(item);
        log.info("Создание Item ItemServiceImpl.createItem, userId = {}, item = {}", userId, item);
        return ItemMapper.toItemDto(item);
    }
    public ItemDtoLastNext findLastNextBooking (Item item) {
        ItemDtoLastNext itemDtoLastNext = ItemMapper.toItemDtoLastNext(item);
        Optional<List<Booking>> listBooking = bookingRepository.findByItem_Id(item.getId());
        if ((listBooking.get().size() - 1) >= 0) {
            itemDtoLastNext.setNextBooking(
                    BookingMapper.toBookingDtoByIdTime(listBooking.get().get(listBooking.get().size() - 1)));
        }
        if ((listBooking.get().size() - 2) >= 0) {
            itemDtoLastNext.setLastBooking(
                    BookingMapper.toBookingDtoByIdTime(listBooking.get().get(listBooking.get().size() - 2)));
        }
        itemDtoLastNext.setComments(ItemMapper.toListItemDtoLastNext((commentRepository.findByItemId(item.getId()))));
        log.info("Отработал ItemDtoLastNext ItemServiceImpl.ItemDtoLastNext, item = {}", item);
        return itemDtoLastNext;

    }

    @Override
    public List<ItemDtoLastNext> getAllItemsOwner(Optional<Long> userId, Optional<Integer> from, Optional<Integer> size) {
        validationUser(userId);
        List<Item> listItem;
        if (from.isEmpty() || size.isEmpty()) {
            listItem = repository.findByOwner_IdOrderById(userId.get());
        } else if (from.get() < 0 || size.get() <= 0) {
            throw new BadRequestException("Ошибка в параметрах from или size! ItemServiceImpl.getAllItemsOwner()");
        } else {
            listItem = em.createQuery("SELECT i FROM Item i WHERE i.owner.id = ?1 ORDER BY i.id", Item.class)
                    .setParameter(1, userId.get())
                    .setFirstResult(from.get() - 1)
                    .setMaxResults(size.get())
                    .getResultList();
        }
        if (listItem.isEmpty()) {
            throw new BadRequestException("У пользователя нет вещей! findAllItemsOwner()");
        }
        List<ItemDtoLastNext> list = new ArrayList<>();
        for (Item item : listItem) {
            list.add(findLastNextBooking(item));
        }
        log.info("Поиск всех Item ItemServiceImpl.getAllItemsOwner, userId = {}, from = {}, size = {}, list = {}",
                userId, from, size, list);
        return list;
    }

    @Override
    public ItemDtoLastNext getItemById(Optional<Long> userId, Optional<Long> id) {
        validationUser(userId);
        validationItem(id);
        Item item = repository.findById(id.get()).get();
        ItemDtoLastNext itemDto;
        if (item.getOwner().getId() == userId.get()) {
            itemDto = findLastNextBooking(item);
        } else {
            itemDto = ItemMapper.toItemDtoLastNext(item);
            itemDto.setComments(ItemMapper.toListCommentDto(commentRepository.findByItemId(item.getId())));
        }
        log.info("Поиск всех Item ItemServiceImpl.getItemById, userId = {}, itemId = {}, itemDto = {}", userId, id, itemDto);
        return itemDto;
    }
    @Override
    @Transactional
    public ItemDto updateItem(Optional<Long> userId, ItemDto itemDto, Optional<Long> id){
        Item item = ItemMapper.toItem(itemDto);
        if (id.isEmpty())
            throw new NotFoundException("Ошибка отсутствует id Item, ItemServiceImpl.updateItem");
        validationUser(userId);
        Item itemUpd = repository.findById(id.get()).get();
        if (itemUpd.getOwner().getId() != (userId.get()))
            throw new NotFoundException("Ошибка владелец указан неверно, ItemServiceImpl.updateItem");
        if (!(item.getName() == null || item.getName().isEmpty())) itemUpd.setName(item.getName());
        if (!(item.getDescription() == null || item.getDescription() == ""))
            itemUpd.setDescription(item.getDescription());
        if (item.getAvailable() != null) itemUpd.setAvailable(item.getAvailable());
        repository.save(itemUpd);
        log.info("Обновление Item ItemServiceImpl.updateItem, userId = {}, itemId = {}, itemDto = {}", userId, id, itemDto);
        return ItemMapper.toItemDto(itemUpd);
    }
    @Override
    @Transactional
    public ItemDto deleteItem(Optional<Long> userId, Optional<Long> id){
        validationUser(userId);
        if (id.isPresent()) {
            Item item = repository.findById(id.get()).get();
            if (item.getOwner().getId() != userId.get())
                throw new NotFoundException("Ошибка getOwne, ItemServiceImpl.deleteItem()");
            repository.delete(item);
            log.info("Удаление Item, item = {}.", item);
            return ItemMapper.toItemDto(item);
        }
        throw new NotFoundException("Ошибка Id ItemServiceImpl.deleteItem()");
    }
    @Override
    public List<ItemDto> getItemByIdSearch (Optional<Long> userId, String text, Optional<Integer> from, Optional<Integer> size) {
        validationUser(userId);
        if (text == null || text.length() == 0) return Collections.emptyList();
        List<Item> listItem;
        if (!from.isPresent() || !size.isPresent()) {
            listItem = repository.searchListItemText(text);
        } else {
            final Pageable pageable = PageRequest.of(from.get(), size.get());
            listItem = repository.searchListItem(text, pageable).getContent();
        }

        if (listItem.isEmpty()) throw new BadRequestException("Ошибка! ItemServiceImpl.getItemByIdSearch ()");
        log.info("Поиск Item по Text ItemServiceImpl.getItemByIdSearch, userId = {}, text = {}", userId, text);
        return ItemMapper.toListItemDto(listItem);
    }
    public Optional<User> validationUser (Optional<Long> userId){
        if (userId.isEmpty()) {
            throw new BadRequestException("Ошибка userId, ItemServiceImpl.validationUser()");
        }
        Optional<User> user = userRepository.findById(userId.get());
        if (user.isEmpty())
            throw new NotFoundException("Ошибка user ItemServiceImpl.validationUser");
        log.info("Проверка User ItemServiceImpl.validationUser, user = {},", user);
        return user;
    }
    public Optional<Item> validationItem (Optional<Long> id) {
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
        Optional<List<Booking>> bookings= bookingRepository.findByItem_IdAndBooker_id(id.get(), userId.get());
        if (!bookings.isPresent())
            throw new BadRequestException("Ошибка bookings - isEmpty, ItemServiceImpl.createComment");
        LocalDateTime time = LocalDateTime.now();
        int count = 0;
        for (Booking booking: bookings.get()) {
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