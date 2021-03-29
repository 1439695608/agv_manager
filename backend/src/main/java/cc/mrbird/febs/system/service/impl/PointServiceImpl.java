package cc.mrbird.febs.system.service.impl;

import cc.mrbird.febs.system.dao.LocationMapper;
import cc.mrbird.febs.system.dao.PointMapper;
import cc.mrbird.febs.system.domain.Location;
import cc.mrbird.febs.system.domain.Point;
import cc.mrbird.febs.system.service.LocationService;
import cc.mrbird.febs.system.service.PointService;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service("PointService")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class PointServiceImpl extends ServiceImpl<PointMapper, Point> implements PointService {

    @Autowired
    private PointMapper pointMapper;

    @Override
    public List<Point> addPoints(List<Point> points) {
        for (Point point: points) {
            pointMapper.insert(point);
        }
        return null;
    }

    @Override
    public List<Point> selectPoints() {
        return pointMapper.selectList(null);
    }
}
