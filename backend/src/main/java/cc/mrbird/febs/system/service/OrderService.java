/*
 * Copyright (c) Akveo 2019. All Rights Reserved.
 * Licensed under the Personal / Commercial License.
 * See LICENSE_PERSONAL / LICENSE_COMMERCIAL in the project root for license information on type of purchased license.
 */

package cc.mrbird.febs.system.service;

import com.alibaba.fastjson.JSONObject;

import java.util.Map;

public interface OrderService {

//    public Map getDataForGrid();

    public String sendOrder(JSONObject jsonParam);
    public String sendOrderRandom();
    public String getOrder(JSONObject jsonParam);
}
