package com.tul.tomasz_wojtkiewicz.praca_magisterska.test_object_factories.time_off_type;

import com.tul.tomasz_wojtkiewicz.praca_magisterska.web.dto.put.TimeOffTypePutDto;
import org.springframework.beans.BeanUtils;

public class TestTimeOffTypePutDtoBuilder {
    private final TimeOffTypePutDto dto;

    public TestTimeOffTypePutDtoBuilder() {
        dto = new TimeOffTypePutDto();
        BeanUtils.copyProperties(new TestTimeOffTypeEntityBuilder().build(), dto);
    }

    public TestTimeOffTypePutDtoBuilder withName(String name) {
        dto.setName(name);
        return this;
    }

    public TestTimeOffTypePutDtoBuilder withCompensationPercentage(float compensationPercentage) {
        dto.setCompensationPercentage(compensationPercentage);
        return this;
    }

    public TestTimeOffTypePutDtoBuilder withId(long id) {
        dto.setId(id);
        return this;
    }

    public TimeOffTypePutDto build() {
        return dto;
    }
}
