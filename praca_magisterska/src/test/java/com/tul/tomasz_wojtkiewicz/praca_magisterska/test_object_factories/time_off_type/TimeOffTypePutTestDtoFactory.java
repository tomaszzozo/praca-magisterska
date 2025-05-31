package com.tul.tomasz_wojtkiewicz.praca_magisterska.test_object_factories.time_off_type;

import com.tul.tomasz_wojtkiewicz.praca_magisterska.domain.TimeOffTypeEntity;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.web.dto.put.TimeOffTypePutDto;
import lombok.Builder;
import lombok.Getter;
import org.springframework.beans.BeanUtils;

@Builder
@Getter
public class TimeOffTypePutTestDtoFactory {
	private static final TimeOffTypeEntity defaultReference = TimeOffTypeTestEntityFactory.build().asEntity();

	@Builder.Default
	private final Long id = 0L;
	@Builder.Default
	private final String name = defaultReference.getName();
	@Builder.Default
	private final Float compensationPercentage = defaultReference.getCompensationPercentage();

	public static TimeOffTypePutTestDtoFactory build() {
		return builder().build();
	}

	public TimeOffTypePutDto asDto() {
		var entity = new TimeOffTypePutDto();
		BeanUtils.copyProperties(this, entity);
		return entity;
	}
}
