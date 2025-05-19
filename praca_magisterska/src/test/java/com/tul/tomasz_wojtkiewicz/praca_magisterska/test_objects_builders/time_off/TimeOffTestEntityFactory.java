package com.tul.tomasz_wojtkiewicz.praca_magisterska.test_objects_builders.time_off;

import com.tul.tomasz_wojtkiewicz.praca_magisterska.domain.EmployeeEntity;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.domain.TimeOffEntity;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.domain.TimeOffLimitEntity;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.domain.TimeOffTypeEntity;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.test_objects_builders.time_off_limit.TimeOffLimitTestEntityFactory;
import lombok.Builder;
import lombok.Getter;
import org.springframework.beans.BeanUtils;

import java.time.LocalDate;

import static org.mockito.Mockito.mock;

@Builder
@Getter
public class TimeOffTestEntityFactory {
    @Builder.Default
    private LocalDate firstDay = LocalDate.of(TimeOffLimitTestEntityFactory.build().getLeaveYear(), 3, 10);
    @Builder.Default
    private LocalDate lastDayInclusive = LocalDate.of(TimeOffLimitTestEntityFactory.build().getLeaveYear(), 3, 10);
    @Builder.Default
    private Integer hoursCount = 8;
    @Builder.Default
    private String comment = "";
    @Builder.Default
    private TimeOffLimitEntity timeOffYearlyLimit = mock(TimeOffLimitEntity.class);
    @Builder.Default
    private TimeOffTypeEntity timeOffType = mock(TimeOffTypeEntity.class);
    @Builder.Default
    private EmployeeEntity employee = mock(EmployeeEntity.class);

    public static TimeOffTestEntityFactory build() {
        return builder().build();
    }

    public TimeOffEntity asEntity() {
        var entity = new TimeOffEntity();
        BeanUtils.copyProperties(this, entity);
        return entity;
    }
}
