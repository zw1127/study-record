/*
 * Copyright (c) 2022 Fiberhome Technologies.
 *
 * No.6, Gaoxin 4th Road, Hongshan District.,Wuhan,P.R.China,
 * Fiberhome Telecommunication Technologies Co.,LTD
 *
 * All rights reserved.
 */
package com.example.demo.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.io.IOException;
import java.io.InputStream;

public final class JsonUtils {

    private JsonUtils() {
    }

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    static {
        OBJECT_MAPPER.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
    }

    public static <T> byte[] writeValueAsBytes(T instance) throws JsonProcessingException {
        return OBJECT_MAPPER.writeValueAsBytes(instance);
    }

    public static <T> String writeValueAsString(T instance) throws JsonProcessingException {
        return OBJECT_MAPPER.writeValueAsString(instance);
    }

    public static <T> T readValue(String json, Class<T> clazz) throws JsonProcessingException {
        return OBJECT_MAPPER.readValue(json, clazz);
    }

    public static <T> T readValue(byte[] src, Class<T> clazz) throws IOException {
        return OBJECT_MAPPER.readValue(src, clazz);
    }

    public static <T> T readValue(InputStream src, Class<T> valueType) throws IOException {
        return OBJECT_MAPPER.readValue(src, valueType);
    }

}
