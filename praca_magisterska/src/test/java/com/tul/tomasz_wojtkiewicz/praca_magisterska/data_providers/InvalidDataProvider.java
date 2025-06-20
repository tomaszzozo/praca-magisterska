package com.tul.tomasz_wojtkiewicz.praca_magisterska.data_providers;

import org.junit.jupiter.params.provider.Arguments;

import java.time.LocalDate;
import java.time.Year;
import java.util.stream.Stream;

public interface InvalidDataProvider {
	static Stream<Arguments> integerNegativeAndZero() {
		return Stream.of(Integer.MIN_VALUE, -1000, -1, 0).map(Arguments::of);
	}

	static Stream<Arguments> emails() {
		return Stream.of(Arguments.of(""), Arguments.of("invalid-email"), Arguments.of("user@.com"), Arguments.of("user@.com."), Arguments.of("user@-example.com"), Arguments.of("user@example..com"));
	}

	static Stream<Arguments> accessLevels() {
		return Stream.of(Integer.MIN_VALUE, -100, -1, 4, 100, Integer.MAX_VALUE).map(Arguments::of);
	}

	static Stream<Arguments> names() {
		return Stream.of("Al", "Jo", "A", "Jan123", "M@rek", "Kowalski_", "Anna-Maria!", "Łukasz#", "Zażółć@gęślą", "Иван", "张伟", "अर्जुन", "-Anna", ".Jan", "'Kowalski", "Anna-", "Kowalski'", "J@n K0w@lski", "M@r!a", "Zażółć@gęślą", "", " ").map(Arguments::of);
	}

	static Stream<Arguments> hoursInYearMoreThanMax() {
		return Stream.concat(Stream.of(2025, 2026, 2027, 2028).map(year -> Arguments.of(year, LocalDate.of(year, 12, 31).getDayOfYear() * 24 + 1)), Stream.of(Arguments.of(2025, Integer.MAX_VALUE / 2), Arguments.of(2025, Integer.MAX_VALUE)));
	}

	static Stream<Arguments> hoursInYearNegative() {
		return Stream.of(Integer.MIN_VALUE, -100, -1).map(Arguments::of);
	}

	static Stream<Arguments> hoursCountMoreThanHoursInTimeOff() {
		return Stream.of(Arguments.of(25, LocalDate.of(2025, 6, 10), LocalDate.of(2025, 6, 10)), Arguments.of(49, LocalDate.of(2025, 6, 10), LocalDate.of(2025, 6, 11)), Arguments.of(200, LocalDate.of(2025, 6, 1), LocalDate.of(2025, 6, 7)));
	}

	static Stream<Arguments> firstOrLastDayWithYearOutsideOfRange() {
		return Stream.of(Arguments.of(LocalDate.of(LocalDate.MIN.getYear(), 6, 10), LocalDate.of(2025, 6, 10)), Arguments.of(LocalDate.of(2025, 6, 10), LocalDate.of(LocalDate.MAX.getYear(), 6, 10)), Arguments.of(LocalDate.of(LocalDate.MIN.getYear(), 6, 10), LocalDate.of(LocalDate.MAX.getYear(), 6, 10)), Arguments.of(LocalDate.of(-100, 6, 10), LocalDate.of(LocalDate.MIN.getYear(), 6, 10)), Arguments.of(LocalDate.of(2025, 6, 10), LocalDate.of(2400, 6, 10)), Arguments.of(LocalDate.of(2019, 6, 10), LocalDate.of(2101, 6, 10)), Arguments.of(LocalDate.of(2025, 6, 10), LocalDate.of(2101, 6, 10)), Arguments.of(LocalDate.of(2019, 6, 10), LocalDate.of(2025, 6, 10)));
	}

	static Stream<Arguments> differentDates() {
		var starter = LocalDate.of(2025, 6, 10);
		return Stream.of(Arguments.of(starter, starter.plusMonths(1)), Arguments.of(starter.minusMonths(1), starter), Arguments.of(starter.minusMonths(1), starter.plusMonths(1)), Arguments.of(starter.minusYears(1).minusMonths(1), starter.minusYears(1)), Arguments.of(starter.minusYears(1), starter.minusYears(1).minusMonths(1)), Arguments.of(starter.minusYears(1).minusDays(1), starter.plusYears(1).minusMonths(1).plusDays(1)));
	}


	static Stream<Arguments> phoneNumbers() {
		return Stream.of("", " ", "1", "22", "333", "4444", "55555", "666666", "7777777", "88888888", "9999999999", "99999999999", "asd", "12345678i", "12g345678", "q12345678i", "123-456-789", "123456-78").map(Arguments::of);
	}

	static Stream<Arguments> years() {
		return Stream.of(Year.MIN_VALUE, -100, -1, 0, 1, 2019, 2101, Year.MAX_VALUE).map(Arguments::of);
	}

	static Stream<Arguments> compensationPercentages() {
		return Stream.of(-Float.MAX_VALUE, -100f, -Float.MIN_VALUE, 100.00001f, 500f, Float.MAX_VALUE).map(Arguments::of);
	}

	static Stream<Arguments> firstDayAfterLastDay() {
		var now = LocalDate.now();
		return Stream.of(1, 10, 100, 1000).map(daysAfter -> Arguments.of(now, now.plusDays(daysAfter)));
	}
}
