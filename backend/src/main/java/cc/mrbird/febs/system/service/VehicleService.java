/*
 * Copyright (c) Akveo 2019. All Rights Reserved.
 * Licensed under the Personal / Commercial License.
 * See LICENSE_PERSONAL / LICENSE_COMMERCIAL in the project root for license information on type of purchased license.
 */

package cc.mrbird.febs.system.service;

import cc.mrbird.febs.system.domain.Vehicle;

import com.alibaba.fastjson.JSONObject;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface VehicleService {
    public Vehicle updataVehicleByName(JSONObject jsonParam);
    public List<Vehicle> updataVehiclesByName(JSONObject jsonParam);
}
