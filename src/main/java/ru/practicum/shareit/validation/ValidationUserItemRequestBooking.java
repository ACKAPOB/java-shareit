package ru.practicum.shareit.validation;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.exception.BadRequestException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Optional;
@Slf4j
@Service
public class ValidationUserItemRequestBooking {
    UserRepository userRepository;

    public User validationUser (Optional<Long> idUser) {
        if (idUser.isEmpty()) {
            throw new BadRequestException("Ошибка idUser isEmpty, ItemRequestServiceImpl.validationUser()");
        }
        Optional<User> user = userRepository.findById(idUser.get());
        if (user.isEmpty()) {
            throw new BadRequestException("Ошибка user isEmpty, ItemRequestServiceImpl.validationUser()");
        }
        return user.get();
    }
}
