/**
 * Lenovo.com Inc.
 * Copyright (c) 2004-2016 All Rights Reserved.
 */
package com.wind.algorithm.facade;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.wind.algorithm.exception.ReqParamsException;
import com.wind.algorithm.service.MaxFlowService;

/**
 * <p>
 * </p>
 *
 * @author tangxx3
 * @version $Id: GraphFacade.java, v 0.1 2016年3月10日 下午2:53:23 tangxx3 Exp $
 */
@Component
public class GraphFacade {
    @Autowired
    private MaxFlowService maxFlowService;

    public Object fetchShortestPaths(HttpServletRequest request) {
        String startV = request.getParameter("startV");
        String endV = request.getParameter("endV");
        String type = request.getParameter("type");

        if (startV == null || endV == null || type == null) {
            throw new ReqParamsException("startV=" + startV + ",endV=" + endV + ",type=" + type);
        }

        //计算最短路径
        Object paths = maxFlowService.shortestPath(request, type, startV, endV);

        return paths;
    }
}
