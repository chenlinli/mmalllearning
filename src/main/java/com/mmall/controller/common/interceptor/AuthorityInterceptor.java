package com.mmall.controller.common.interceptor;

import com.alipay.demo.trade.config.Configs;
import com.google.common.collect.Maps;
import com.mmall.common.Const;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.utils.CookieUtil;
import com.mmall.utils.JsonUtil;
import com.mmall.utils.RedisShardedPoolUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;

@Slf4j
public class AuthorityInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //进入方法前拦截
        log.info("preHandle");
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        String methodName = handlerMethod.getMethod().getName();
        String className = handlerMethod.getBean().getClass().getSimpleName();
        //打印参数
        StringBuffer requestParamterBuffer = new StringBuffer();
        Map<String, String[]> parameterMap = request.getParameterMap();
        Iterator it = parameterMap.entrySet().iterator();
        while (it.hasNext()){
            Map.Entry entry = (Map.Entry) it.next();
            String key = (String) entry.getKey();
            String value = StringUtils.EMPTY;
            //request的parammap返回String[]
            Object obj =  entry.getValue();
            if(obj instanceof String[]){
                String[] strs = (String[]) obj;
                value = Arrays.toString(strs);
            }
            requestParamterBuffer.append(key).append("=").append(value);
        }
        if(StringUtils.equals(className,"UserManageController") && StringUtils.equals(methodName,"login")){
            //不打印参数，日志泄露密码
            log.info("拦截到请求：className:{},methodName:{}",className,methodName);
            return true;
        }

        log.info("拦截到请求：className:{},methodName:{},param:{}",className,methodName,requestParamterBuffer.toString());
        //权限校验
        User user = null;
        String loginToken = CookieUtil.readLoginToken(request);
        if(StringUtils.isNotEmpty(loginToken)){
            String userJsonStr = RedisShardedPoolUtil.get(loginToken);
            user = JsonUtil.string2Obj(userJsonStr, User.class);
        }

         if(user==null || (user.getRole().intValue()!= Const.Role.ROLL_ADMIN)){
            //false 不会调用controller的方法
            //response托管到拦截器
            //要reset，否则人报getWriter() has already been called for this response 异常
            response.reset();
            //设置编码，否则乱码
            response.setCharacterEncoding("utf-8");
            //设置返回值类型，全部是json
            response.setContentType("application/json;charset=UTF-8");

            PrintWriter out = response.getWriter();

            //富文本特殊返回处理，是否登录和权限校验
            if(user==null){
                if(StringUtils.equals(className,"ProductManageController") && StringUtils.equals(methodName,"richtextImgUpload")){
                    Map resultMap = Maps.newHashMap();
                    resultMap.put("success",false);
                    resultMap.put("msg","请登录管理员");
                    out.print(JsonUtil.obj2String(resultMap));
                    return true;
                }else {
                    out.print(JsonUtil.obj2String(ServerResponse.createByErrorCodeNEEDLOGIN("拦截器：用户未登录，请登录")));
                }
            }else{
                //user!=null 不是管理员
                if(StringUtils.equals(className,"ProductManageController") && StringUtils.equals(methodName,"richtextImgUpload")){
                    Map resultMap = Maps.newHashMap();
                    resultMap.put("success",false);
                    resultMap.put("msg","无权限操作");
                    out.print(JsonUtil.obj2String(resultMap));
                    return true;
                }else {
                    out.print(JsonUtil.obj2String(ServerResponse.createByErrorCodeNEEDLOGIN("拦截器：用户无权限操作")));
                }
            }
            out.flush();
            out.close();

            return false;
        }

        return true;
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {
        log.info("postHandle");
    }


    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {
        log.info("afterCompletion");
    }
}
