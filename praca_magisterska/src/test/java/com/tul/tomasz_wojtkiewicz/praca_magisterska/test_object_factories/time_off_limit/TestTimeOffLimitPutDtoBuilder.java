package com.tul.tomasz_wojtkiewicz.praca_magisterska.test_object_factories.time_off_limit;

import com.tul.tomasz_wojtkiewicz.praca_magisterska.web.dto.put.TimeOffLimitPutDto;

public class TestTimeOffLimitPutDtoBuilder {
    private final TimeOffLimitPutDto dto = new TimeOffLimitPutDto();

    public TestTimeOffLimitPutDtoBuilder() {
        dto.setMaxHours(TestTimeOffLimitEntityBuilder.Defaults.maxHours);
        dto.setYear(TestTimeOffLimitEntityBuilder.Defaults.leaveYear);
        dto.setEmployeeId(1);
        dto.setTypeId(1);
        dto.setId(0);
    }

    public TestTimeOffLimitPutDtoBuilder withMaxHours(int maxHours) {
        dto.setMaxHours(maxHours);
        return this;
    }

    public TestTimeOffLimitPutDtoBuilder withTypeId(long id) {
        dto.setTypeId(id);
        return this;
    }

    public TestTimeOffLimitPutDtoBuilder withEmployeeId(long id) {
        dto.setEmployeeId(id);
        return this;
    }

    public TestTimeOffLimitPutDtoBuilder withId(long id) {
        dto.setId(id);
        return this;
    }

    public TimeOffLimitPutDto build() {
        return dto;
    }
}
