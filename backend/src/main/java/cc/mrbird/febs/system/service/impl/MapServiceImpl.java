/*
 * Copyright (c) Akveo 2019. All Rights Reserved.
 * Licensed under the Personal / Commercial License.
 * See LICENSE_PERSONAL / LICENSE_COMMERCIAL in the project root for license information on type of purchased license.
 */

package cc.mrbird.febs.system.service.impl;

import cc.mrbird.febs.system.dao.*;
import cc.mrbird.febs.system.domain.Location;
import cc.mrbird.febs.system.domain.MapInfo;

import cc.mrbird.febs.system.domain.Point;
import cc.mrbird.febs.system.domain.User;
import cc.mrbird.febs.system.service.LocationService;
import cc.mrbird.febs.system.service.MapService;
import cc.mrbird.febs.system.service.PointService;
import cc.mrbird.febs.system.service.UserService;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;

@Slf4j
@Service("MapService")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class MapServiceImpl extends ServiceImpl<MapinfoMapper, MapInfo> implements MapService {
    private Logger logger = LoggerFactory.getLogger(MapServiceImpl.class);

//    @Autowired(required = false)
//    private LocationMapper locationMapper;
//    @Autowired(required = false)
//    private PointMapper pointMapper;
    @Autowired
    private LocationService locationService;
    @Autowired
    private PointService pointService;
    public Map getDataForGrid() {
        Map resultMap = new HashMap();
        try {
//            resultMap.put("point_arr", pointRepository.findAll());
//            resultMap.put("location_arr", locationRepository.findAll());
            List<Location> locationArr = locationService.selectLocations();

            resultMap.put("locationArr", locationArr);
            return resultMap;
        } catch (Exception e){
            e.printStackTrace();
        }
        return resultMap;
    }

    public boolean saveDataForGrid(JSONObject jsonParam) {
        Map resultMap = new HashMap();
//        resultMap.put("point_arr", pointRepository.saveAll(entities));
        List<Point> pointArr = new ArrayList<>();
        List<Location> locationArr = new ArrayList<>();
        try {
            List<Map> pointArrParam = (List)jsonParam.get("pointArr");
            List<Map> locationArrParam = (List)jsonParam.get("locationArr");
            for (int i=0;i<pointArrParam.size();i++) {
                Point tempPoint = new Point();
                tempPoint.setName((String) pointArrParam.get(i).get("name"));
                tempPoint.setType(pointArrParam.get(i).get("type").toString());
                tempPoint.setXposition(Integer.valueOf((String) pointArrParam.get(i).get("xPosition")));
                tempPoint.setYposition(Integer.valueOf((String) pointArrParam.get(i).get("yPosition")));
                tempPoint.setZposition(0);
                pointArr.add(tempPoint);
            }
            for (int i=0;i<locationArrParam.size();i++) {
                Location tempLocation = new Location();
                tempLocation.setName((String) locationArrParam.get(i).get("name"));
                tempLocation.setType((String) locationArrParam.get(i).get("type").toString());
                tempLocation.setRealName((String) locationArrParam.get(i).get("realName"));
                tempLocation.setXposition(Integer.valueOf((String) locationArrParam.get(i).get("xPosition")));
                tempLocation.setYposition(Integer.valueOf((String) locationArrParam.get(i).get("yPosition")));
                tempLocation.setZposition(0);
                tempLocation.setState("2".equals(locationArrParam.get(i).get("type").toString()) ? "0" : "1");
                locationArr.add(tempLocation);
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        boolean res1 = pointService.saveBatch(pointArr);
        boolean res2 = locationService.saveBatch(locationArr);
//        System.out.println(((List)jsonParam.get("mapArr")).size());
//        return res1.size() > 0 && res2.size() > 0;
        return res1 && res2;
    }

}
