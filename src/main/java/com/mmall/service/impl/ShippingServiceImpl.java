package com.mmall.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Maps;
import com.mmall.common.ServerResponse;
import com.mmall.dao.ShippingMapper;
import com.mmall.pojo.Shipping;
import com.mmall.service.IShippingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service("iShippingService")
public class ShippingServiceImpl  implements IShippingService{

    @Autowired
    private ShippingMapper shippingMapper;
    public ServerResponse add(Integer userrId,Shipping shipping){
        shipping.setUserId(userrId);
        int rowCount = shippingMapper.insert(shipping);//会把shipping的id主键填充到对象
        if(rowCount>0){
            Map result = Maps.newHashMap();
            result.put("shippingId",shipping.getId());
            return ServerResponse.createBySuccess("新建地址成功",result);
        }
        return ServerResponse.createByErrorMessage("新建地址失败");
    }

    @Override
    public ServerResponse del(Integer userId, Integer shipingId) {
        //横向越权：登陆了，但是传的不是本用户的userId
        int rowCount = shippingMapper.deleteByShippingIdUserId(userId,shipingId);
        if(rowCount>0){
            return ServerResponse.createBySuccess("删除地址成功");
        }
        return ServerResponse.createByErrorMessage("删除地址失败");
    }

    @Override
    public ServerResponse update(Integer userId, Shipping shipping) {
        shipping.setUserId(userId);//防止横向越权
        //横向越权：登陆了，但是传的不是本用户的userId
        int rowCount = shippingMapper.updateByShipping(shipping);
        if(rowCount>0){
            return ServerResponse.createBySuccess("更新地址成功");
        }
        return ServerResponse.createByErrorMessage("更新地址失败");
    }

    @Override
    public ServerResponse<Shipping> select(Integer userId, Integer shippingId) {
        Shipping shipping = shippingMapper.selectByShippingIdUserId(userId, shippingId);
        if(shipping ==null){
            return ServerResponse.createByErrorMessage("无法查询到该地址");
        }else{
            return ServerResponse.createBySuccess("更改地址成功",shipping);
        }
    }

    @Override
    public ServerResponse<PageInfo> list(Integer userId, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum,pageSize);
        List<Shipping> shippings = shippingMapper.selectByUserId(userId);
        PageInfo pageInfo = new PageInfo(shippings);
        return ServerResponse.createBySuccess(pageInfo);

    }
}

