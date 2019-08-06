package com.mmall.vo;

import com.mmall.utils.BigDecimalUtil;

import java.math.BigDecimal;
import java.util.List;

public class OrderProductVo {
    private List<OrderItemVo> orderItemVoList;
    private BigDecimal productTotalPrice;
    private String imagHost;

    public List<OrderItemVo> getOrderItemVoList() {
        return orderItemVoList;
    }

    public void setOrderItemVoList(List<OrderItemVo> orderItemVoList) {
        this.orderItemVoList = orderItemVoList;
    }

    public BigDecimal getProductTotalPrice() {
        return productTotalPrice;
    }

    public void setProductTotalPrice(BigDecimal productTotalPrice) {
        this.productTotalPrice = productTotalPrice;
    }

    public String getImagHost() {
        return imagHost;
    }

    public void setImagHost(String imagHost) {
        this.imagHost = imagHost;
    }
}
