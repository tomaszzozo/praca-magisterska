package com.tul.tomasz_wojtkiewicz.praca_magisterska.web.dto.get;

import com.tul.tomasz_wojtkiewicz.praca_magisterska.domain.TimeOffEntity;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.BeanUtils;

import java.time.LocalDate;

@Getter
@Setter
public class TimeOffGetDto {
    private long id;
    private LocalDate firstDay;
    private LocalDate lastDayInclusive;
    private int hoursCount;
    private long typeId;
    private long yearlyLimitId;
    private long employeeId;
    private String comment;

    public static TimeOffGetDto fromEntity(TimeOffEntity entity) {
        var result = new TimeOffGetDto();
        BeanUtils.copyProperties(entity, result, "timeOffYearlyLimit", "timeOffType", "employee");
        result.setTypeId(entity.getTimeOffType().getId());
        result.setYearlyLimitId(entity.getTimeOffYearlyLimit().getId());
        result.setEmployeeId(entity.getEmployee().getId());
        return result;
    }
}
