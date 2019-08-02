package com.mmall.dao;

import com.mmall.pojo.Product;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.Param;
import org.springframework.scheduling.support.SimpleTriggerContext;

import java.util.List;

public interface ProductMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Product record);

    int insertSelective(Product record);

    Product selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Product record);

    int updateByPrimaryKey(Product record);

    List<Product> selectList();//sql不能加;因为分页会动态加limit

    List<Product> selectByNameAndProductId(@Param("productName")String productName,@Param("productId") Integer productId);

    List<Product> selectByNameAndCategoryId(@Param(value = "productName") String productName,@Param("categoryIdList")List<Integer> categoryIdList);
}