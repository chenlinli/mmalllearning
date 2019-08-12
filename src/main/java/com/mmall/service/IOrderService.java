package com.mmall.service;

import com.github.pagehelper.PageInfo;
import com.mmall.common.ServerResponse;
import com.mmall.vo.OrderVo;

import java.util.Map;

public interface IOrderService {
    public ServerResponse pay(Integer userId, String path, Long orderNo);

    public ServerResponse aliCallback(Map<String,String> params);

    ServerResponse<Boolean> queryOrderPayStatus(Integer userId, long orderNo);

    ServerResponse createOrder(Integer id, Integer shippingId);

    ServerResponse cancel(Integer userId, Long orderNo);

    ServerResponse getOrderCartProduct(Integer userId);

    ServerResponse getOrderDetail(Integer id, long orderNo);

    ServerResponse<PageInfo> list(Integer id,int pageSize,int pageNum);

    ServerResponse<PageInfo> manageList(int pageSize, int pageNum);

    ServerResponse<OrderVo> manageDetail(long orderNo);

    ServerResponse<PageInfo> manageSearch(long orderNo,int pageNum,int pageSize);

    ServerResponse<String> manageSendGoods(long orderNo);

    //二期定时任务
    void closeOrder(int hour);
}
