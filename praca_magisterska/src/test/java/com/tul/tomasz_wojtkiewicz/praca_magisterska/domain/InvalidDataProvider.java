package com.tul.tomasz_wojtkiewicz.praca_magisterska.domain;

import lombok.experimental.UtilityClass;
import org.junit.jupiter.params.provider.Arguments;

import java.util.stream.Stream;

@UtilityClass
class InvalidDataProvider {
    static Stream<Arguments> provideInvalidEmails() {
        return Stream.of(
                Arguments.of(""),
                Arguments.of("invalid-email"),
                Arguments.of("user@.com"),
                Arguments.of("user@.com."),
                Arguments.of("user@-example.com"),
                Arguments.of("user@example..com")
        );
    }

    static Stream<Arguments> provideInvalidNames() {
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

    static Stream<Arguments> provideInvalidPhoneNumbers() {
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
