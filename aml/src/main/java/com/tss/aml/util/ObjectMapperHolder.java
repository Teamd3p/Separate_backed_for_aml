package com.tss.aml.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;

public class ObjectMapperHolder {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    public static Map<String, Object> readMap(String json) throws Exception {
        return MAPPER.readValue(json, new TypeReference<Map<String, Object>>() {});
    }
}