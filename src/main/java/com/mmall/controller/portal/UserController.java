package com.mmall.controller.portal;


import com.mmall.common.Const;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.service.IUserService;
import com.mmall.utils.CookieUtil;
import com.mmall.utils.JsonUtil;
import com.mmall.utils.RedisPoolUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/user")
public class UserController {

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
    public ServerResponse<User> login(String username, String password, HttpSession httpSession,
                                      HttpServletResponse httpServletResponse,
                                      HttpServletRequest request) {
        ServerResponse<User> result = iUserService.login(username, password);
        if (result.isSuccess()) {
            //httpSession.setAttribute(Const.CURRENT_USER, result.getData());
            CookieUtil.writeLoginToken(httpServletResponse,httpSession.getId());
            CookieUtil.readLoginToken(request);
            CookieUtil.delLoginToken(request,httpServletResponse);
            RedisPoolUtil.setEx(httpSession.getId(), JsonUtil.obj2String(result.getData()),Const.RedisCacheExtime.REDIS_SESSION_TIME);
        }
        return result;
    }

    @ResponseBody
    @RequestMapping(value = "/logout.do", method = RequestMethod.POST)
    public ServerResponse<String> logout(HttpSession httpSession) {
        httpSession.removeAttribute(Const.CURRENT_USER);
        return ServerResponse.createBySuccess();
    }

    @ResponseBody
    @RequestMapping(value = "/register.do", method = RequestMethod.POST)
    public ServerResponse<String> register(User user) {
        return iUserService.register(user);
    }

    @ResponseBody
    @RequestMapping(value = "/check_valid.do", method = RequestMethod.POST)
    public ServerResponse<String> checkValid(String str, String type) {
        return iUserService.checkValid(str, type);
    }

    @ResponseBody
    @RequestMapping(value = "/get_user_info.do", method = RequestMethod.POST)
    public ServerResponse<User> getUserInfo(HttpSession httpSession) {
        User user = (User) httpSession.getAttribute(Const.CURRENT_USER);
        if (user != null) {
            return ServerResponse.createBySuccess(user);
        }
        return ServerResponse.createByErrorMessage("用户未登录，无法返回当前用户信息");

    }

    @ResponseBody
    @RequestMapping(value = "/forget_get_question.do", method = RequestMethod.POST)
    public ServerResponse<String> forgetGetQuestion(String username) {
        return iUserService.selectQuestion(username);
    }

    @ResponseBody
    @RequestMapping(value = "/forget_check_answer.do", method = RequestMethod.POST)
    public ServerResponse<String> forgetCheckAnswer(String username, String question, String answer) {
        //缓存有效期
        return iUserService.checkAnswer(username, question, answer);
    }

    @ResponseBody
    @RequestMapping(value = "/forget_reset_password.do", method = RequestMethod.POST)
    public ServerResponse<String> forgetResetPassword(String username, String passwordNew, String forgetToken){
        return iUserService.forgetResetPassword(username,passwordNew,forgetToken);
    }

    @ResponseBody
    @RequestMapping(value = "/reset_password.do", method = RequestMethod.POST)
    public ServerResponse<String> resetPassword(String passwordOld, String passwordNew,HttpSession httpSession){
        User user = (User) httpSession.getAttribute(Const.CURRENT_USER);
        if(user==null){
            return ServerResponse.createByErrorCodeNEEDLOGIN("用户未登录");
        }
        return iUserService.resetPassword(passwordOld,passwordNew,user);
    }


    @ResponseBody
    @RequestMapping(value = "/update_information.do", method = RequestMethod.POST)
    public ServerResponse<User> updateInformation(HttpSession httpSession,User user){
        User currentUser = (User) httpSession.getAttribute(Const.CURRENT_USER);
        if(currentUser==null){
            return ServerResponse.createByErrorMessage("用户未登录");
        }
        user.setId(currentUser.getId());
        user.setUsername(currentUser.getUsername());
        ServerResponse<User> response = iUserService.updateInformation(user);
        if(response.isSuccess()){
            response.getData().setUsername(currentUser.getUsername());
            httpSession.setAttribute(Const.CURRENT_USER,response.getData());
        }
        return response;
    }

    @ResponseBody
    @RequestMapping(value = "/get_information.do", method = RequestMethod.POST)
    public ServerResponse<User> getInformation(HttpSession httpSession){
        User currentUser = (User) httpSession.getAttribute(Const.CURRENT_USER);
        if(currentUser==null){
            return ServerResponse.createByErrorCodeNEEDLOGIN("用户未登录,无法获取当前用户信息,status=10,强制登录");
        }
        return iUserService.getInfomation(currentUser.getId());
    }
}

