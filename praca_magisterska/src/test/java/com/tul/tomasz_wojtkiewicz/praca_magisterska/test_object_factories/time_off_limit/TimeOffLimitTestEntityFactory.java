package com.tul.tomasz_wojtkiewicz.praca_magisterska.test_object_factories.time_off_limit;

import com.tul.tomasz_wojtkiewicz.praca_magisterska.domain.EmployeeEntity;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.domain.TimeOffLimitEntity;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.domain.TimeOffTypeEntity;
import lombok.Builder;
import lombok.Getter;
import org.springframework.beans.BeanUtils;

import static org.mockito.Mockito.mock;

@Builder
@Getter
public class TimeOffLimitTestEntityFactory {
	@Builder.Default
	private Integer leaveYear = 2025;
	@Builder.Default
	private Integer maxHours = new TimeOffLimitEntity().getMaxHours();
	@Builder.Default
	private TimeOffTypeEntity timeOffType = mock(TimeOffTypeEntity.class);
	@Builder.Default
	private EmployeeEntity employee = mock(EmployeeEntity.class);

	public static TimeOffLimitTestEntityFactory build() {
		return builder().build();
	}

	public TimeOffLimitEntity asEntity() {
		var entity = new TimeOffLimitEntity();
		BeanUtils.copyProperties(this, entity);
		return entity;
	}
}
