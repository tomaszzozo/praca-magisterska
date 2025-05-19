package com.tul.tomasz_wojtkiewicz.praca_magisterska.test_objects_builders.employee;

import com.tul.tomasz_wojtkiewicz.praca_magisterska.domain.EmployeeEntity;
import lombok.Builder;
import lombok.Getter;
import org.junit.jupiter.params.provider.Arguments;
import org.springframework.beans.BeanUtils;

import java.util.List;
import java.util.stream.Stream;

@Builder
@Getter
public class EmployeeTestEntityFactory {
    @Builder.Default
    private String email = "bruceWayne@gmail.com";
    @Builder.Default
    private String firstName = "Bruce";
    @Builder.Default
    private String lastName = "Wayne";
    @Builder.Default
    private String phoneNumber = "123456789";
    @Builder.Default
    private Integer accessLevel = 3;

    public static EmployeeTestEntityFactory build() {
        return builder().build();
    }

    public EmployeeEntity asEntity() {
        var entity = new EmployeeEntity();
        BeanUtils.copyProperties(this, entity);
        return entity;
    }

    public static List<EmployeeEntity> buildTen() {
        return Stream.of(Arguments.of("Ruggiero", "Stealy", "rstealy0@plala.or.jp", "712568069"), Arguments.of("Jan", "Żółcik", "cbiggin1@tiny.cc", "709347655"), Arguments.of("Żaneta", "Krysiak", "sbowlesworth2@de.vu", "452172090"), Arguments.of("Waneta", "Karus", "wkarus3@reverbnation.com", "164781938"), Arguments.of("Roxine", "Maypother", "rmaypother4@paginegialle.it", "131396665"), Arguments.of("Clyde", "Beyer", "cbeyer5@dyndns.org", "560599143"), Arguments.of("Gal", "McWhorter", "gmcwhorter6@noaa.gov", "618709380"), Arguments.of("Parry", "Pascoe", "ppascoe7@zimbio.com", "226206126"), Arguments.of("Teriann", "Huffy", "thuffy8@wunderground.com", "823368512"), Arguments.of("Ilyse", "Sandeford", "isandeford9@cdc.gov", "861156530")).map(a -> builder().firstName((String) a.get()[0]).lastName((String) a.get()[1]).email((String) a.get()[2]).phoneNumber((String) a.get()[3]).build().asEntity()).toList();
    }
}
