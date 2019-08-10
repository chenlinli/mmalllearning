package com.mmall.controller.portal;


import com.mmall.common.Const;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.service.IUserService;
import com.mmall.utils.CookieUtil;
import com.mmall.utils.JsonUtil;
import com.mmall.utils.RedisShardedPoolUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/user/springsession/")
public class UserSpringSessionController {

    @Autowired
    private IUserService iUserService;

    /**
     * 登录
     *
     * @param username
     * @param password
     * @param httpSession
     * @return
     */
    @ResponseBody//返回转为json
    @RequestMapping(value = "/login.do", method = RequestMethod.GET)
    public ServerResponse<User> login(String username, String password, HttpSession httpSession
                                      ) {
        //测试全局异常
     /*   int i=0;
        int j=777/i;*/
        ServerResponse<User> result = iUserService.login(username, password);
        if (result.isSuccess()) {
            httpSession.setAttribute(Const.CURRENT_USER,result.getData());
        }
        return result;
    }

    @ResponseBody
    @RequestMapping(value = "/logout.do", method = RequestMethod.GET)
    public ServerResponse<String> logout(HttpSession httpSession) {

        httpSession.removeAttribute(Const.CURRENT_USER);
        return ServerResponse.createBySuccess();
    }

    @ResponseBody
    @RequestMapping(value = "/get_user_info.do", method = RequestMethod.GET)
    public ServerResponse<User> getUserInfo(HttpSession httpSession,HttpServletRequest request) {
        User user = (User) httpSession.getAttribute(Const.CURRENT_USER);
        if (user != null) {
            return ServerResponse.createBySuccess(user);
        }
        return ServerResponse.createByErrorMessage("用户未登录，无法返回当前用户信息");

    }

}

