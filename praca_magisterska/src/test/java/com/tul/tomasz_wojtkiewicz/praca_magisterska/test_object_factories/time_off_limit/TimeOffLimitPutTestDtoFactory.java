package com.tul.tomasz_wojtkiewicz.praca_magisterska.test_object_factories.time_off_limit;

import com.tul.tomasz_wojtkiewicz.praca_magisterska.domain.TimeOffLimitEntity;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.web.dto.put.TimeOffLimitPutDto;
import lombok.Builder;
import lombok.Getter;
import org.springframework.beans.BeanUtils;

@Getter
@Builder
public class TimeOffLimitPutTestDtoFactory {
	private static final TimeOffLimitEntity defaultReference = TimeOffLimitTestEntityFactory.build().asEntity();

	@Builder.Default
	private Long id = 0L;
	@Builder.Default
	private Integer maxHours = defaultReference.getMaxHours();
	@Builder.Default
	private Integer year = defaultReference.getLeaveYear();
	@Builder.Default
	private Long typeId = null;
	@Builder.Default
	private Long employeeId = null;

	public static TimeOffLimitPutTestDtoFactory build() {
		return builder().build();
	}

	public TimeOffLimitPutDto asDto() {
		var entity = new TimeOffLimitPutDto();
		BeanUtils.copyProperties(this, entity);
		return entity;
	}
}
