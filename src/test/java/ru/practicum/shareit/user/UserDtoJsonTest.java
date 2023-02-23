    package ru.practicum.shareit.user;

    import org.junit.jupiter.api.Test;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.boot.test.autoconfigure.json.JsonTest;
    import org.springframework.boot.test.json.JacksonTester;
    import org.springframework.boot.test.json.JsonContent;
    import ru.practicum.shareit.user.dto.UserDto;

    import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

    @JsonTest
    public class UserDtoJsonTest {
        @Autowired
        private JacksonTester<UserDto> jsonUserDto;

        @Test
        void testUserDto() throws Exception {
            UserDto userDto = new UserDto(1L, "user1", "user@mail.com");

            JsonContent<UserDto> result = jsonUserDto.write(userDto);
            assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
            assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("user1");
            assertThat(result).extractingJsonPathStringValue("$.email").isEqualTo("user@mail.com");
        }
    }