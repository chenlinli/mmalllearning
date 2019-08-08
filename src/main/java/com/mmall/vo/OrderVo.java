package com.mmall.vo;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
@Getter
@Setter
public class OrderVo {
    private Long orderNo;
    private Integer shippingId;
    private BigDecimal payment;
    private String paymentDesc;
    private Integer paymentType;
    private Integer postage;
    private Integer status;
    private String statusDes;

    private String paymentTime;
    private String sendTime;
    private String endTime;
    private String closeTime;
    private String createTime;

    private List<OrderItemVo> orderItemVoList;

    private String imageHost;

    private String receiverName;

    private ShippingVo shippingVo;

}
