/**
 * Lenovo.com Inc.
 * Copyright (c) 2004-2016 All Rights Reserved.
 */
package com.wind.algorithm.util;

import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * </p>
 *
 * @author tangxx3
 * @version $Id: JSONUtil.java, v 0.1 2016年3月9日 下午2:17:02 tangxx3 Exp $
 */
public class JSONUtil {
    private static final Logger       LOG      = LoggerFactory.getLogger(JSONUtil.class);
    private static final ObjectMapper MAPPER   = new ObjectMapper();
    private static JSONUtil           jsonUtil = null;

    private JSONUtil() {
    }

    public static JSONUtil getInstance() {
        synchronized (JSONUtil.class) {
            if (jsonUtil == null) {
                jsonUtil = new JSONUtil();
            }
        }

        return jsonUtil;
    }

    public static String fromObject(Object obj) throws IOException, JsonGenerationException {
        StringWriter stringWriter = new StringWriter();
        MAPPER.writeValue(stringWriter, obj);
        return stringWriter.toString();
    }

    public static String fromListForData(List<?> list) throws IOException, JsonGenerationException {
        StringWriter stringWriter = new StringWriter();
        stringWriter.write("{data:[");
        for (int i = 0; i < list.size(); i++) {
            stringWriter.write(fromObject(list.get(i)));
            if (i != list.size() - 1) {
                stringWriter.write(",");
            }
        }
        stringWriter.write("]}");
        return stringWriter.toString();
    }

    public static List<?> toList(String json) throws IOException, JsonGenerationException {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Get json string is:" + json);
        }
        MAPPER.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
        return MAPPER.readValue(json, List.class);
    }

    public static Map<?, ?> toMap(String json) throws IOException, JsonGenerationException {
        MAPPER.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
        return MAPPER.readValue(json, Map.class);
    }

    public static Map<?, ?> toMap1(String json) throws Exception {
        MAPPER.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
        return MAPPER.readValue(json, Map.class);
    }

    /**
     * 从Json字串中得到指定属性值 
     * @param jsonStr
     * @param proertyName
     * @return
     */
    @SuppressWarnings({ "rawtypes", "static-access" })
    public static Object getFromJson(String jsonStr, String proertyName) {
        Map map = new HashMap();
        try {
            map = JSONUtil.getInstance().toMap(jsonStr);
        } catch (Exception e) {
            LOG.error("", e);
        }
        return (Object) map.get(proertyName);
    }

    /**
     * JSON转化为对象.
     * 
     * @param json
     * @param clazz
     * @return
     * @throws JsonParseException
     * @throws JsonMappingException
     * @throws IOException
     */
    public static <T> T josnToObject(String json, Class<T> clazz) throws JsonParseException, IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(json, clazz);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public Object json2Object(String json, Class clazz) throws JsonParseException, IOException {

        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(json, clazz);
    }
}
