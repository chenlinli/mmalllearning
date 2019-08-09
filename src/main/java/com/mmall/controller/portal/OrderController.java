package com.mmall.controller.portal;

import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.demo.trade.config.Configs;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Maps;
import com.mmall.common.Const;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.service.IOrderService;
import com.mmall.utils.CookieUtil;
import com.mmall.utils.JsonUtil;
import com.mmall.utils.RedisShardedPoolUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import sun.awt.HeadlessToolkit;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Iterator;
import java.util.Map;

@Controller
@RequestMapping("/order/")
public class OrderController {
    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);

    @Autowired
    private IOrderService iOrderService;


    @ResponseBody
    @RequestMapping("create.do")
    public ServerResponse create(HttpServletRequest request,Integer shippingId){
        String loginToken = CookieUtil.readLoginToken(request);
        if(StringUtils.isEmpty(loginToken)){
            return ServerResponse.createByErrorMessage("用户未登录，无法返回当前用户信息");
        }
        String userJsonStr = RedisShardedPoolUtil.get(loginToken);
        User user = JsonUtil.string2Obj(userJsonStr, User.class);
        if(user==null){
            return ServerResponse.createByErrorCodeNEEDLOGIN("用户未登录，请登录");
        }

        return iOrderService.createOrder(user.getId(),shippingId);
    }


    @ResponseBody
    @RequestMapping("cancel.do")
    public ServerResponse cancle(HttpServletRequest request,long orderNo){
        String loginToken = CookieUtil.readLoginToken(request);
        if(StringUtils.isEmpty(loginToken)){
            return ServerResponse.createByErrorMessage("用户未登录，无法返回当前用户信息");
        }
        String userJsonStr = RedisShardedPoolUtil.get(loginToken);
        User user = JsonUtil.string2Obj(userJsonStr, User.class);
        if(user==null){
            return ServerResponse.createByErrorCodeNEEDLOGIN("用户未登录，请登录");
        }

        return iOrderService.cancel(user.getId(),orderNo);
    }

    /**
     * 购物车已经选择的产品
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping("get_order_cart_product.do")
    public ServerResponse getOrderCartProduct(HttpServletRequest request){
        String loginToken = CookieUtil.readLoginToken(request);
        if(StringUtils.isEmpty(loginToken)){
            return ServerResponse.createByErrorMessage("用户未登录，无法返回当前用户信息");
        }
        String userJsonStr = RedisShardedPoolUtil.get(loginToken);
        User user = JsonUtil.string2Obj(userJsonStr, User.class);
        if(user==null){
            return ServerResponse.createByErrorCodeNEEDLOGIN("用户未登录，请登录");
        }
        return iOrderService.getOrderCartProduct(user.getId());
    }

    /**
     * 订单详情
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping("detail.do")
    public ServerResponse detail(HttpServletRequest request,long orderNo){
        String loginToken = CookieUtil.readLoginToken(request);
        if(StringUtils.isEmpty(loginToken)){
            return ServerResponse.createByErrorMessage("用户未登录，无法返回当前用户信息");
        }
        String userJsonStr = RedisShardedPoolUtil.get(loginToken);
        User user = JsonUtil.string2Obj(userJsonStr, User.class);
        if(user==null){
            return ServerResponse.createByErrorCodeNEEDLOGIN("用户未登录，请登录");
        }
        return iOrderService.getOrderDetail(user.getId(),orderNo);
    }

    @ResponseBody
    @RequestMapping("list.do")
    public ServerResponse<PageInfo> list(HttpServletRequest request,
                                         @RequestParam(value = "pageSize",defaultValue = "10") int pageSize,
                                         @RequestParam(value = "pageNum",defaultValue = "1") int pageNum){
        String loginToken = CookieUtil.readLoginToken(request);
        if(StringUtils.isEmpty(loginToken)){
            return ServerResponse.createByErrorMessage("用户未登录，无法返回当前用户信息");
        }
        String userJsonStr = RedisShardedPoolUtil.get(loginToken);
        User user = JsonUtil.string2Obj(userJsonStr, User.class);
        if(user==null){
            return ServerResponse.createByErrorCodeNEEDLOGIN("用户未登录，请登录");
        }
        return iOrderService.list(user.getId(),pageSize,pageNum);
    }


    @ResponseBody
    @RequestMapping("pay.do")
    public ServerResponse pay(long orderNo, HttpServletRequest request){
        String loginToken = CookieUtil.readLoginToken(request);
        if(StringUtils.isEmpty(loginToken)){
            return ServerResponse.createByErrorMessage("用户未登录，无法返回当前用户信息");
        }
        String userJsonStr = RedisShardedPoolUtil.get(loginToken);
        User user = JsonUtil.string2Obj(userJsonStr, User.class);
        if(user==null){
            return ServerResponse.createByErrorCodeNEEDLOGIN("用户未登录，请登录");
        }
        String path  = request.getSession().getServletContext().getRealPath("upload");
        return iOrderService.pay(user.getId(),path,orderNo);

    }

    @RequestMapping("alipay_callback.do")
    @ResponseBody
    public Object aliPayCallBack(HttpServletRequest request){
        Map<String,String> params = Maps.newHashMap();
        Map<String, String[]> requestParams = request.getParameterMap();
        //重新组装参数map
        for (Iterator iter = requestParams.keySet().iterator();iter.hasNext(); ) {
            String name = (String) iter.next();
            String[] values = requestParams.get(name);
            String valurStr = "";
            for(int i=0;i<values.length;i++){
                //","逗号拼接values
                valurStr =(i==values.length-1)?valurStr+values[i]:valurStr+values[i]+",";
            }
            //存入自己的map
            params.put(name,valurStr);
        }
        //https://docs.open.alipay.com/194/103296:异步回调文档
        logger.info("支付宝回调，sign:{},trade_status:{},参数:{}",
                params.get("sign"),params.get("trade_status"),params.toString());

        //非常重要：验证回调是不是支付宝发的，避免重复回调
        params.remove("sign_type");
        try {
            boolean aliupayRSACheckV2 = AlipaySignature.rsaCheckV2(params, Configs.getAlipayPublicKey(),"utf-8",Configs.getSignType());
            if(!aliupayRSACheckV2){
                return ServerResponse.createByErrorMessage("非法请求，验证不通过，找网警了");
            }
            //业务逻辑


        } catch (AlipayApiException e) {
            logger.error("支付宝验证回调异常",e);
        }
        //验证各种数据
        /**
         * 商户需要验证该通知数据中的
         * out_trade_no 是否为商户系统中创建的订单号，
         * 并判断 total_amount 是否确实为该订单的实际金额（即商户订单创建时的金额），
         * 同时需要校验通知中的 seller_id（或者seller_email) 是否为 out_trade_no 这笔单据的对应的操作方（有的时候，一个商户可能有多个 seller_id/seller_email），
         * 上述有任何一个验证不通过，则表明本次通知是异常通知，务必忽略。
         * 在上述验证通过后商户必须根据支付宝不同类型的业务通知，正确的进行不同的业务处理，并且过滤重复的通知结果数据。在支付宝的业务通知中，只有交易通知状态为 TRADE_SUCCESS 或 TRADE_FINISHED 时，支付宝才会认定为买家付款成功。
         */
        ServerResponse serverResponse = iOrderService.aliCallback(params);
        if(serverResponse.isSuccess()){
            return Const.AlipayCallback.RESPONSE_SCCESS;
        }
        return Const.AlipayCallback.RESPONSE_FAILED;
    }

    /**
     * 前台轮询查看是否付款成功，
     */
    @ResponseBody
    @RequestMapping("query_order_pay_status.do")
    public ServerResponse<Boolean> queryOrderPayStatus(HttpServletRequest request,long orderNo){
        String loginToken = CookieUtil.readLoginToken(request);
        if(StringUtils.isEmpty(loginToken)){
            return ServerResponse.createByErrorMessage("用户未登录，无法返回当前用户信息");
        }
        String userJsonStr = RedisShardedPoolUtil.get(loginToken);
        User user = JsonUtil.string2Obj(userJsonStr, User.class);
        if(user==null){
            return ServerResponse.createByErrorCodeNEEDLOGIN("用户未登录，请登录");
        }

        if(iOrderService.queryOrderPayStatus(user.getId(),orderNo).isSuccess()){
            return ServerResponse.createBySuccess(true);
        }
        return ServerResponse.createBySuccess(false);
    }
}
