package com.mmall.controller.portal;

import com.github.pagehelper.PageInfo;
import com.mmall.common.Const;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.Shipping;
import com.mmall.pojo.User;
import com.mmall.service.IShippingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/shipping")
public class ShippingController {

    @Autowired
    private IShippingService iShippingService;

    /**
     * 添加地址
     * @param httpSession
     * @param shipping
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "add.do",method = RequestMethod.GET)
    public ServerResponse add(HttpSession httpSession, Shipping shipping){
        User user = (User)httpSession.getAttribute(Const.CURRENT_USER);
        if(user==null){
            return ServerResponse.createByErrorCodeNEEDLOGIN("用户未登录，请登录");
        }
        return iShippingService.add(user.getId(),shipping);
    }

    @ResponseBody
    @RequestMapping(value = "del.do",method = RequestMethod.GET)
    public ServerResponse del(HttpSession httpSession,Integer shippingId){
        User user = (User)httpSession.getAttribute(Const.CURRENT_USER);
        if(user==null){
            return ServerResponse.createByErrorCodeNEEDLOGIN("用户未登录，请登录");
        }
        return iShippingService.del(user.getId(),shippingId);
    }

    @ResponseBody
    @RequestMapping(value = "update.do",method = RequestMethod.GET)
    public ServerResponse update(HttpSession httpSession,Shipping shipping){
        User user = (User)httpSession.getAttribute(Const.CURRENT_USER);
        if(user==null){
            return ServerResponse.createByErrorCodeNEEDLOGIN("用户未登录，请登录");
        }
        return iShippingService.update(user.getId(),shipping);
    }


    @ResponseBody
    @RequestMapping(value = "select.do",method = RequestMethod.GET)
    public ServerResponse<Shipping> select(HttpSession httpSession,Integer shippingId){
        User user = (User)httpSession.getAttribute(Const.CURRENT_USER);
        if(user==null){
            return ServerResponse.createByErrorCodeNEEDLOGIN("用户未登录，请登录");
        }
        return iShippingService.select(user.getId(), shippingId);
    }

    @ResponseBody
    @RequestMapping(value = "list.do",method = RequestMethod.GET)
    public ServerResponse<PageInfo> list(HttpSession httpSession,
                                         @RequestParam(value = "pageNum" ,defaultValue = "1") int pageNum,
                                         @RequestParam(value = "pageSize",defaultValue = "10") int pageSize){
        User user = (User)httpSession.getAttribute(Const.CURRENT_USER);
        if(user==null){
            return ServerResponse.createByErrorCodeNEEDLOGIN("用户未登录，请登录");
        }
        return iShippingService.list(user.getId(),pageNum,pageSize);
    }


}
