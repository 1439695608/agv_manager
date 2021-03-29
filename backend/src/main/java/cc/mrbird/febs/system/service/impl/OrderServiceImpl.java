/*
 * Copyright (c) Akveo 2019. All Rights Reserved.
 * Licensed under the Personal / Commercial License.
 * See LICENSE_PERSONAL / LICENSE_COMMERCIAL in the project root for license information on type of purchased license.
 */

package cc.mrbird.febs.system.service.impl;
import cc.mrbird.febs.system.domain.Location;
import cc.mrbird.febs.system.service.LocationService;
import cc.mrbird.febs.system.service.OrderService;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Slf4j
@Service("OrderService")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class OrderServiceImpl implements OrderService {
    private Logger logger = LoggerFactory.getLogger(OrderServiceImpl.class);

    private RestTemplate restTemplate = new RestTemplate();
    @Autowired
    private LocationService locationService;

    @Override
    public String sendOrder(JSONObject jsonParam) {
        RestTemplate client = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        HttpMethod method = HttpMethod.POST;
        // 以表单的方式提交
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        //将请求头部和参数合成一个请求
        HttpEntity requestEntity = new HttpEntity<>(jsonParam.toString(), headers);
        //执行HTTP请求，将返回的结构使用ResultVO类格式化
        String url="http://192.168.10.182:55201/v1/transportOrders/Order-" + new Date().getTime();
        ResponseEntity<String> response = client.exchange(url, method, requestEntity, String.class);
        return response.getBody();
    }

    @Override
    public String sendOrderRandom() {
        RestTemplate client = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        HttpMethod method = HttpMethod.POST;
        // 以表单的方式提交
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        //将请求头部和参数合成一个请求
        JSONObject requestBody = new JSONObject();
        List destinations = new ArrayList();
        requestBody.put("destinations", destinations);
        requestBody.put("deadline", new Date());

        Map destination1 = new HashMap();
        QueryWrapper queryStartWrapper = new QueryWrapper();
        queryStartWrapper.eq("state", "1");
        List<Location> startLocation = locationService.list(queryStartWrapper);
        int locationIndex = (int) (((startLocation.size() - 1) * Math.random()));
        destination1.put("locationName",startLocation.get(locationIndex).getRealName());
        destination1.put("operation","load");
        Map destination2 = new HashMap();
        QueryWrapper queryEndWrapper = new QueryWrapper();
        queryEndWrapper.eq("state", "0");
        List<Location> endLocation = locationService.list(queryEndWrapper);
        int locationIndex2 = (int) (((endLocation.size() - 1) * Math.random()));
        destination2.put("locationName",endLocation.get(locationIndex2).getRealName());
        destination2.put("operation","unload");

        destinations.add(destination1);
        destinations.add(destination2);
        System.out.println(requestBody);
        System.out.println(requestBody.toString());
        HttpEntity requestEntity = new HttpEntity<>(requestBody.toString(), headers);
        //执行HTTP请求，将返回的结构使用ResultVO类格式化
        String url="http://192.168.10.182:55201/v1/transportOrders/Order-" + new Date().getTime();
        ResponseEntity<String> response = client.exchange(url, method, requestEntity, String.class);
        return response.getBody();
    }

    @Override
    public String getOrder(JSONObject jsonParam) {
        String url="http://192.168.10.182:55201/v1/transportOrders";
        System.out.println(restTemplate.getForObject(url, String.class));
        return restTemplate.getForObject(url, String.class);
    }
}
