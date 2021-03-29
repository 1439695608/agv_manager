/*
 * Copyright (c) Akveo 2019. All Rights Reserved.
 * Licensed under the Personal / Commercial License.
 * See LICENSE_PERSONAL / LICENSE_COMMERCIAL in the project root for license information on type of purchased license.
 */

package cc.mrbird.febs.system.controller;

import cc.mrbird.febs.system.domain.Vehicle;
import cc.mrbird.febs.system.service.VehicleService;
import cc.mrbird.febs.util.Json;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;


/**
 * Controller for managing users
 */
@Controller
@RequestMapping("/vehicle")
public class VehicleController {

    private VehicleService vehicleService;

    @Autowired
    public VehicleController(VehicleService vehicleService) {
        this.vehicleService = vehicleService;
    }
//
    @ResponseBody
    @RequestMapping(value = "/updateByName", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public String updateByName(HttpServletRequest request) {

        //获取到JSONObject
        JSONObject jsonParam = Json.getJSONParam(request);
        Vehicle res = vehicleService.updataVehicleByName(jsonParam);
        // 将获取的json数据封装一层，然后在给返回
        JSONObject result = new JSONObject();
        result.put("msg", "ok");
        result.put("method", "request");
        result.put("data", res);
        return result.toString();
    }

    @ResponseBody
    @RequestMapping(value = "/updateManyByName", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public String updateManyByName(HttpServletRequest request) {
        //获取到JSONObject
        JSONObject jsonParam = Json.getJSONParam(request);
        List<Vehicle> res = vehicleService.updataVehiclesByName(jsonParam);
        // 将获取的json数据封装一层，然后在给返回
        JSONObject result = new JSONObject();
        result.put("msg", "ok");
        result.put("method", "request");
        result.put("data", res);
        return result.toString();
    }
}
