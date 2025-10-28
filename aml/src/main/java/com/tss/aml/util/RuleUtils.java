package com.tss.aml.util;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;

public class RuleUtils {
	public static String getString(Map<String, Object> m, String k) {
		return Optional.ofNullable(m).map(x -> x.get(k)).map(Object::toString).orElse(null);
	}

	public static Integer getInt(Map<String, Object> m, String k) {
		return Optional.ofNullable(m).map(x -> x.get(k)).filter(v -> v instanceof Number)
				.map(v -> ((Number) v).intValue()).orElse(null);
	}

	public static BigDecimal getBigDecimal(Map<String, Object> m, String k) {
		return Optional.ofNullable(m).map(x -> x.get(k)).map(Object::toString).map(BigDecimal::new).orElse(null);
	}
}