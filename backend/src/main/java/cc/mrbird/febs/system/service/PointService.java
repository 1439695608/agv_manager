/*
 * Copyright (c) Akveo 2019. All Rights Reserved.
 * Licensed under the Personal / Commercial License.
 * See LICENSE_PERSONAL / LICENSE_COMMERCIAL in the project root for license information on type of purchased license.
 */

package cc.mrbird.febs.system.service;

import cc.mrbird.febs.system.domain.Location;
import cc.mrbird.febs.system.domain.Point;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;


public interface PointService extends IService<Point> {
    public List<Point> addPoints(List<Point> points);
//    public int updataLocationByName(JSONObject jsonParam);
    public List<Point> selectPoints();
}
