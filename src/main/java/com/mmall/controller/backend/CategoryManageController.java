package com.mmall.controller.backend;

import com.mmall.common.Const;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.Category;
import com.mmall.pojo.User;
import com.mmall.service.ICategoryService;
import com.mmall.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
@RequestMapping("manage/category")
public class CategoryManageController {

    @Autowired
    private IUserService iUserService;

    @Autowired
    private ICategoryService iCategoryService;

    @ResponseBody
    @RequestMapping(value = "add_category.do",method = RequestMethod.POST)
    public ServerResponse<String> addCategory(HttpSession httpSession,String categoryName,
                                              @RequestParam(value = "parentId",defaultValue = "0") int parentId){
        User user = (User)httpSession.getAttribute(Const.CURRENT_USER);
        if(user==null){
            return ServerResponse.createByErrorCodeNEEDLOGIN("用户未登录，请登录");
        }
        //校验是否管理员
        if(iUserService.checkAdminRole(user).isSuccess()){
            //管理员
            return iCategoryService.addCategory(categoryName,parentId);

        }else{
            return ServerResponse.createByErrorMessage("无权限操作，需要管理员权限");
        }

    }


    @ResponseBody
    @RequestMapping(value = "set_category_name.do",method = RequestMethod.POST)
    public ServerResponse<String> setCategoryName(HttpSession httpSession,Integer categoryId,String categoryName){
        User user = (User)httpSession.getAttribute(Const.CURRENT_USER);
        if(user==null){
            return ServerResponse.createByErrorCodeNEEDLOGIN("用户未登录，请登录");
        }
        //校验是否管理员
        if(iUserService.checkAdminRole(user).isSuccess()) {
            //管理员
            //更新
            return iCategoryService.setCategoryName(categoryId,categoryName);

        }else{
            return ServerResponse.createByErrorMessage("无权限操作，需要管理员权限");
        }
    }

    @ResponseBody
    @RequestMapping(value = "get_category.do",method = RequestMethod.POST)
    public ServerResponse<List<Category>> getChildrenParallelCategory(HttpSession httpSession,
                                                           @RequestParam(value = "categoryId",defaultValue = "0") Integer categoryId){
        User user = (User)httpSession.getAttribute(Const.CURRENT_USER);
        if(user==null){
            return ServerResponse.createByErrorCodeNEEDLOGIN("用户未登录，请登录");
        }
        //校验是否管理员
        if(iUserService.checkAdminRole(user).isSuccess()) {
            //管理员
            //查询平级子节点
            return iCategoryService.getChildrenParallelCategory(categoryId);

        }else{
            return ServerResponse.createByErrorMessage("无权限操作，需要管理员权限");
        }

    }

    /**
     *   //查询当前结点和递归子节点的id
     * @param httpSession
     * @param categoryId
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "get_deep_category.do",method = RequestMethod.POST)
    public ServerResponse<List<Integer>> getCategoryAndDeepChildrenCategory(HttpSession httpSession,
                                                                      @RequestParam(value = "categoryId",defaultValue = "0") Integer categoryId){
        User user = (User)httpSession.getAttribute(Const.CURRENT_USER);
        if(user==null){
            return ServerResponse.createByErrorCodeNEEDLOGIN("用户未登录，请登录");
        }
        //校验是否管理员
        if(iUserService.checkAdminRole(user).isSuccess()) {
            //查询当前结点和递归子节点的id
            return iCategoryService.getCategoryAndChildrenById(categoryId);

        }else{
            return ServerResponse.createByErrorMessage("无权限操作，需要管理员权限");
        }

    }

}

