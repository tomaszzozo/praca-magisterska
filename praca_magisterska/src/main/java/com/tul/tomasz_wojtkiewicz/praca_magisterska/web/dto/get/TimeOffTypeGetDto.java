package com.tul.tomasz_wojtkiewicz.praca_magisterska.web.dto.get;

import com.tul.tomasz_wojtkiewicz.praca_magisterska.domain.TimeOffTypeEntity;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.BeanUtils;

@Getter
@Setter
public class TimeOffTypeGetDto {
    private long id;
    private String name;
    private double compensationPercentage;

    public static TimeOffTypeGetDto fromEntity(TimeOffTypeEntity entity) {
        var result = new TimeOffTypeGetDto();
        BeanUtils.copyProperties(entity, result);
        return result;
    }
}
