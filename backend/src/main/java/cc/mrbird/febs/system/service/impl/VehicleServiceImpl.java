package cc.mrbird.febs.system.service.impl;

import cc.mrbird.febs.system.dao.LocationMapper;
import cc.mrbird.febs.system.dao.VehicleMapper;
import cc.mrbird.febs.system.domain.Location;
import cc.mrbird.febs.system.domain.Vehicle;
import cc.mrbird.febs.system.service.LocationService;
import cc.mrbird.febs.system.service.VehicleService;
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
import java.util.Map;

@Slf4j
@Service("VehicleService")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class VehicleServiceImpl extends ServiceImpl<VehicleMapper, Vehicle> implements VehicleService {

    @Autowired
    private VehicleMapper vehicleMapper;

    @Override
    public Vehicle updataVehicleByName(JSONObject jsonParam) {
        try{
            String position = (String) jsonParam.get("position");
            String state = (String) jsonParam.get("state");
            String loadState = (String) jsonParam.get("loadState");
            String electricLevel = (String) jsonParam.get("electricLevel");
            String name = (String) jsonParam.get("name");
            System.out.println(position + state + electricLevel + name);
            QueryWrapper<Vehicle> vehicleQueryWrapper = new QueryWrapper<>();
            vehicleQueryWrapper.eq("name", name);
            System.out.println(vehicleMapper.selectOne(vehicleQueryWrapper));
            Vehicle vehicle = vehicleMapper.selectOne(vehicleQueryWrapper);
            int updateRes = 0;
            if (vehicle != null) {
                if (loadState == null){
                    loadState = vehicle.getLoadState();
                }
                Vehicle vehicle1 = new Vehicle();
                vehicle1.setElectricLevel("100");
                vehicle1.setLoadState(loadState);
                vehicle1.setName(name);
                vehicle1.setPosition(position);
                vehicle1.setState(state);
                updateRes = vehicleMapper.update(vehicle1, vehicleQueryWrapper);
                if (updateRes > 0) {
                    vehicle.setState(state);
                    vehicle.setPosition(position);
                    vehicle.setElectricLevel(electricLevel);
                    vehicle.setLoadState(loadState);
                    return vehicle;
                }
            } else {
                Vehicle vehicle1 = new Vehicle();
                vehicle1.setElectricLevel(electricLevel);
                vehicle1.setName(name);
                vehicle1.setPosition(position);
                vehicle1.setState(state);
                vehicleMapper.insert(vehicle1);
            }
//            return vehicleRepository.updataVehicleByName(position, state, electricLevel, name);
        } catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Vehicle> updataVehiclesByName(JSONObject jsonParam) {
        List<Vehicle> vehicles = new ArrayList<>();
        try{
            List<Map> vehicleArr = (List<Map>) jsonParam.get("vehicleArr");
            for (Map map: vehicleArr) {
                map.put("position", map.get("currentPosition"));
                vehicles.add(updataVehicleByName((JSONObject) map));
            }
            return vehicles;
        } catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
}
