/*
 * Copyright (c) Akveo 2019. All Rights Reserved.
 * Licensed under the Personal / Commercial License.
 * See LICENSE_PERSONAL / LICENSE_COMMERCIAL in the project root for license information on type of purchased license.
 */

package cc.mrbird.febs.system.controller;

import cc.mrbird.febs.system.service.MapService;
import cc.mrbird.febs.system.service.OrderService;
import cc.mrbird.febs.util.Json;
import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;


/**
 * Controller for managing users
 */
@Controller
@RequestMapping("/order")
public class OrderController {

    private OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @ResponseBody
    @RequestMapping(value = "/sendOrderRandom", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public String sendOrderRadom(HttpServletRequest request) {
        JSONObject result = new JSONObject();
        //获取到JSONObject
//        JSONObject jsonParam = Json.getJSONParam(request);
        result.put("msg", "ok");
        result.put("method", "request");
        result.put("data", orderService.sendOrderRandom());
        return result.toString();
    }
    @ResponseBody
    @RequestMapping(value = "/sendOrder", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public String sendOrder(HttpServletRequest request) {
        JSONObject result = new JSONObject();
        //获取到JSONObject
        JSONObject jsonParam = Json.getJSONParam(request);
        result.put("msg", "ok");
        result.put("method", "request");
        result.put("data", orderService.sendOrder(jsonParam));
        return result.toString();
    }
    @ResponseBody
    @RequestMapping(value = "/getOrder", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public String getOrder(HttpServletRequest request) {

        //获取到JSONObject
        JSONObject jsonParam = Json.getJSONParam(request);
        String res = orderService.getOrder(jsonParam);
        // 将获取的json数据封装一层，然后在给返回
        JSONObject result = new JSONObject();
        result.put("msg", "ok");
        result.put("method", "request");
        result.put("data", JSONObject.parse(res));
        return result.toString();
    }
}
