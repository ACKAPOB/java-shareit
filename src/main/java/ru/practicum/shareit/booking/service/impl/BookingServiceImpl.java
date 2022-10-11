package ru.practicum.shareit.booking.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.FromSizeRequest;
import ru.practicum.shareit.booking.dto.BookingDtoById;
import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.booking.dto.BookingDtoOut;
import ru.practicum.shareit.booking.exception.BadRequestException;
import ru.practicum.shareit.booking.exception.BookingNotFoundException;
import ru.practicum.shareit.booking.exception.MessageFailedException;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;

import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class BookingServiceImpl implements BookingService{

    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    public BookingServiceImpl (BookingRepository bookingRepository, ItemRepository itemRepository,
                               UserRepository userRepository) {
        this.bookingRepository = bookingRepository;
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
    }

    @Override
    public BookingDtoById createBooking (Optional<Long> userId, Optional<BookingDtoIn> bookingDtoIn) {
        User user = validationUser(userId);
        Item item = validationItem(bookingDtoIn);
        if (user.getId().equals(item.getOwner().getId()))
            throw new BookingNotFoundException("Ошибка 1, BookingServiceImpl.createBooking");
        if (!item.getAvailable())
            throw new BadRequestException("Ошибка 2, BookingServiceImpl.createBooking");
        if (bookingDtoIn.get().getEnd().isBefore(bookingDtoIn.get().getStart()))
            throw new BadRequestException  ("Ошибка 3, BookingServiceImpl.createBooking");
        LocalDateTime timeToday = LocalDateTime.now();
        if (bookingDtoIn.get().getEnd().isBefore(timeToday))
            throw new BadRequestException  ("Ошибка 4, BookingServiceImpl.createBooking");
        if (bookingDtoIn.get().getStart().isBefore(timeToday))
            throw new BadRequestException  ("Ошибка 5, BookingServiceImpl.createBooking");
        Booking booking = BookingMapper.toBooking(bookingDtoIn.get());
        booking.setItem(item);
        booking.setBooker(user);
        booking.setStatus(Status.WAITING);
        bookingRepository.save(booking);
        log.info("Создание Booking BookingServiceImpl.createBooking, booking = {}", booking);
        return BookingMapper.toBookingDtoById(booking);
    }

    public User validationUser (Optional<Long> userId) {
        if (userId.isEmpty()) {
            throw new BookingNotFoundException("Ошибка userId.isEmpty, BookingServiceImpl.validationUser()");
        }
        Optional<User> user = userRepository.findById(userId.get());
        if (user.isEmpty()){
            throw new BookingNotFoundException("Ошибка user.isEmpty, BookingServiceImpl.validationUser()");
        }
        log.info("Проверка User BookingServiceImpl.validationUser, user = {}", user);
        return user.get();
    }
    public Item validationItem (Optional<BookingDtoIn> bookingDtoIn) {
        if (bookingDtoIn.isEmpty()) {
            throw new BookingNotFoundException("Ошибка bookingDtoIn.isEmpty, BookingServiceImpl.validationItem()");
        }
        Optional<Item> item = itemRepository.findById(bookingDtoIn.get().getItemId());
        if (item.isEmpty()) {
            throw new BookingNotFoundException("Ошибка item.isEmpty, BookingServiceImpl.validationItem()");
        }
        return item.get();
    }
    public Booking validationBooking (Optional<Long> bookingId)  {
        if (bookingId.isEmpty()) {
            throw new BadRequestException("Ошибка bookingId.isEmpty, BookingServiceImpl.validationBooking()");
        }
        Optional<Booking> booking = bookingRepository.findById(bookingId.get());
        if (booking.isEmpty()) {
            throw new BookingNotFoundException("Ошибка booking.isEmpty, BookingServiceImpl.validationBooking()");
        }
        log.info("Проверка Booking BookingServiceImpl.validationBooking, booking = {}", booking);
        return booking.get() ;
    }
    @Override
    public BookingDtoOut updateStatus (Optional<Long> userId, Optional<Long> bookingId, Boolean approved) {
        User user = validationUser(userId);
        Booking booking = validationBooking(bookingId);
        Optional<Item> item = itemRepository.findById(booking.getItem().getId());
        if (item.isEmpty())
            throw new BadRequestException("Ошибка 1, BookingServiceImpl.updateStatus");
        if (!item.get().getOwner().getId().equals(user.getId()))
            throw new BookingNotFoundException("Ошибка 2, BookingServiceImpl.updateStatus");
        if (booking.getStatus().equals(Status.APPROVED) && approved)
            throw new BadRequestException("Ошибка 3, BookingServiceImpl.updateStatus");
        if (approved) {
            booking.setStatus(Status.APPROVED);
        } else {
            booking.setStatus(Status.REJECTED);
        }
        bookingRepository.save(booking);
        log.info("Обновление Status booking = {}, approved = {}", booking, approved);
        return BookingMapper.toBookingDto(booking);
    }
    @Override
    public List<BookingDtoOut> getBookingsAllById(Optional<Long> userId){
        User user = validationUser(userId);
        List<Booking> list =  bookingRepository.findByBooker_IdOrderByStartDesc(user.getId());
        log.info("Получение Bookings BookingServiceImpl.getBookingsAllById, listBooking = {} ", list);
        return BookingMapper.toListBookingDto(list);
    }
    @Override
    public BookingDtoOut getBookingById(Optional<Long> userId, Optional<Long> bookingId) {
        User user = validationUser(userId);
        Booking booking = validationBooking(bookingId);
        if (booking.getBooker().getId().equals(user.getId()) ||
                booking.getItem().getOwner().getId().equals(user.getId())) {
            log.info("Получение Booking BookingServiceImpl.getBookingById, booking = {}, userId = {} ", booking, userId);
            return BookingMapper.toBookingDto(booking);
        }
        throw new BookingNotFoundException("Ошибка, BookingServiceImpl.getBookingById");
    }
    @Override
    public List<BookingDtoOut> getBookingsState(Optional<Long> userId, Optional<Integer> from, Optional<Integer> size, String state)
            throws BadRequestException, MessageFailedException {
        try {
            State.valueOf(state);
        } catch (IllegalArgumentException e) {
            throw new MessageFailedException();
        }
        User user = validationUser(userId);
        final Pageable pageable = FromSizeRequest.of(from.get(), size.get());
        switch (State.valueOf(state)) {
            case ALL:
                List <Booking> listBooking = bookingRepository
                        .findByBookingsListStateAll(userId.get(), pageable).getContent();
                log.info("Получение ALL Bookings BookingServiceImpl.getBookingsState, listBooking = {} ", listBooking);
                return BookingMapper.toListBookingDto(listBooking);
            case CURRENT:
                LocalDateTime time = LocalDateTime.now();
                listBooking = bookingRepository
                        .findByBookingsListStateCurrent(userId.get(), time, pageable).getContent();
                log.info("Получение CURRENT Bookings BookingServiceImpl.getBookingsState, listBooking = {} ", listBooking);
                return BookingMapper.toListBookingDto(listBooking);
            case PAST:
                time = LocalDateTime.now();
                listBooking = bookingRepository
                        .findByBookingsListStatePast(userId.get(), time, pageable).getContent();
                log.info("Получение PAST Bookings BookingServiceImpl.getBookingsState, listBooking = {} ", listBooking);
                return BookingMapper.toListBookingDto(listBooking);
            case FUTURE:
                time = LocalDateTime.now();
                listBooking = bookingRepository
                        .findByBookingsListStateFuture(userId.get(), time, pageable).getContent();
                log.info("Получение FUTURE Bookings BookingServiceImpl.getBookingsState, listBooking = {} ", listBooking);
                return BookingMapper.toListBookingDto(listBooking);
            case WAITING:
                listBooking = bookingRepository
                        .findByBookingsListStateWaiting(userId.get(), pageable).getContent();
                log.info("Получение WAITING Bookings BookingServiceImpl.getBookingsState, listBooking = {} ", listBooking);
                return BookingMapper.toListBookingDto(listBooking);
            case REJECTED:
                listBooking = bookingRepository
                        .findByBookingsListStateRejected(userId.get(), pageable).getContent();
                log.info("Получение REJECTED Bookings BookingServiceImpl.getBookingsState, listBooking = {} ", listBooking);
                return BookingMapper.toListBookingDto(listBooking);
            default:
                throw new MessageFailedException();
        }
    }

    @Override
    public List<BookingDtoOut> getBookingsOwnerState (Optional<Long> userId, Optional<Integer> from,
                                                       Optional<Integer> size, String state) throws MessageFailedException {
        try {
            State.valueOf(state);
        } catch (IllegalArgumentException e) {
            throw new MessageFailedException();
        }
        User user = validationUser(userId);
        if (itemRepository.findByOwner_IdOrderById(userId.get()).isEmpty())
            throw new BadRequestException("Ошибка, BookingServiceImpl.getBookingsOwnerState");

        final Pageable pageable = FromSizeRequest.of(from.get(), size.get());
        switch (State.valueOf(state)) {
            case ALL:
                List <Booking> list = bookingRepository
                        .findByBookingsOwnerListStateAll(userId.get(), pageable).getContent();
                log.info("Получение ALL Bookings BookingServiceImpl.getBookingsOwnerState, list = {} ", list);
                return BookingMapper.toListBookingDto(list);
            case CURRENT:
                LocalDateTime time = LocalDateTime.now();
                list = bookingRepository
                        .findByBookingsOwnerListStateCurrent(userId.get(), time, pageable).getContent();
                log.info("Получение CURRENT Bookings BookingServiceImpl.getBookingsOwnerState, list = {} ", list);
                return BookingMapper.toListBookingDto(list);
            case PAST:
                time = LocalDateTime.now();
                list = bookingRepository
                        .findByBookingsOwnerListStatePast(userId.get(), time, pageable).getContent();
                log.info("Получение PAST Bookings BookingServiceImpl.getBookingsOwnerState, list = {} ", list);
                return BookingMapper.toListBookingDto(list);
            case FUTURE:
                time = LocalDateTime.now();
                list = bookingRepository
                        .findByBookingsOwnerListStateFuture(userId.get(), time, pageable).getContent();
                log.info("Получение FUTURE Bookings BookingServiceImpl.getBookingsOwnerState, list = {} ", list);
                return BookingMapper.toListBookingDto(list);
            case WAITING:
                list = bookingRepository
                        .findByBookingsOwnerListStateWaiting(userId.get(), pageable).getContent();
                log.info("Получение WAITING Bookings BookingServiceImpl.getBookingsOwnerState, list = {} ", list);
                return BookingMapper.toListBookingDto(list);
            case REJECTED:
                list = bookingRepository
                        .findByBookingsOwnerListStateRejected(userId.get(), pageable).getContent();
                log.info("Получение REJECTED Bookings BookingServiceImpl.getBookingsOwnerState, list = {} ", list);
                return BookingMapper.toListBookingDto(list);
            default:
                throw new MessageFailedException();
        }
    }
}
