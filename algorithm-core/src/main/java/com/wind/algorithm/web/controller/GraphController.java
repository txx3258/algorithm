/**
 * Lenovo.com Inc.
 * Copyright (c) 2004-2016 All Rights Reserved.
 */
package com.wind.algorithm.web.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * <p>
 * </p>
 *
 * @author tangxx3
 * @version $Id: GraphController.java, v 0.1 2016年3月9日 下午4:06:28 tangxx3 Exp $
 */
@Controller
public class GraphController extends BaseController {

    @RequestMapping("/index")
    public void index(HttpServletRequest request, HttpServletResponse response) throws IOException {
        sendWithoutCache(response, "hello world");
    }
}
