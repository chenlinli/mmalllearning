package com.mmall.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 购物车产品的Vo
 */
@Data
public class CartProductVo {
    private Integer id;
    private Integer userId;
    private Integer productId;
    private Integer quantity;//购物车此商品数量

    private String productName;
    private String productSubtitle;
    private String productMainImage;
    private BigDecimal productPrice;
    private Integer productStock;
    private BigDecimal productTotalPrice;
    private Integer productStatus;
    private Integer productChecked;//是否勾选

    private String limitQuantity; //限制购物车产品数量的结果

}
