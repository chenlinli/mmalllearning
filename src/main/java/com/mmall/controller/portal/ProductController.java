package com.mmall.controller.portal;

import com.github.pagehelper.PageInfo;
import com.mmall.common.ServerResponse;
import com.mmall.service.IProductService;
import com.mmall.vo.ProductDetailVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/product/")
public class ProductController {

    @Autowired
    private IProductService iProductService;

    @RequestMapping(value = "detail.do",method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<ProductDetailVo> detail(Integer productId){
        return iProductService.getProductDetail(productId);
    }

    @RequestMapping(value = "/{productId}",method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<ProductDetailVo> detailRESTFul(@PathVariable Integer productId){
        return iProductService.getProductDetail(productId);
    }

    @RequestMapping(value = "list.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<PageInfo> list(@RequestParam(value = "keyword",required = false)String keyword ,
                                         @RequestParam(value = "categoryId",required = false) Integer categoryId,
                                         @RequestParam(value = "pageNum" ,defaultValue = "1") int pageNum,
                                         @RequestParam(value = "pageSize",defaultValue = "10") int pageSize,
                                         @RequestParam(value = "orderBy",defaultValue = "") String orderBy){

        return iProductService.getProductByKeywordCategoryId(keyword,categoryId,pageNum,pageSize,orderBy);

    }

    @RequestMapping(value = "/{keyword}/{categoryId}/{pageNum}/{pageSize}/{orderBy}",method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<PageInfo> listRESTFul(@PathVariable(value = "keyword")String keyword ,
                                         @PathVariable(value = "categoryId") Integer categoryId,
                                         @PathVariable(value = "pageNum" ) Integer pageNum,
                                         @PathVariable(value = "pageSize") Integer pageSize,
                                         @PathVariable(value = "orderBy") String orderBy){
        if(pageNum==null){
            pageNum=1;
        }
        if (pageSize==null){
            pageSize=10;
        }
        if(StringUtils.isBlank(orderBy)){
            orderBy="price_asc";
        }

        return iProductService.getProductByKeywordCategoryId(keyword,categoryId,pageNum,pageSize,orderBy);

    }
    @RequestMapping(value = "/{categoryId}/{pageNum}/{pageSize}/{orderBy}",method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<PageInfo> listRESTFulBadCase(
                                                @PathVariable(value = "categoryId") Integer categoryId,
                                                @PathVariable(value = "pageNum" ) Integer pageNum,
                                                @PathVariable(value = "pageSize") Integer pageSize,
                                                @PathVariable(value = "orderBy") String orderBy){
        if(pageNum==null){
            pageNum=1;
        }
        if (pageSize==null){
            pageSize=10;
        }
        if(StringUtils.isBlank(orderBy)){
            orderBy="price_asc";
        }

        return iProductService.getProductByKeywordCategoryId(null,categoryId,pageNum,pageSize,orderBy);

    }
    @RequestMapping(value = "/{keyword}/{pageNum}/{pageSize}/{orderBy}",method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<PageInfo> listRESTFulBadcase(@PathVariable(value = "keyword")String keyword ,
                                                @PathVariable(value = "pageNum" ) Integer pageNum,
                                                @PathVariable(value = "pageSize") Integer pageSize,
                                                @PathVariable(value = "orderBy") String orderBy){
        if(pageNum==null){
            pageNum=1;
        }
        if (pageSize==null){
            pageSize=10;
        }
        if(StringUtils.isBlank(orderBy)){
            orderBy="price_asc";
        }

        return iProductService.getProductByKeywordCategoryId(keyword,null,pageNum,pageSize,orderBy);

    }

    @RequestMapping(value = "/keyword /{keyword}/{pageNum}/{pageSize}/{orderBy}",method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<PageInfo> listRESTFul(@PathVariable(value = "keyword")String keyword ,
                                                @PathVariable(value = "pageNum" ) Integer pageNum,
                                                @PathVariable(value = "pageSize") Integer pageSize,
                                                @PathVariable(value = "orderBy") String orderBy){
        if(pageNum==null){
            pageNum=1;
        }
        if (pageSize==null){
            pageSize=10;
        }
        if(StringUtils.isBlank(orderBy)){
            orderBy="price_asc";
        }

        return iProductService.getProductByKeywordCategoryId(keyword,null,pageNum,pageSize,orderBy);

    }
    @RequestMapping(value = "/category/{categoryId}/{pageNum}/{pageSize}/{orderBy}",method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<PageInfo> listRESTFul(
            @PathVariable(value = "categoryId") Integer categoryId,
            @PathVariable(value = "pageNum" ) Integer pageNum,
            @PathVariable(value = "pageSize") Integer pageSize,
            @PathVariable(value = "orderBy") String orderBy){
        if(pageNum==null){
            pageNum=1;
        }
        if (pageSize==null){
            pageSize=10;
        }
        if(StringUtils.isBlank(orderBy)){
            orderBy="price_asc";
        }

        return iProductService.getProductByKeywordCategoryId(null,categoryId,pageNum,pageSize,orderBy);

    }
}
