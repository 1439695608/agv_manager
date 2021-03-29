package cc.mrbird.febs.system.service.impl;

import cc.mrbird.febs.system.dao.LocationMapper;
import cc.mrbird.febs.system.domain.*;
import cc.mrbird.febs.system.service.LocationService;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service("LocationService")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class LocationServiceImpl extends ServiceImpl<LocationMapper, Location> implements LocationService {

    @Autowired
    private LocationMapper locationMapper;

    @Override
    public List<Location> selectLocations() {
        QueryWrapper<Location> wp = new QueryWrapper();
//        wp.eq();
        return this.locationMapper.selectList(null);
    }

    @Override
    public int updataLocationByName(JSONObject jsonParam) {
        try{
            String locationType = (String) jsonParam.get("type");
            String realName = (String) jsonParam.get("realName");
            String state = locationType == "2" ? "0": "1";
            QueryWrapper<Location> locationQueryWrapper = new QueryWrapper<>();
            locationQueryWrapper.eq("real_name", realName);
            Location location = locationMapper.selectOne(locationQueryWrapper);
            location.setType(locationType);
            location.setState(state);
            UpdateWrapper<Location> updateWrapper = new UpdateWrapper();
            updateWrapper.eq("real_name", realName);
            return locationMapper.update(location, updateWrapper);
        } catch (Exception e){
            e.printStackTrace();
        }
        return 0;
    }
}
