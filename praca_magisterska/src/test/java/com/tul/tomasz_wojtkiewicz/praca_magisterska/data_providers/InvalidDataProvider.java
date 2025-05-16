package com.tul.tomasz_wojtkiewicz.praca_magisterska.data_providers;

import org.junit.jupiter.params.provider.Arguments;

import java.time.Year;
import java.util.stream.Stream;

public interface InvalidDataProvider {
    static Stream<Arguments> emails() {
        return Stream.of(Arguments.of(""), Arguments.of("invalid-email"), Arguments.of("user@.com"), Arguments.of("user@.com."), Arguments.of("user@-example.com"), Arguments.of("user@example..com"));
    }

    static Stream<Arguments> accessLevels() {
        return Stream.of(Integer.MIN_VALUE, -100, -1, 4, 100, Integer.MAX_VALUE).map(Arguments::of);
    }

    static Stream<Arguments> names() {
        return Stream.of("Al", "Jo", "A", "Jan123", "M@rek", "Kowalski_", "Anna-Maria!", "Łukasz#", "Zażółć@gęślą", "Иван", "张伟", "अर्जुन", "-Anna", ".Jan", "'Kowalski", "Anna-", "Kowalski'", "J@n K0w@lski", "M@r!a", "Zażółć@gęślą", "", " ").map(Arguments::of);
    }

    static Stream<Arguments> phoneNumbers() {
        return Stream.of("", " ", "1", "22", "333", "4444", "55555", "666666", "7777777", "88888888", "9999999999", "99999999999", "asd", "12345678i", "12g345678", "q12345678i", "123-456-789", "123456-78").map(Arguments::of);
    }

    static Stream<Arguments> years() {
        return Stream.of(Year.MIN_VALUE, -100, -1, 0, 1, 2019, 2201, Year.MAX_VALUE).map(Arguments::of);
    }
}
