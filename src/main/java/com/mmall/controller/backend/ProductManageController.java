package com.mmall.controller.backend;

import com.google.common.collect.Maps;
import com.mmall.common.Const;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.Product;
import com.mmall.pojo.User;
import com.mmall.service.IFileService;
import com.mmall.service.IProductService;
import com.mmall.service.IUserService;
import com.mmall.utils.PropertiesUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/manage/product/")
public class ProductManageController {

    @Autowired
    private IUserService iUserService;

    @Autowired
    private IProductService iProductService;

    @Autowired
    private IFileService iFileService;


    /**
     *更新或新增商品
     * @param httpSession
     * @param product
     * @return
     */

    @ResponseBody
    @RequestMapping(value = "save.do",method = RequestMethod.GET)
    public ServerResponse productSave(HttpSession httpSession,Product product){
        User user = (User)httpSession.getAttribute(Const.CURRENT_USER);
        if(user==null){
            return ServerResponse.createByErrorCodeNEEDLOGIN("用户未登录，请登录");
        }
        //校验是否管理员
        if(iUserService.checkAdminRole(user).isSuccess()) {
            //
            return iProductService.saveOrUpdateProduct(product);

        }else{
            return ServerResponse.createByErrorMessage("无权限操作，需要管理员权限");
        }

    }


    /**
     * 产品上下架
     * @param httpSession
     * @param productId
     * @param status
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "set_sell_status.do",method = RequestMethod.GET)
    public ServerResponse setSaleStatus(HttpSession httpSession,Integer productId,Integer status){
        User user = (User)httpSession.getAttribute(Const.CURRENT_USER);
        if(user==null){
            return ServerResponse.createByErrorCodeNEEDLOGIN("用户未登录，请登录");
        }
        //校验是否管理员
        if(iUserService.checkAdminRole(user).isSuccess()) {
           return iProductService.setSaleStatus(productId,status);

        }else{
            return ServerResponse.createByErrorMessage("无权限操作，需要管理员权限");
        }

    }


    @ResponseBody
    @RequestMapping(value = "detail.do",method = RequestMethod.POST)
    public ServerResponse getDetail(HttpSession httpSession,Integer productId){
        User user = (User)httpSession.getAttribute(Const.CURRENT_USER);
        if(user==null){
            return ServerResponse.createByErrorCodeNEEDLOGIN("用户未登录，请登录");
        }
        //校验是否管理员
        if(iUserService.checkAdminRole(user).isSuccess()) {
            return iProductService.manageProductDetail(productId);

        }else{
            return ServerResponse.createByErrorMessage("无权限操作，需要管理员权限");
        }

    }

    /**
     * 产品列表
     * @param httpSession
     * @param pageNum
     * @param pageSize
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "list.do",method = RequestMethod.POST)
    public ServerResponse getList(HttpSession httpSession,
                                  @RequestParam(value = "pageNum",defaultValue = "1") int pageNum,
                                  @RequestParam(value = "pageSize" ,defaultValue = "10") int pageSize){
        User user = (User)httpSession.getAttribute(Const.CURRENT_USER);
        if(user==null){
            return ServerResponse.createByErrorCodeNEEDLOGIN("用户未登录，请登录");
        }
        //校验是否管理员
        if(iUserService.checkAdminRole(user).isSuccess()) {
            return iProductService.getProductList(pageNum,pageSize);
        }else{
            return ServerResponse.createByErrorMessage("无权限操作，需要管理员权限");
        }
    }

    /**
     * 产品名称、id搜索商品，分页显示
     * @param httpSession
     * @param productName
     * @param productId
     * @param pageNum
     * @param pageSize
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "search.do",method = RequestMethod.POST)
    public ServerResponse productSearch(HttpSession httpSession,
                                  String productName,Integer productId,
                                  @RequestParam(value = "pageNum",defaultValue = "1") int pageNum,
                                  @RequestParam(value = "pageSize" ,defaultValue = "10") int pageSize){
        User user = (User)httpSession.getAttribute(Const.CURRENT_USER);
        if(user==null){
            return ServerResponse.createByErrorCodeNEEDLOGIN("用户未登录，请登录");
        }
        //校验是否管理员
        if(iUserService.checkAdminRole(user).isSuccess()) {
            return iProductService.searchProduct(productName,productId,pageNum,pageSize);
        }else{
            return ServerResponse.createByErrorMessage("无权限操作，需要管理员权限");
        }
    }

    /**
     * 文件上传
     * @param multipartFile
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "upload.do",method = RequestMethod.POST)
    public ServerResponse upload(HttpSession httpSession,
                                 @RequestParam(value = "upload_file",required = false) MultipartFile multipartFile,
                                 HttpServletRequest request){

        User user = (User)httpSession.getAttribute(Const.CURRENT_USER);
        if(user==null){
            return ServerResponse.createByErrorCodeNEEDLOGIN("用户未登录，请登录");
        }
        //校验是否管理员
        if(iUserService.checkAdminRole(user).isSuccess()) {
            String path = request.getSession().getServletContext().getRealPath("upload");
            String upload = iFileService.upload(multipartFile, path);//返回文件名
            String url = PropertiesUtil.getProperty("ftp.server.http.prefix")+upload;

            HashMap<Object, Object> map = Maps.newHashMap();
            map.put("uri",upload);
            map.put("url",url);
            return ServerResponse.createBySuccess(map);

        }else{
            return ServerResponse.createByErrorMessage("无权限操作，需要管理员权限");
        }

    }


    /**
     * 文件上传
     * @param multipartFile
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "richtext_img_upload.do",method = RequestMethod.POST)
    public Map richtextImgUpload(HttpServletResponse response,HttpSession httpSession, @RequestParam(value = "upload_file",required = false) MultipartFile multipartFile, HttpServletRequest request){

        Map resultMap = Maps.newHashMap();
        User user = (User)httpSession.getAttribute(Const.CURRENT_USER);
        if(user==null){
            resultMap.put("success",false);
            resultMap.put("msg","请登录管理员");
            return resultMap;
        }
        //富文本有返回值有要求：simditor富文本的api
        //校验是否管理员
        if(iUserService.checkAdminRole(user).isSuccess()) {
            String path = request.getSession().getServletContext().getRealPath("upload");
            String upload = iFileService.upload(multipartFile, path);//返回文件名
            if(StringUtils.isBlank(upload)){
                resultMap.put("success",false);
                resultMap.put("msg","上传失败");
                return resultMap;
            }
            String url = PropertiesUtil.getProperty("ftp.server.http.prefix")+upload;
            resultMap.put("success",true);
            resultMap.put("msg","上次成功");
            resultMap.put("file_path",url);
            response.addHeader("Access-Control-Allow-Headers","X-File-Name");
            return resultMap;

        }else{
            resultMap.put("success",false);
            resultMap.put("msg","无权限操作");
            return resultMap;
        }

    }




}
