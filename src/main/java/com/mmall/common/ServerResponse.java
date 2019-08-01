package com.mmall.common;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.io.Serializable;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
//三个字段有空的字段不再序列化
public class ServerResponse<T> implements Serializable {

    private String msg;
    private int staus;
    private T data;

    private ServerResponse(int status){
        this.staus  = status;
    }

    private ServerResponse(int staus, T data) {
        this.staus = staus;
        this.data = data;
    }

    private ServerResponse( int staus,String msg, T data) {
        this.msg = msg;
        this.staus = staus;
        this.data = data;
    }

    //第二个和第四个如果第二个参数传String,会调用第四个，第二个参数非String会调第二个
    private ServerResponse(int staus,String msg) {
        this.msg = msg;
        this.staus = staus;
    }

    @JsonIgnore //不序列化success字段，json序列化根据方法名
    public boolean isSuccess(){
        return staus==ResponseCode.SUCCESS.getCode();
    }

    public T getData(){
        return  data;
    }

    public String getMsg() {
        return msg;
    }

    public int getStaus() {
        return staus;
    }

    public static <T> ServerResponse<T> createBySuccess(){
        return new ServerResponse<T>(ResponseCode.SUCCESS.getCode());
    }

    public static <T> ServerResponse<T> createBySuccessMNessage(String msg){
        return new ServerResponse<T>(ResponseCode.SUCCESS.getCode(),msg);
    }

    public static <T> ServerResponse<T> createBySuccess(T data){
        return new ServerResponse<T>(ResponseCode.SUCCESS.getCode(),data);
    }

    public static <T> ServerResponse<T> createBySuccess(String msg,T data){
        return new ServerResponse<T>(ResponseCode.SUCCESS.getCode(),msg,data);
    }

    public static <T> ServerResponse<T> createByError(){
        return new  ServerResponse<T>(ResponseCode.ERROR.getCode(),ResponseCode.ERROR.getDesc());
    }

    public static <T> ServerResponse<T> createByErrorMessage(String msg){
        return new  ServerResponse<T>(ResponseCode.ERROR.getCode(),msg);
    }

    public static <T> ServerResponse<T> createByErrorCodeMessage(int errorCode,String errorMsg){
        return new  ServerResponse<T>(errorCode,errorMsg);
    }
}


