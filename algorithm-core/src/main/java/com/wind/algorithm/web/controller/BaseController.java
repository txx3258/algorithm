/**
 * Lenovo.com Inc.
 * Copyright (c) 2004-2016 All Rights Reserved.
 */
package com.wind.algorithm.web.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.ExceptionHandler;

import com.wind.algorithm.util.ExceptionUtil;
import com.wind.algorithm.util.JSONUtil;
import com.wind.algorithm.util.Profiler;
import com.wind.algorithm.util.ResultUtil;

/**
 * <p>
 * </p>
 *
 * @author tangxx3
 * @version $Id: BaseController.java, v 0.1 2016年3月7日 上午11:05:47 tangxx3 Exp $
 */
public abstract class BaseController {

    private static final String PRAGMA                = "Pragma";
    private static final String NO_CACHE              = "no-cache";
    private static final String CACHE_CONTROL         = "Cache-Control";
    private static final String EXPIRES               = "Expires";
    public static final String  AMS_CONTENT_TYPE_HTML = "text/html;charset=utf-8";

    /**
     * 处理post请求
     */
    public <T> T parsePostRequest(HttpServletRequest request, Class<T> clz) {
        Profiler.enter("parsePostRequest...");
        try {

            return null;
        } finally {
            Profiler.release();
        }
    }

    /**
     * 处理异常
     * 
     * @param response
     * @param e
     */
    @ExceptionHandler(RuntimeException.class)
    public void dealRuntimeException(HttpServletResponse response, RuntimeException e) {
        sendError(response, "RuntimeException", e.getMessage());
    }

    /**
     * 发送失败
     * @param response
     * @param object
     */
    protected void sendError(HttpServletResponse response, String code, String msg) {
        try {
            response.setHeader(PRAGMA, NO_CACHE);
            response.setHeader(CACHE_CONTROL, NO_CACHE);
            response.setDateHeader(EXPIRES, 0);
            response.setContentType(AMS_CONTENT_TYPE_HTML);

            String content = JSONUtil.fromObject(ResultUtil.fail(code, msg));
            response.getWriter().write(content);
        } catch (Exception e) {
            ExceptionUtil.caught(e, e.getMessage(), "buildNoCacheResponse is Exception");
        }
    }

    /**
     * 发送结果
     * @param response
     * @param object
     */
    protected void sendWithoutCache(HttpServletResponse response, Object object) {
        try {
            response.setHeader(PRAGMA, NO_CACHE);
            response.setHeader(CACHE_CONTROL, NO_CACHE);
            response.setDateHeader(EXPIRES, 0);
            response.setContentType(AMS_CONTENT_TYPE_HTML);

            String content = JSONUtil.fromObject(ResultUtil.success(object));
            response.getWriter().write(content);
        } catch (Exception e) {
            ExceptionUtil.caught(e, e.getMessage(), "buildNoCacheResponse is Exception");
        }
    }
}
