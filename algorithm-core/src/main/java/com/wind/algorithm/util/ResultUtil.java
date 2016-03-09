/**
 * Lenovo.com Inc.
 * Copyright (c) 2004-2016 All Rights Reserved.
 */
package com.wind.algorithm.util;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * </p>
 *
 * @author tangxx3
 * @version $Id: ResultUtil.java, v 0.1 2016年3月9日 下午2:13:01 tangxx3 Exp $
 */
public final class ResultUtil {
    private static final String SUCCESS = "success";

    private static final String CODE    = "code";

    private static final String MSG     = "msg";

    private static final String DATA    = "data";

    private ResultUtil() {

    }

    public static Map<String, Object> fail(String code, String msg) {
        Map<String, Object> result = new HashMap<String, Object>();
        result.put(SUCCESS, Boolean.FALSE);
        result.put(CODE, code);
        result.put(MSG, msg);
        result.put(DATA, null);

        return result;
    }

    public static Map<String, Object> fail(String code, String msg, Object data) {
        Map<String, Object> result = new HashMap<String, Object>();
        result.put(SUCCESS, Boolean.FALSE);
        result.put(CODE, code);
        result.put(MSG, msg);
        result.put(DATA, data);

        return result;
    }

    public static Map<String, Object> success(Object data) {
        Map<String, Object> result = new HashMap<String, Object>();
        result.put(SUCCESS, Boolean.TRUE);
        result.put(DATA, data);
        result.put(CODE, null);
        result.put(MSG, null);

        return result;
    }
}
