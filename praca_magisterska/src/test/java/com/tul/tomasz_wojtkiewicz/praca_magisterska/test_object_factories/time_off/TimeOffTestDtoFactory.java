package com.tul.tomasz_wojtkiewicz.praca_magisterska.test_object_factories.time_off;

import com.tul.tomasz_wojtkiewicz.praca_magisterska.domain.TimeOffEntity;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.web.dto.post.TimeOffPostDto;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.web.dto.put.TimeOffPutDto;
import lombok.Builder;
import lombok.Getter;
import org.springframework.beans.BeanUtils;

import java.time.LocalDate;

@Builder
@Getter
public class TimeOffTestDtoFactory {
	private static final TimeOffEntity defaultReference = TimeOffTestEntityFactory.build().asEntity();

	@Builder.Default
	private LocalDate firstDay = defaultReference.getFirstDay();
	@Builder.Default
	private LocalDate lastDayInclusive = defaultReference.getLastDayInclusive();
	@Builder.Default
	private Integer hoursCount = defaultReference.getHoursCount();
	@Builder.Default
	private String comment = defaultReference.getComment();
	@Builder.Default
	private long typeId = 0;
	@Builder.Default
	private long yearlyLimitId = 0;
	@Builder.Default
	private long employeeId = 0;

	public static TimeOffTestDtoFactory build() {
		return builder().build();
	}

	public TimeOffPostDto asPostDto() {
		var dto = new TimeOffPostDto();
		BeanUtils.copyProperties(this, dto);
		return dto;
	}

	public TimeOffPutDto asPutDto() {
		var dto = new TimeOffPutDto();
		BeanUtils.copyProperties(this, dto);
		return dto;
	}
}
