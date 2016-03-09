/**
 * Lenovo.com Inc.
 * Copyright (c) 2004-2016 All Rights Reserved.
 */
package com.wind.algorithm.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * </p>
 *
 * @author tangxx3
 * @version $Id: ExceptionUtil.java, v 0.1 2016年3月9日 下午1:52:49 tangxx3 Exp $
 */
public final class ExceptionUtil {
    private static final Logger LOGGER       = LoggerFactory.getLogger("COMMON-ERROR");

    private static final String SEPERATOR    = "|";

    private static final String EMPTY_STRING = "";

    private ExceptionUtil() {

    }

    /**
     * 捕获异常.
     * 
     * @param e   
     * @param bizDesc  业务描述
     */
    public static void caught(Exception e, String... bizDesc) {
        if (null == e) {//e instanceof AmsNullClientInfoException ||
            return;
        }

        LOGGER.error(buildDesc(bizDesc), e);
    }

    private static String buildDesc(String... bizDesc) {
        if (null == bizDesc) {
            return EMPTY_STRING;
        }

        StringBuilder sb = new StringBuilder();
        for (String desc : bizDesc) {
            if (sb.length() > 0) {
                sb.append(SEPERATOR);
            }
            sb.append(desc);
        }

        return sb.toString();
    }
}
