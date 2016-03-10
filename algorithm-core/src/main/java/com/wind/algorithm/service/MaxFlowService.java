/**
 * Lenovo.com Inc.
 * Copyright (c) 2004-2016 All Rights Reserved.
 */
package com.wind.algorithm.service;

import org.jgrapht.WeightedGraph;
import org.jgrapht.alg.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.wind.algorithm.service.parse.WeightedGraphParse;
import com.wind.algorithm.util.ExceptionUtil;

/**
 * <p>
 * </p>
 *
 * @author tangxx3
 * @version $Id: MaxFlowService.java, v 0.1 2016年3月10日 上午9:08:17 tangxx3 Exp $
 */
@Service
public class MaxFlowService {
    @Autowired
    private WeightedGraphParse weightedGraphParse;

    /**
     * 获取最短路径
     * @param way
     * @param type
     * @param startV
     * @param endV
     * @return
     */
    public Object shortestPath(Object way, String type, String startV, String endV) {
        Object paths = null;
        try {
            WeightedGraph<String, DefaultWeightedEdge> graph = weightedGraphParse.parseByType(way, type);

            DijkstraShortestPath<String, DefaultWeightedEdge> shortest = new DijkstraShortestPath<String, DefaultWeightedEdge>(
                graph, startV, endV);

            paths = shortest.getPath().getEdgeList();
        } catch (Exception e) {
            ExceptionUtil.caught(e, e.getMessage());
        }

        return paths;

    }
}
