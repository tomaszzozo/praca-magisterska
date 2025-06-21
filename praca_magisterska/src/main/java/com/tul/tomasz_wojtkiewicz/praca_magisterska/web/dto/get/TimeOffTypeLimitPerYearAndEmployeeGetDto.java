package com.tul.tomasz_wojtkiewicz.praca_magisterska.web.dto.get;

import com.tul.tomasz_wojtkiewicz.praca_magisterska.domain.TimeOffLimitEntity;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.BeanUtils;

@Getter
@Setter
public class TimeOffTypeLimitPerYearAndEmployeeGetDto {
    private long id;
    private int maxHours;
    private int leaveYear;
    private long typeId;
    private long employeeId;

    public static TimeOffTypeLimitPerYearAndEmployeeGetDto fromEntity(TimeOffLimitEntity entity) {
        var result = new TimeOffTypeLimitPerYearAndEmployeeGetDto();
        BeanUtils.copyProperties(entity, result, "timeOffType", "employee", "timeOffs");
        result.setTypeId(entity.getTimeOffType().getId());
        result.setEmployeeId(entity.getEmployee().getId());
        return result;
    }
}
