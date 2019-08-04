package com.mmall.controller.portal;

import com.mmall.common.Const;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.service.ICartService;
import com.mmall.service.IUserService;
import com.mmall.vo.CartVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/cart/")
public class CartController {

    @Autowired
    private IUserService iUserService;

    @Autowired
    private ICartService iCartService;

    @ResponseBody
    @RequestMapping(value = "add.do" ,method = RequestMethod.GET)
    public ServerResponse<CartVo> add(HttpSession httpSession, Integer count, Integer productId){
        if(productId==null || count==null){
            return ServerResponse.createByErrorCodeIllegaArg();
        }
        User user = (User)httpSession.getAttribute(Const.CURRENT_USER);
        if(user==null){
            return ServerResponse.createByErrorCodeNEEDLOGIN("用户未登录，请登录");
        }
        return iCartService.add(user.getId(),productId,count);

    }

    @ResponseBody
    @RequestMapping(value = "update.do" ,method = RequestMethod.GET)
    public ServerResponse<CartVo> update(HttpSession httpSession, Integer count, Integer productId){
        if(productId==null || count==null){
            return ServerResponse.createByErrorCodeIllegaArg();
        }
        User user = (User)httpSession.getAttribute(Const.CURRENT_USER);
        if(user==null){
            return ServerResponse.createByErrorCodeNEEDLOGIN("用户未登录，请登录");
        }
        return iCartService.update(user.getId(),productId,count);

    }

    @ResponseBody
    @RequestMapping(value = "delete_product.do" ,method = RequestMethod.GET)
    public ServerResponse<CartVo> deleteProduct(HttpSession httpSession, String productIds){

        User user = (User)httpSession.getAttribute(Const.CURRENT_USER);
        if(user==null){
            return ServerResponse.createByErrorCodeNEEDLOGIN("用户未登录，请登录");
        }
        return iCartService.deleteProduct(user.getId(),productIds);
    }

    @ResponseBody
    @RequestMapping(value = "list.do" ,method = RequestMethod.GET)
    public ServerResponse<CartVo> list(HttpSession httpSession){

        User user = (User)httpSession.getAttribute(Const.CURRENT_USER);
        if(user==null){
            return ServerResponse.createByErrorCodeNEEDLOGIN("用户未登录，请登录");
        }
        return iCartService.list(user.getId());
    }

    /**
     * 全选
     * @param httpSession
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "select_all.do" ,method = RequestMethod.GET)
    public ServerResponse<CartVo> selectAll(HttpSession httpSession){

        User user = (User)httpSession.getAttribute(Const.CURRENT_USER);
        if(user==null){
            return ServerResponse.createByErrorCodeNEEDLOGIN("用户未登录，请登录");
        }
        return iCartService.selectOrUnSelect(user.getId(),null,Const.Cart.CHECKED);
    }

    /**
     * 全反选
     * @param httpSession
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "un_select_all.do" ,method = RequestMethod.GET)
    public ServerResponse<CartVo> unSelectAll(HttpSession httpSession){

        User user = (User)httpSession.getAttribute(Const.CURRENT_USER);
        if(user==null){
            return ServerResponse.createByErrorCodeNEEDLOGIN("用户未登录，请登录");
        }
        return iCartService.selectOrUnSelect(user.getId(),null,Const.Cart.UN_CHECKED);
    }


    /**
     * 单独选
     * @param httpSession
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "select.do" ,method = RequestMethod.GET)
    public ServerResponse<CartVo> list(HttpSession httpSession,Integer productId){

        User user = (User)httpSession.getAttribute(Const.CURRENT_USER);
        if(user==null){
            return ServerResponse.createByErrorCodeNEEDLOGIN("用户未登录，请登录");
        }
        return iCartService.selectOrUnSelect(user.getId(),productId,Const.Cart.CHECKED);
    }

    /**
     * 单独反选
     * @param httpSession
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "un_select.do" ,method = RequestMethod.GET)
    public ServerResponse<CartVo> unSelect(HttpSession httpSession,Integer productId){

        User user = (User)httpSession.getAttribute(Const.CURRENT_USER);
        if(user==null){
            return ServerResponse.createByErrorCodeNEEDLOGIN("用户未登录，请登录");
        }
        return iCartService.selectOrUnSelect(user.getId(),productId,Const.Cart.UN_CHECKED);
    }

    /**
     * 查询用户的购物车产品数量，数量有10个，返回10个
     * @param httpSession
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "get_cart_product_count.do" ,method = RequestMethod.GET)
    public ServerResponse<Integer> getCartProductCount(HttpSession httpSession){

        User user = (User)httpSession.getAttribute(Const.CURRENT_USER);
        if(user==null){
            return ServerResponse.createBySuccess(0);
        }
        return iCartService.getCartProductCount(user.getId());
    }

}
