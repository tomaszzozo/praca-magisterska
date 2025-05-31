package com.tul.tomasz_wojtkiewicz.praca_magisterska.test_object_factories.time_off_type;

import com.tul.tomasz_wojtkiewicz.praca_magisterska.domain.TimeOffEntity;
import com.tul.tomasz_wojtkiewicz.praca_magisterska.domain.TimeOffTypeEntity;

import java.util.List;

public class TestTimeOffTypeEntityBuilder {
    public interface Defaults {
        String name = "Sick leave";
        float compensationPercentage = 1;
    }

    private final TimeOffTypeEntity entity;

    public TestTimeOffTypeEntityBuilder() {
        entity = new TimeOffTypeEntity();
        entity.setName(Defaults.name);
        entity.setCompensationPercentage(Defaults.compensationPercentage);
    }

    public TestTimeOffTypeEntityBuilder withName(String name) {
        entity.setName(name);
        return this;
    }

    public TestTimeOffTypeEntityBuilder withTimeOffs(List<TimeOffEntity> timeOffs) {
        entity.setTimeOffs(timeOffs);
        return this;
    }

    public TestTimeOffTypeEntityBuilder withCompensationPercentage(float compensationPercentage) {
        entity.setCompensationPercentage(compensationPercentage);
        return this;
    }

    public TimeOffTypeEntity build() {
        return entity;
    }
}
