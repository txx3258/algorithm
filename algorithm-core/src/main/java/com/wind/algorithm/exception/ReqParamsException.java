/**
 * Lenovo.com Inc.
 * Copyright (c) 2004-2016 All Rights Reserved.
 */
package com.wind.algorithm.exception;

/**
 * <p>
 * </p>
 *
 * @author tangxx3
 * @version $Id: ReqParamsException.java, v 0.1 2016年3月10日 下午2:59:23 tangxx3 Exp $
 */
public class ReqParamsException extends RuntimeException {

    /**  */
    private static final long serialVersionUID = 1L;

    /**
     * 
     */
    public ReqParamsException() {
        super();
    }

    /**
     * @param message
     * @param cause
     */
    public ReqParamsException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * @param message
     */
    public ReqParamsException(String message) {
        super(message);
    }

    /**
     * @param cause
     */
    public ReqParamsException(Throwable cause) {
        super(cause);
    }

}
