package com.tul.tomasz_wojtkiewicz.praca_magisterska;

import lombok.experimental.UtilityClass;
import org.junit.jupiter.params.provider.Arguments;

import java.util.stream.Stream;

@UtilityClass
public class InvalidDataProvider {
    public static Stream<Arguments> emails() {
        return Stream.of(
                Arguments.of(""),
                Arguments.of("invalid-email"),
                Arguments.of("user@.com"),
                Arguments.of("user@.com."),
                Arguments.of("user@-example.com"),
                Arguments.of("user@example..com")
        );
    }

    public static Stream<Arguments> accessLevels() {
        return Stream.of(Integer.MIN_VALUE, -1234567890, -123456789, -12345678, -1234567, -123456, -12345, -1234, -123, -12, -1, 4, 12, 123, 1234, 12345, 123456, 1234567, 12345678, 123456789, 1234567890, Integer.MAX_VALUE).map(Arguments::of);
    }

    public static Stream<Arguments> names() {
        return Stream.of(
                "Al",
                "Jo",
                "A",
                "Jan123",
                "M@rek",
                "Kowalski_",
                "Anna-Maria!",
                "Łukasz#",
                "Zażółć@gęślą",
                "Иван",
                "张伟",
                "अर्जुन",
                "-Anna",
                ".Jan",
                "'Kowalski",
                "Anna-",
                "Kowalski'",
                "J@n K0w@lski",
                "M@r!a",
                "Zażółć@gęślą",
                "",
                " "
        ).map(Arguments::of);
    }

    public static Stream<Arguments> phoneNumbers() {
        return Stream.of(
                "",
                " ",
                "1",
                "22",
                "333",
                "4444",
                "55555",
                "666666",
                "7777777",
                "88888888",
                "9999999999",
                "99999999999",
                "asd",
                "12345678i",
                "12g345678",
                "q12345678i",
                "123-456-789",
                "123456-78"
        ).map(Arguments::of);
    }
}
