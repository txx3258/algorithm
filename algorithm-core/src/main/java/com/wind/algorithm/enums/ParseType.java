/**
 * Lenovo.com Inc.
 * Copyright (c) 2004-2016 All Rights Reserved.
 */
package com.wind.algorithm.enums;

/**
 * <p>
 * </p>
 *
 * @author tangxx3
 * @version $Id: ParseType.java, v 0.1 2016年3月10日 上午11:37:40 tangxx3 Exp $
 */
public enum ParseType {
    FILE_PARSE(0, "file"), HTTP_PARSE(1, "http");
    private int    enumVal; ;
    private String type;

    public int getEnumVal() {
        return enumVal;
    }

    public String getType() {
        return type;
    }

    private ParseType(int enumVal, String type) {
        this.enumVal = enumVal;
        this.type = type;
    }

    public static ParseType getParseTypeByType(String type) {
        for (ParseType parse : values()) {
            if (parse.getType().equals(type)) {
                return parse;
            }
        }
        return null;
    }
}
