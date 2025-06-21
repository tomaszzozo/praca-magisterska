package com.tul.tomasz_wojtkiewicz.praca_magisterska.data_providers;

import org.junit.jupiter.params.provider.Arguments;

import java.time.LocalDate;
import java.util.stream.Stream;

public interface ValidDataProvider {
	static Stream<Arguments> hoursInYearLessThanOrMax() {
		return Stream.concat(Stream.of(2025, 2026, 2027, 2028).map(year -> Arguments.of(year, LocalDate.of(year, 12, 31).getDayOfYear() * 24)), Stream.of(Arguments.of(2029, 50)));
	}

	static Stream<Arguments> firstAndLastDayWithYearInRange() {
		return Stream.of(Arguments.of(LocalDate.of(2025, 6, 10), LocalDate.of(2025, 6, 10)), Arguments.of(LocalDate.of(2025, 6, 10), LocalDate.of(2026, 6, 10)), Arguments.of(LocalDate.of(2020, 6, 10), LocalDate.of(2025, 6, 10)), Arguments.of(LocalDate.of(2025, 6, 10), LocalDate.of(2100, 6, 10)));
	}

	static Stream<Arguments> sameDatesDifferentDays() {
		var starter = LocalDate.of(2025, 6, 10);
		return Stream.of(Arguments.of(starter, starter), Arguments.of(starter, starter.plusDays(1)), Arguments.of(starter.minusDays(1), starter), Arguments.of(starter.minusDays(1), starter.plusDays(1)), Arguments.of(starter.minusMonths(1), starter.minusMonths(1)), Arguments.of(starter.minusYears(1), starter.minusYears(1)), Arguments.of(starter.minusYears(1).minusMonths(1), starter.minusYears(1).minusMonths(1)), Arguments.of(starter.minusYears(1).minusMonths(1).minusDays(1), starter.minusYears(1).minusMonths(1).plusDays(1)));
	}

	static Stream<Arguments> hoursCountLessOrEqualHoursInTimeOff() {
		return Stream.of(Arguments.of(24, LocalDate.of(2025, 6, 10), LocalDate.of(2025, 6, 10)), Arguments.of(48, LocalDate.of(2025, 6, 10), LocalDate.of(2025, 6, 11)), Arguments.of(0, LocalDate.of(2025, 6, 10), LocalDate.of(2025, 6, 10)), Arguments.of(168, LocalDate.of(2025, 6, 1), LocalDate.of(2025, 6, 7)));
	}

	static Stream<Arguments> firstDayBeforeOrSameAsLastDay() {
		var now = LocalDate.now();
		return Stream.of(0, 1, 10, 100, 1000).map(daysBefore -> Arguments.of(now, now.minusDays(daysBefore)));
	}
}
