package com.mmall.controller.common;

import com.mmall.common.Const;
import com.mmall.common.RedisPool;
import com.mmall.pojo.User;
import com.mmall.utils.CookieUtil;
import com.mmall.utils.JsonUtil;
import com.mmall.utils.RedisPoolUtil;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public class SessionExpireFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        String loginToken = CookieUtil.readLoginToken(request);
        if(StringUtils.isNotEmpty(loginToken)){
            //呐user
            String userJsonstr = RedisPoolUtil.get(loginToken);
            User user = JsonUtil.string2Obj(userJsonstr, User.class);
            if(user!=null){
                //重置过期时间
                RedisPoolUtil.expire(loginToken, Const.RedisCacheExtime.REDIS_SESSION_TIME);
            }
        }
        filterChain.doFilter(servletRequest,servletResponse);
    }

    @Override
    public void destroy() {

    }
}
