    package ru.practicum.shareit.user;

    import org.junit.jupiter.api.Assertions;
    import org.junit.jupiter.api.BeforeEach;
    import org.junit.jupiter.api.Test;
    import org.junit.jupiter.api.extension.ExtendWith;
    import org.mockito.Mockito;
    import org.mockito.junit.jupiter.MockitoExtension;
    import ru.practicum.shareit.user.dto.UserDto;
    import ru.practicum.shareit.user.dto.UserMapper;
    import ru.practicum.shareit.user.exception.AlreadyExistsException;
    import ru.practicum.shareit.user.model.User;
    import ru.practicum.shareit.user.repository.UserRepository;
    import ru.practicum.shareit.user.service.UserService;
    import ru.practicum.shareit.user.service.impl.UserServiceImpl;

    import java.util.*;

    @ExtendWith(MockitoExtension.class)
    class UserServiceImplTest {

        private final UserRepository userRepository = Mockito.mock(UserRepository.class);

        private final UserMapper userMapper = Mockito.mock(UserMapper.class);
        private final UserService userService = new UserServiceImpl(userMapper, userRepository);

        private User user;
        private UserDto userDto;
        private List<User> list;

        @BeforeEach
        void beforeEach() {
            user = new User(1L, "user1", "user1@mail.com");
            userDto = new UserDto(1L, "user1", "user1@mail.com");

            list = Collections.singletonList(user);
        }

        @Test
        void getUsersTest() {
            Mockito
                    .when(userRepository.findAll()).thenReturn(list);
            Mockito
                    .when(userMapper.toUserDtoDB(user)).thenReturn(userDto);
            List<UserDto> listTest = userService.getUsers();
            Assertions.assertEquals(1L, listTest.get(0).getId());
            Assertions.assertEquals("user1", listTest.get(0).getName());
            Assertions.assertEquals("user1@mail.com", listTest.get(0).getEmail());
        }

        @Test
        void getUserTest() {
            Mockito
                    .when(userRepository.findById(1L)).thenReturn(Optional.of(user));
            Mockito
                    .when(userMapper.toUserDtoDB(user)).thenReturn(userDto);
            UserDto userDtoTest = userService.getUser(1L);
            Assertions.assertEquals(1L, userDtoTest.getId());
            Assertions.assertEquals("user1", userDtoTest.getName());
            Assertions.assertEquals("user1@mail.com", userDtoTest.getEmail());
        }

        @Test
        void deleteWrongUserTest() {
            Mockito
                    .when(userRepository.findById(1L)).thenReturn(Optional.ofNullable(user));
            Assertions.assertThrows(NoSuchElementException.class, () -> userService.deleteUser(2L));
        }

        @Test
        void deleteUserTest() {
            Mockito
                    .when(userRepository.existsById(1L))
                    .thenReturn(true);
            Mockito
                    .when(userRepository.findById(1L))
                    .thenReturn(Optional.ofNullable(user));

            userService.deleteUser(1L);
            Mockito.verify(userRepository, Mockito.times(1))
                    .delete(user);
        }


        @Test
        void createUserTest() {
            Mockito
                    .when(userService.createUser(userDto)).thenReturn(userDto);
            Mockito
                    .when(userRepository.save(user)).thenReturn(user);
            Mockito
                    .when(userMapper.toUserDtoDB(user)).thenReturn(userDto);
            UserDto userDtoTest = userService.createUser(userDto);
            Assertions.assertEquals(1L, userDtoTest.getId());
            Assertions.assertEquals("user1", userDtoTest.getName());
            Assertions.assertEquals("user1@mail.com", userDtoTest.getEmail());
        }

        @Test
        void updateUserTest() throws AlreadyExistsException {
            Mockito
                    .when(userRepository.existsById(1L)).thenReturn(true);
            Mockito
                    .when(userRepository.findById(1L)).thenReturn(Optional.of(user));
            Mockito
                   .when(userRepository.save(user)).thenReturn(user);
            Mockito
                    .when(userService.updateUser(userDto, 1L)).thenReturn(userDto);

            UserDto userDtoTest = userService.updateUser(userDto, 1L);
            Assertions.assertEquals(1L, userDtoTest.getId());
            Assertions.assertEquals("user1", userDtoTest.getName());
            Assertions.assertEquals("user1@mail.com", userDtoTest.getEmail());
        }

        @Test
        void updateUserExceptionTest() {
            Mockito
                    .when(userRepository.save(user)).thenReturn(user);
            Assertions.assertThrows(AlreadyExistsException.class, () -> userService.updateUser(userDto, null));
        }
    }