package com.tul.tomasz_wojtkiewicz.praca_magisterska;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@SpringBootTest
public abstract class IntegrationTestsBase {
	@Autowired
	private JdbcTemplate jdbcTemplate;

	protected <T> List<Map<String, Object>> getDbTableDump(Class<T> entityClass) {
		var tableName = "SELECT * FROM %s".formatted(Arrays.stream(entityClass.getName().split("\\.")).toList().getLast().replaceAll("([A-Z])", "_$1")).replaceFirst("_", "");
		return jdbcTemplate.queryForList(tableName.substring(0, 1).toUpperCase() + tableName.substring(1));
	}
}
