package com.mmall.common;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.io.Serializable;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
//三个字段有空的字段不再序列化
public class ServerResponse<T> implements Serializable {

    private String msg;
    private int status;
    private T data;

    private ServerResponse(int status){
        this.status  = status;
    }

    private ServerResponse(int status, T data) {
        this.status = status;
        this.data = data;
    }

    private ServerResponse(int status,String msg, T data) {
        this.msg = msg;
        this.status = status;
        this.data = data;
    }

    //第二个和第四个如果第二个参数传String,会调用第四个，第二个参数非String会调第二个
    private ServerResponse(int status,String msg) {
        this.msg = msg;
        this.status = status;
    }

    @JsonIgnore //不序列化success字段，json序列化根据方法名
    public boolean isSuccess(){
        return status==ResponseCode.SUCCESS.getCode();
    }

    public T getData(){
        return  data;
    }

    public String getMsg() {
        return msg;
    }

    public int getStatus() {
        return status;
    }

    public static <T> ServerResponse<T> createBySuccess(){
        return new ServerResponse<T>(ResponseCode.SUCCESS.getCode());
    }

    public static <T> ServerResponse<T> createBySuccessMessage(String msg){
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

    public static <T> ServerResponse<T> createByErrorCodeNEEDLOGIN(String msg){
        return new ServerResponse<T>(ResponseCode.NEED_LOGIN.getCode(),msg);
    }

    public static <T> ServerResponse<T> createByErrorCodeNEEDLOGIN(){
        return new ServerResponse<T>(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
    }


    public static <T> ServerResponse<T> createByErrorCodeIllegaArg(){
        return new ServerResponse<T>(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
    }
}


