package ru.practicum.shareit.booking.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDtoById;
import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.booking.dto.BookingDtoOut;
import ru.practicum.shareit.booking.exception.BadRequestException;
import ru.practicum.shareit.booking.exception.BookingNotFoundException;
import ru.practicum.shareit.booking.exception.MessageFailedException;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.dto.BookingMapper;
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
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public BookingDtoById createBooking(Optional<Long> userId, Optional<BookingDtoIn> bookingDtoIn) {
        User user = validationUser(userId);
        Item item = validationItem(bookingDtoIn);
        if (user.getId().equals(item.getOwner().getId()))
            throw new BookingNotFoundException("Ошибка 1, BookingServiceImpl.createBooking");
        if (!item.getAvailable())
            throw new BadRequestException("Ошибка 2, BookingServiceImpl.createBooking");
        if (bookingDtoIn.get().getEnd().isBefore(bookingDtoIn.get().getStart()))
            throw new BadRequestException("Ошибка 3, BookingServiceImpl.createBooking");
        LocalDateTime timeToday = LocalDateTime.now();
        if (bookingDtoIn.get().getEnd().isBefore(timeToday))
            throw new BadRequestException("Ошибка 4, BookingServiceImpl.createBooking");
        if (bookingDtoIn.get().getStart().isBefore(timeToday))
            throw new BadRequestException("Ошибка 5, BookingServiceImpl.createBooking");
        Booking booking = BookingMapper.toBooking(bookingDtoIn.get());
        booking.setItem(item);
        booking.setBooker(user);
        booking.setStatus(Status.WAITING);
        bookingRepository.save(booking);
        log.info("Создание Booking BookingServiceImpl.createBooking, booking = {}", booking);
        return BookingMapper.toBookingDtoById(booking);
    }

    @Override
    @Transactional
    public BookingDtoOut updateStatus(Optional<Long> userId, Optional<Long> bookingId, Boolean approved) {

        Booking booking = bookingRepository.findById(bookingId.get())
                        .orElseThrow(() -> new BadRequestException("Ошибка booking, createBooking.updateStatus"));
        User user = userRepository.findById(userId.get())
                .orElseThrow(() -> new BadRequestException("Ошибка user, createBooking.updateStatus"));
        Item item = itemRepository.findById(booking.getItem().getId())
                .orElseThrow(() -> new BadRequestException("Ошибка item, createBooking.updateStatus"));
        if (!item.getOwner().getId().equals(user.getId()))
            throw new BookingNotFoundException("Ошибка equals, BookingServiceImpl.updateStatus");
        if (booking.getStatus().equals(Status.APPROVED))
            throw new BadRequestException("Ошибка Status.APPROVED, BookingServiceImpl.updateStatus");
        booking.setStatus(Boolean.TRUE.equals(approved) ? Status.APPROVED : Status.REJECTED);
        bookingRepository.save(booking);
        log.info("Обновление Status booking = {}, approved = {}", booking, approved);
        return BookingMapper.toBookingDto(booking);
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
    public List<BookingDtoOut> getBookingsState(Optional<Long> userId, String state)
            throws BadRequestException, MessageFailedException {
        User user = validationUser(userId);
        switch (state) {
            case "ALL":
                log.info("Получение ALL Bookings BookingServiceImpl.getBookingsState, userId = {} ", userId);
                return BookingMapper.toListBookingDto(bookingRepository
                        .findBookingsByBookerIdOrderByStartDesc(userId.get()));
            case "CURRENT":
                log.info("Получение CURRENT Bookings BookingServiceImpl.getBookingsState, userId = {} ", userId);
                return BookingMapper.toListBookingDto(bookingRepository
                        .findByBookingsListStateCurrent(userId.get(), LocalDateTime.now()));
            case "PAST":
                log.info("Получение PAST Bookings BookingServiceImpl.getBookingsState, userId = {} ", userId);
                return BookingMapper.toListBookingDto(bookingRepository
                        .findByBookingsListStatePast(userId.get(), LocalDateTime.now()));
            case "FUTURE":
                log.info("Получение FUTURE Bookings BookingServiceImpl.getBookingsState, userId = {} ", userId);
                return BookingMapper.toListBookingDto(bookingRepository.findByBookingsListStateFuture(userId.get()));
            case "WAITING":
                log.info("Получение WAITING Bookings BookingServiceImpl.getBookingsState, userId = {} ", userId);
                return BookingMapper.toListBookingDto(bookingRepository.findByBookingsListStateWaiting(userId.get()));
            case "REJECTED":
                log.info("Получение REJECTED Bookings BookingServiceImpl.getBookingsState, userId = {} ", userId);
                return BookingMapper.toListBookingDto(bookingRepository.findByBookingsListStateRejected(userId.get()));
            default:
                throw new MessageFailedException();
        }
    }

    @Override
    public List<BookingDtoOut> getBookingsOwnerState(Optional<Long> userId, String state) throws
            MessageFailedException {
        User user = validationUser(userId);

        switch (state) {
            case "ALL":
                log.info("Получение ALL Bookings BookingServiceImpl.getBookingsOwnerState, ownerId = {} ", userId);
                return BookingMapper.toListBookingDto(bookingRepository
                        .findByBookingsOwnerListStateAll(userId.get()));
            case "CURRENT":
                log.info("Получение CURRENT Bookings BookingServiceImpl.getBookingsOwnerState, ownerId = {} ", userId);
                return BookingMapper.toListBookingDto(bookingRepository
                        .findByBookingsOwnerListStateCurrent(userId.get(), LocalDateTime.now()));
            case "PAST":
                log.info("Получение PAST Bookings BookingServiceImpl.getBookingsOwnerState, ownerId = {} ", userId);
                return BookingMapper.toListBookingDto(bookingRepository
                        .findByBookingsOwnerListStatePast(userId.get(), LocalDateTime.now()));
            case "FUTURE":
                log.info("Получение FUTURE Bookings BookingServiceImpl.getBookingsOwnerState, ownerId = {} ", userId);
                return BookingMapper.toListBookingDto(bookingRepository
                        .findByBookingsOwnerListStateFuture(userId.get()));
            case "WAITING":
                log.info("Получение WAITING Bookings BookingServiceImpl.getBookingsOwnerState, ownerId = {} ", userId);
                return BookingMapper.toListBookingDto(bookingRepository
                        .findByBookingsOwnerListStateWaiting(userId.get()));
            case "REJECTED":
                log.info("Получение REJECTED Bookings BookingServiceImpl.getBookingsOwnerState, ownerId = {} ", userId);
                return BookingMapper.toListBookingDto(bookingRepository
                        .findByBookingsOwnerListStateRejected(userId.get()));
            default:
                throw new MessageFailedException();
        }
    }

    public User validationUser(Optional<Long> userId) {
        if (userId.isEmpty()) {
            throw new BadRequestException("Ошибка userId.isEmpty, BookingServiceImpl.validationUser()");
        }
        log.info("Проверка User BookingServiceImpl.validationUser, userId = {}", userId);
        return userRepository.findById(userId.get())
                .orElseThrow(() -> new BookingNotFoundException("Ошибка user не найден, createBooking.updateStatus"));
    }

    public Item validationItem(Optional<BookingDtoIn> bookingDtoIn) {
        if (bookingDtoIn.isEmpty()) {
            throw new BookingNotFoundException("Ошибка bookingDtoIn.isEmpty, BookingServiceImpl.validationItem()");
        }
        Optional<Item> item = itemRepository.findById(bookingDtoIn.get().getItemId());
        if (item.isEmpty()) {
            throw new BookingNotFoundException("Ошибка item.isEmpty, BookingServiceImpl.validationItem()");
        }
        return item.get();
    }

    public Booking validationBooking(Optional<Long> bookingId) {
        if (bookingId.isEmpty()) {
            throw new BadRequestException("Ошибка bookingId.isEmpty, BookingServiceImpl.validationBooking()");
        }
        Optional<Booking> booking = bookingRepository.findById(bookingId.get());
        if (booking.isEmpty()) {
            throw new BookingNotFoundException("Ошибка booking.isEmpty, BookingServiceImpl.validationBooking()");
        }
        log.info("Проверка Booking BookingServiceImpl.validationBooking, booking = {}", booking);
        return booking.get();
    }
}