package com.mmall.service.impl;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.mmall.common.Const;
import com.mmall.common.ServerResponse;
import com.mmall.dao.CartMapper;
import com.mmall.dao.ProductMapper;
import com.mmall.pojo.Cart;
import com.mmall.pojo.Product;
import com.mmall.service.ICartService;
import com.mmall.service.ICategoryService;
import com.mmall.utils.BigDecimalUtil;
import com.mmall.utils.PropertiesUtil;
import com.mmall.vo.CartProductVo;
import com.mmall.vo.CartVo;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

@Service("iCartService")
public class CartServiceImpl implements ICartService {


    @Autowired
    private CartMapper cartMapper;

    @Autowired
    private ProductMapper productMapper;

    public ServerResponse<CartVo> add(Integer userId,Integer productId,Integer count){
        Cart cart = cartMapper.serlectCartByUserIdProductId(userId,productId);
        if(cart==null){
            //购物车里没有该产品
            Cart cartItem = new Cart();
            cartItem.setQuantity(count);
            cartItem.setProductId(productId);
            cartItem.setChecked(Const.Cart.CHECKED);
            cartItem.setUserId(userId);

            int rowCount = cartMapper.insert(cartItem);
        }else{
            count = cart.getQuantity()+count;
            cart.setQuantity(count);
            cartMapper.updateByPrimaryKeySelective(cart);
        }
        return list(userId);
    }

    /**
     * 封装某个用户的所有购物车条目Vo
     * @param userId
     * @return
     */
    private CartVo getCartVoLimit(Integer userId){
        CartVo cartVo = new CartVo();
        //用户的购物车
        List<Cart> cartList = cartMapper.selectCartByUserId(userId);
        List<CartProductVo> cartProductVoList = Lists.newArrayList();
        //购物车总价
        BigDecimal cartTotalPrice = new BigDecimal("0");
        if(CollectionUtils.isNotEmpty(cartList)){
            for (Cart c: cartList) {
                //构造cartProductVo
                CartProductVo cartProductVo = new CartProductVo();
                cartProductVo.setId(c.getId());
                cartProductVo.setUserId(c.getUserId());
                cartProductVo.setProductId(c.getProductId());
                Product product = productMapper.selectByPrimaryKey(c.getProductId());
                if(product!=null){
                    cartProductVo.setProductMainImage(product.getMainImage());
                    cartProductVo.setProductStatus(product.getStatus());
                    cartProductVo.setProductName(product.getName());
                    cartProductVo.setProductStock(product.getStock());
                    cartProductVo.setProductSubtitle(product.getSubtitle());
                    cartProductVo.setProductPrice(product.getPrice());

                    int buyLimitCount = 0;
                    if(product.getStock()>=c.getQuantity()){
                        //库存充足
                       buyLimitCount = c.getQuantity();
                        cartProductVo.setLimitQuantity(Const.Cart.LIMIT_NUM_SUCCESS);
                    }else{
                        buyLimitCount = product.getStock();
                        cartProductVo.setLimitQuantity(Const.Cart.LIMIT_NUM_FAIL);
                        //购物车更新有效库存
                        Cart cartForQuantity = new Cart();
                        cartForQuantity.setId(c.getId());
                        cartForQuantity.setQuantity(buyLimitCount);
                        cartMapper.updateByPrimaryKeySelective(cartForQuantity);
                    }
                    cartProductVo.setQuantity(buyLimitCount);

                    //计算总价
                    cartProductVo.setProductTotalPrice(
                            BigDecimalUtil.mul(cartProductVo.getQuantity().doubleValue(),cartProductVo.getProductPrice().doubleValue()));
                    cartProductVo.setProductChecked(c.getChecked());

                    if(c.getChecked()==Const.Cart.CHECKED){
                        //商品勾选，放入总价
                        cartTotalPrice = BigDecimalUtil.add(cartTotalPrice.doubleValue(),cartProductVo.getProductTotalPrice().doubleValue());
                    }
                    cartProductVoList.add(cartProductVo);
                }

            }

            cartVo.setCartTotalPrice(cartTotalPrice);
            cartVo.setCartProductVoList(cartProductVoList);
            cartVo.setAllChecked(getAllCheckedStatus(userId));
            cartVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix"));
        }

        return cartVo;

    }

    private boolean getAllCheckedStatus(Integer userId){
        if(userId==null){
            return false;
        }
        return cartMapper.selectCartProductCheckedStatusByUserId(userId)==0;
    }

    /**
     * 跟新购物车数量
     * @param userId
     * @param productId
     * @param count
     * @return
     */
    @Override
    public ServerResponse<CartVo> update(Integer userId, Integer productId, Integer count) {
        Cart cart = cartMapper.serlectCartByUserIdProductId(userId,productId);
        if(cart!=null){
            cart.setQuantity(count);
        }
        cartMapper.updateByPrimaryKeySelective(cart);
        return list(userId);
    }

    @Override
    public ServerResponse<CartVo> deleteProduct(Integer userId, String productIds) {
        List<String > productIdList = Splitter.on(",").splitToList(productIds);
        if(CollectionUtils.isEmpty(productIdList)){
            return ServerResponse.createByErrorCodeIllegaArg();
        }
        cartMapper.deleteByUserIdProductIds(userId,productIdList);
        return list(userId);
    }

    /**
     * 购物车列表不分页
     * @param userId
     * @return
     */
    @Override
    public ServerResponse<CartVo> list(Integer userId) {
        CartVo cartVo = getCartVoLimit(userId);
        return ServerResponse.createBySuccess(cartVo);
    }

    /**
     *
     * @param userId
     * @param productId
     * @param checked
     * @return
     */
    public ServerResponse<CartVo> selectOrUnSelect(Integer userId, Integer productId,Integer checked){
        cartMapper.checkOrUnCheckProduct(userId,productId ,checked);
        return list(userId);
    }

    @Override
    public ServerResponse<Integer> getCartProductCount(Integer userId) {
        if(userId==null) {
            return ServerResponse.createBySuccess(0);
        }
        return ServerResponse.createBySuccess(cartMapper.selectCartProductCount(userId));
    }
}
