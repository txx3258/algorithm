/**
 * Lenovo.com Inc.
 * Copyright (c) 2004-2016 All Rights Reserved.
 */
package com.wind.algorithm.web.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private final Logger        LOGGER                   = LoggerFactory.getLogger(this.getClass());

    private static final String PRAGMA                   = "Pragma";
    private static final String NO_CACHE                 = "no-cache";
    private static final String CACHE_CONTROL            = "Cache-Control";
    private static final String EXPIRES                  = "Expires";
    public static final String  AMS_CONTENT_TYPE_HTML    = "text/html;charset=utf-8";

    private static final String CONTENT_TYPE_URL_ENCODED = "application/x-www-form-urlencoded";

    /**
     * 处理post请求
     */
    public <T> T parsePostRequest(HttpServletRequest request, Class<T> clz) {
        Profiler.enter("parsePostRequest...");
        String body = null;

        try {
            String contentType = request.getContentType();
            if (CONTENT_TYPE_URL_ENCODED.equalsIgnoreCase(contentType)) {
                body = getFormUrlencodedPostData(request);
            }

            if (StringUtils.isBlank(body)) {
                body = IOUtils.toString(request.getInputStream());
            }

            LOGGER.info("post params:" + body);
            JSONUtil.josnToObject(body, clz);
        } catch (Exception e) {
            ExceptionUtil.caught(e, e.getMessage(), body);
        } finally {
            Profiler.release();
        }

        return null;
    }

    /**
     * application/x-www-form-urlencoded头提交数据获取结果
     * 
     * @param request
     * @return
     */
    @SuppressWarnings("unchecked")
    private String getFormUrlencodedPostData(HttpServletRequest request) {
        String data = null;
        try {
            Map<String, String> paraMap = request.getParameterMap();
            //只获取第一个Key
            for (Map.Entry<String, String> entry : paraMap.entrySet()) {
                data = entry.getKey();
                if (StringUtils.isBlank(data)) {
                    continue;
                }

                boolean isJson = isJson(data);
                if (isJson) {
                    break;
                }
            }

        } catch (Exception e) {
            ExceptionUtil.caught(e, e.getMessage());
        }

        return data;
    }

    /**
     * 
     * 验证是否是有效的JSON格式信息
     * @param data
     * @return
     */
    private boolean isJson(String data) {
        try {
            JSONUtil.toMap(data);
        } catch (Exception e) {
            return false;
        }

        return true;
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
