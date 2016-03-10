/**
 * Lenovo.com Inc.
 * Copyright (c) 2004-2016 All Rights Reserved.
 */
package com.wind.algorithm.service.parse;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.IOUtils;
import org.jgrapht.WeightedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;
import org.springframework.stereotype.Component;

import com.wind.algorithm.enums.ParseType;
import com.wind.algorithm.util.ExceptionUtil;

/**
 * <p>
 * </p>
 *
 * @author tangxx3
 * @version $Id: WeightedGraphParse.java, v 0.1 2016年3月10日 上午9:09:48 tangxx3 Exp $
 */
@Component
public class WeightedGraphParse {

    /**
     * 从文件读取构建权重图
     * @return
     * @throws Exception 
     */
    public WeightedGraph<String, DefaultWeightedEdge> parseByType(Object way, String type) throws Exception {
        WeightedGraph<String, DefaultWeightedEdge> result = new SimpleDirectedWeightedGraph<String, DefaultWeightedEdge>(
            DefaultWeightedEdge.class);
        Parse<String, DefaultWeightedEdge> parse = null;

        ParseType parseType = ParseType.getParseTypeByType(type);

        switch (parseType) {
            case FILE_PARSE:
                parse = new FileVistor<String, DefaultWeightedEdge>((String) way, result);
                break;
            case HTTP_PARSE:
                parse = new HttpVistor<String, DefaultWeightedEdge>((HttpServletRequest) way, result);
                break;
            default:
                break;
        }

        //转换图形
        parse.convertGraph();

        return result;
    }

    /**
     * 提供访问遍历的接口
     */
    interface Vistor {
        String next() throws Exception;

        void end() throws Exception;
    }

    abstract class Parse<T, V> implements Vistor {
        private static final String START   = "###@@@!!!";
        private static final String V_SPLIT = ",";
        private static final String E_SPLIT = ";";

        boolean                     vStart  = false;
        boolean                     eStart  = false;
        WeightedGraph<T, V>         wGraph;

        @SuppressWarnings("unchecked")
        protected void convertGraph() throws Exception {
            try {
                String line = null;
                while ((line = next()) != null) {
                    if (START.equals(line.trim())) {
                        vStart = true;
                        eStart = false;
                        continue;
                    }

                    if (vStart) {
                        String[] vertexs = line.split(V_SPLIT);
                        for (String vertex : vertexs) {
                            wGraph.addVertex((T) vertex);
                        }
                        vStart = false;
                        eStart = true;
                        continue;
                    }

                    if (eStart && line.indexOf(E_SPLIT) != -1) {
                        String[] edges = line.split(E_SPLIT);
                        wGraph.setEdgeWeight(wGraph.addEdge((T) edges[0], (T) edges[1]), Float.parseFloat(edges[2]));
                    }
                }

            } catch (Exception e) {
                ExceptionUtil.caught(e, e.getMessage());
            } finally {
                end();
            }

        }
    }

    class FileVistor<T, V> extends Parse<T, V> implements Vistor {
        private BufferedReader reader;

        FileVistor(String path, WeightedGraph<T, V> wGraph) {
            readFromFile(path);

            super.wGraph = wGraph;
        }

        /**
         * 从指定路径读取文件
         * @param path
         */
        private void readFromFile(String path) {
            try {
                reader = new BufferedReader(new FileReader(new File(path)));
            } catch (FileNotFoundException e) {
                reader = null;
            }
        }

        /** 
         * @throws IOException 
         * @see com.wind.algorithm.service.parse.WeightedGraphParse.Vistor#next()
         */
        @Override
        public String next() throws Exception {
            return reader.readLine();
        }

        /** 
         * @throws IOException 
         * @see com.wind.algorithm.service.parse.WeightedGraphParse.Vistor#end()
         */
        @Override
        public void end() throws Exception {
            if (reader != null) {
                reader.close();
            }
        }

    }

    class HttpVistor<T, V> extends Parse<T, V> implements Vistor {
        private static final String SPLIT = "\n";

        private String[]            contents;
        private int                 length;
        private int                 index = 0;

        HttpVistor(HttpServletRequest request, WeightedGraph<T, V> wGraph) {
            readFromHttp(request);

            super.wGraph = wGraph;
        }

        /**
         * 
         * @param request
         */
        private void readFromHttp(HttpServletRequest request) {
            try {
                String content = IOUtils.toString(request.getInputStream());
                contents = content.split(SPLIT);
                length = contents.length;
            } catch (Exception e) {
                //logger.error("", e);
            }
        }

        /** 
         * @see com.wind.algorithm.service.parse.WeightedGraphParse.Vistor#next()
         */
        @Override
        public String next() throws Exception {
            if (index < length) {
                return contents[index++];
            }

            return null;
        }

        /** 
         * @see com.wind.algorithm.service.parse.WeightedGraphParse.Vistor#end()
         */
        @Override
        public void end() throws Exception {
        }

    }
}
