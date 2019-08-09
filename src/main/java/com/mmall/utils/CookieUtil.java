package com.mmall.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
public class CookieUtil {

    //*.happymmall.com下的cookie
    //www开头的是一级域名,
    private final static String COOKIE_DOMAIN = ".happymmall.com";
    private final static String COOKIE_NAME="mmall_login_token";

    public static String readLoginToken(HttpServletRequest request){
        Cookie[] cookies = request.getCookies();
        if(cookies!=null){
            for(Cookie cookie:cookies){
                log.info("read cookieName:{},cookieValue:{}",cookie.getName(),cookie.getValue());
                if(StringUtils.equals(cookie.getName(),COOKIE_NAME)){
                    log.info("return cookieName:{},cookieValue:{}",cookie.getName(),cookie.getValue());
                    return cookie.getValue();
                }
            }
        }
        return null;

    }


    /**
     * X：                              cookie:domain=".happymmall.com" path="/"
     *
     * a,b,c,d,e可以拿到X的cookie
     * a，b互相拿不到彼此的cookie
     * c,d可以共享a的cookie,c和d 不能拿到彼此的cookie
     * c,d不能拿到b的cookie
     * c,d能共享e的cookie
     *
     *
     * domain:
     * a:     A.happymall.com           cookie:domian=A.happymmall.com path="/"
     * b:     B.happymall.com           cookie:domian=B.happymmall.com path="/"
     * c:     A.happymall.com/test/cc   cookie:domian=A.happymmall.com/test/cc path="/test/cc"
     * d:     A.happymall.com/test/dd   cookie:domian=A.happymmall.com/test/dd path="/test/dd"
     * e:     A.happymall.com/test      cookie:domian=A.happymmall.com/test path="/test"

     * 写cookie
     * @param response
     * @param token
     */
    public static void writeLoginToken(HttpServletResponse response,String token){
        Cookie cookie = new Cookie(COOKIE_NAME,token);
        //设置域
        cookie.setDomain(COOKIE_DOMAIN);
        cookie.setPath("/");//根目录，“test”下的子目录才能获取cookie
        cookie.setHttpOnly(true);//排除cookie脚本攻击，不允许脚本访问cookie
        //设置有效期:不设置的话cookie不会写入硬盘，在内存，只在当前页面有效
        //-1：永久有效的cookie
        //一年有效期
        cookie.setMaxAge(60*60*24*356);
        log.info("write cookieName:{},cookieValue:{}",cookie.getName(),cookie.getValue());
        response.addCookie(cookie);
    }

    /**
     * 退出登录删除cookie
     */
    public  static void delLoginToken(HttpServletRequest request,HttpServletResponse response){
        Cookie[] cookies = request.getCookies();
        if(cookies!=null) {
            for (Cookie cookie : cookies) {
                if (StringUtils.equals(cookie.getName(), COOKIE_NAME)) {
                    //删除
                    cookie.setDomain(COOKIE_DOMAIN);
                    cookie.setPath("/");
                    cookie.setMaxAge(0);//有效期0
                    log.info("del cookieName:{},cookieValue:{}",cookie.getName(),cookie.getValue());
                    response.addCookie(cookie);
                    return ;
                }
            }
        }
    }
}
