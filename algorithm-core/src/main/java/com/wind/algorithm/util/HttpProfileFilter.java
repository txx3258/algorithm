/**
 * Lenovo.com Inc.
 * Copyright (c) 2004-2016 All Rights Reserved.
 */
package com.wind.algorithm.util;

import java.io.IOException;
import java.text.MessageFormat;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * </p>
 *
 * @author tangxx3
 * @version $Id: HttpProfileFilter.java, v 0.1 2016年3月9日 下午2:28:16 tangxx3 Exp $
 */
public class HttpProfileFilter implements Filter {

    /** 日志. */
    private final Logger     perf                         = LoggerFactory.getLogger(LoggerNames.PERF);

    private static final int DEFAULT_LOG_WARN_THRESHOLD   = 100;

    private static final int DEFAULT_LOG_DETAIL_THRESHOLD = 20;

    private int              logWarnThreshold;

    /** 记录详细信息阀值20ms. */
    private int              logDetailThreshold;

    /** 
     * @see javax.servlet.Filter#destroy()
     */
    @Override
    public void destroy() {
        //do nothing
    }

    /** 
     * @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest, javax.servlet.ServletResponse, javax.servlet.FilterChain)
     */
    @Override
    public void doFilter(ServletRequest arg0, ServletResponse arg1, FilterChain chain) throws IOException,
                                                                                      ServletException {
        HttpServletRequest request = (HttpServletRequest) arg0;
        HttpServletResponse response = (HttpServletResponse) arg1;

        String requestString = dumpQueryString(request);
        Profiler.start("process http request");
        boolean isErorr = Boolean.FALSE;
        try {
            chain.doFilter(request, response);
        } catch (Exception e) {
            ExceptionUtil.caught(e, "process http error,requestString=" + requestString);
            isErorr = Boolean.TRUE;
        } finally {
            Profiler.release();
            long duration = Profiler.getDuration();
            String detail = Profiler.dump("Detail: ", "        ");

            String erorr = "" + Profiler.get("error");
            if (!isErorr && BooleanUtils.toBoolean(erorr)) {
                isErorr = Boolean.TRUE;
            }

            if (isErorr) {
                perf.warn(MessageFormat.format("|N|{0,number}|{1}: {2}\n{3}", new Object[] { Long.valueOf(duration),
                        requestString, "ERROR", detail }));
            } else if (duration > logWarnThreshold) {
                perf.warn(MessageFormat.format("|Y|{0,number}|{1}\n{2}", new Object[] { Long.valueOf(duration),
                        requestString, detail }));
            } else if (perf.isInfoEnabled() && duration >= logDetailThreshold) {
                perf.info(MessageFormat.format("|Y|{0,number}|{1}\n{2}", new Object[] { Long.valueOf(duration),
                        requestString, detail }));
            } else if (perf.isInfoEnabled() && duration >= 0) {
                //响应时间20ms以下，不打印详细信息，减少日志大小
                perf.info(MessageFormat.format("|Y|{0,number}|{1}", new Object[] { Long.valueOf(duration),
                        requestString }));
            } else {
                perf.info(MessageFormat.format("|Y|{0,number}|{1}\n{2}", new Object[] { Long.valueOf(duration),
                        requestString, detail }));
            }

            Profiler.reset();
        }
    }

    /**
     * 取得请求内容.
     * 
     * @param request
     * @return
     */
    private String dumpQueryString(HttpServletRequest request) {
        StringBuilder sb = new StringBuilder();
        sb.append(request.getMethod());
        sb.append("|").append(request.getRequestURI());

        String queryString = request.getQueryString();
        if (StringUtils.isNotEmpty(queryString)) {
            sb.append("?").append(queryString);
        }

        //        //        String clientId = null;
        //        //        try {
        //        //            clientId = AmsUtil.getClientId(request, null);
        //        //        } catch (Exception e) {
        //        //            //ignore
        //        //        }
        //        //        sb.append("|clientid=").append(clientId);
        //
        //        Object errorCode = Profiler.get("errorCode");
        //        if (errorCode != null && StringUtils.isNotBlank(errorCode + "")) {
        //            sb.append("|errorCode=").append(errorCode);
        //        }

        return sb.toString();
    }

    /** 
     * @see javax.servlet.Filter#init(javax.servlet.FilterConfig)
     */
    @Override
    public void init(FilterConfig arg0) throws ServletException {
        String minLogWarnTime = arg0.getInitParameter("minLogWarnTime");
        logWarnThreshold = StringUtils.isEmpty(minLogWarnTime) ? DEFAULT_LOG_WARN_THRESHOLD : Integer
            .parseInt(minLogWarnTime);

        String minLogDetailTime = arg0.getInitParameter("minLogDetailTime");
        logDetailThreshold = StringUtils.isEmpty(minLogDetailTime) ? DEFAULT_LOG_DETAIL_THRESHOLD : Integer
            .parseInt(minLogDetailTime);
    }

}
