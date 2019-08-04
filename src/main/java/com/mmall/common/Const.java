package com.mmall.common;

import com.google.common.collect.Sets;
import org.apache.commons.lang3.StringUtils;

import java.util.Set;

public class Const {

    public static final String CURRENT_USER="current_user";

    public static final String EMAIL="email";
    public static final String USERNAME="username";

    public interface Role{
        int ROLE_CUSTOMER = 0;//普通用户
        int ROLL_ADMIN=1;//管理员
    }

    public interface  Cart{
        int CHECKED = 1;//选中
        int UN_CHECKED = 0;//未选中
        String LIMIT_NUM_FAIL="LIMIT_NUM_FAIL";
        String LIMIT_NUM_SUCCESS = "LIMIT_NUM_SUCCESS";
    }
    public interface ProductListOrderBy{
        Set<String> PRICE_ASC_DESC= Sets.newHashSet("price_desc","price_asc");
    }
    public enum ProductStatusEnum{

        ON_SALE(1,"在线");

        private String value;
        private int code;

        ProductStatusEnum(int code,String value){
            this.code = code;
            this.value = value;
        }

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }
    }
}
