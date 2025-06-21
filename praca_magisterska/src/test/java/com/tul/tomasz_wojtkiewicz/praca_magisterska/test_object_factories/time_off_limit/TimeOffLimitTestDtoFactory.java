package com.tul.tomasz_wojtkiewicz.praca_magisterska.test_object_factories.time_off_limit;

import com.tul.tomasz_wojtkiewicz.praca_magisterska.domain.TimeOffLimitEntity;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.web.dto.put.TimeOffLimitPutDto;
import lombok.Builder;
import lombok.Getter;
import org.springframework.beans.BeanUtils;

@Getter
@Builder
public class TimeOffLimitTestDtoFactory {
	private static final TimeOffLimitEntity defaultReference = TimeOffLimitTestEntityFactory.build().asEntity();

	@Builder.Default
	private Long id = 0L;
	@Builder.Default
	private Integer maxHours = defaultReference.getMaxHours();
	@Builder.Default
	private Integer year = defaultReference.getLeaveYear();
	@Builder.Default
	private Long typeId = 1L;
	@Builder.Default
	private Long employeeId = 1L;

	public static TimeOffLimitTestDtoFactory build() {
		return builder().build();
	}

	public TimeOffLimitPutDto asPutDto() {
		var entity = new TimeOffLimitPutDto();
		BeanUtils.copyProperties(this, entity);
		return entity;
	}
}
