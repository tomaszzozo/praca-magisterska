package com.tul.tomasz_wojtkiewicz.praca_magisterska.test_object_factories.time_off_type;

import com.tul.tomasz_wojtkiewicz.praca_magisterska.domain.TimeOffTypeEntity;
import lombok.Builder;
import lombok.Getter;
import org.springframework.beans.BeanUtils;

@Builder
@Getter
public class TimeOffTypeTestEntityFactory {
    @Builder.Default
    private String name = "Sick leave";
    @Builder.Default
    private Float compensationPercentage = 80.5f;

    public static TimeOffTypeTestEntityFactory build() {
        return builder().build();
    }

    public TimeOffTypeEntity asEntity() {
        var entity = new TimeOffTypeEntity();
        BeanUtils.copyProperties(this, entity);
        return entity;
    }
}
