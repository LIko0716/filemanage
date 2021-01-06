package com.hzvtc1063.filemanage.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hzvtc1063.filemanage.Result.ResponseBean;
import com.hzvtc1063.filemanage.dto.*;
import com.hzvtc1063.filemanage.entity.User;
import com.hzvtc1063.filemanage.enums.UserEnum;
import com.hzvtc1063.filemanage.exception.UserException;
import com.hzvtc1063.filemanage.service.PermissionService;
import com.hzvtc1063.filemanage.service.UserService;
import com.hzvtc1063.filemanage.vo.PermissionVO;
import com.hzvtc1063.filemanage.vo.UserVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.models.auth.In;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author 1063
 * @since 2020-12-09
 */
@Api("这是用户控制类")
@RestController
@RequestMapping("/api/user")
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private PermissionService permissionService;

    @ApiOperation("用户注册")
    @PostMapping("/registry")
    public ResponseBean registry(@Validated @RequestBody User user, BindingResult bindingResult){
        log.info("用户注册");
        if (bindingResult.hasErrors()){
            log.info("前端用户信息有误");
            return ResponseBean.error(400,bindingResult.getAllErrors().get(0).getDefaultMessage());
        }
        QueryWrapper queryWrapper =new QueryWrapper();
        queryWrapper.eq("user_name",user.getUserName());
        User myUser = userService.getOne(queryWrapper);
        //用户名不能重名
        if (myUser!=null){
            log.info("用户名重复");
            throw  new UserException(UserEnum.USERNAME_EXIST);

        }
        boolean result =userService.insert(user);

        if (result){
            return ResponseBean.success(null,"注册成功",300);
        }else{
            return ResponseBean.error(400,"注册失败");
        }
    }

    @PostMapping("/deleteUser")
    public ResponseBean deleteUser(@RequestBody DeleteUserDto deleteUserDto){
        userService.deleteUser(deleteUserDto);
        return ResponseBean.success(null,"删除成功");
    }

    @PostMapping("/updateUser")
    public ResponseBean updateUser(@Valid @RequestBody UpUserDto upUserDto,BindingResult bindingResult){
        if (bindingResult.hasErrors()){
            log.info("前端用户信息有误");
            return ResponseBean.error(400,bindingResult.getAllErrors().get(0).getDefaultMessage());
        }
        if (!"管理员".equals(upUserDto.getRole())&&!"user".equals(upUserDto.getRole())){
            return ResponseBean.error(400,"只能输入‘管理员’或者‘user’");
        }
        userService.updateUser(upUserDto);
        return ResponseBean.success(null,"修改成功");
    }

    @ApiOperation("退出登录")
    @PostMapping("/logout")
    public ResponseBean logout(@RequestHeader("token")String token) throws IOException, ClassNotFoundException {
        String userName=userService.logout(token);
        return ResponseBean.success(userName,"用户"+userName+"退出成功",204);
    }

    @ApiOperation("用户登录")
    @PostMapping("/login")
    public ResponseBean login(@Valid @RequestBody User user,BindingResult bindingResult) throws UnsupportedEncodingException {
        if (bindingResult.hasErrors()){
            log.info("登录用户前端信息有误");
            return ResponseBean.error(400,bindingResult.getAllErrors().get(0).getDefaultMessage());
        }
        String token=userService.verify(user);
        return ResponseBean.success(token,"登录成功");
    }

    @PostMapping("/admin/login")
    public ResponseBean adminLogin(@Valid @RequestBody User user,BindingResult bindingResult) throws UnsupportedEncodingException {
        if (bindingResult.hasErrors()){
            log.info("登录用户前端信息有误");
            log.info(bindingResult.getAllErrors().get(0).getDefaultMessage());
            return ResponseBean.error(400,bindingResult.getAllErrors().get(0).getDefaultMessage());
        }
        String token=userService.verifyAdmin(user);
        return ResponseBean.success(token,"登录成功");
    }

    @ApiOperation("验证权限")
    @GetMapping("/verifyPermission")
    public ResponseBean verifyPermission(@RequestHeader("token") String token,String permission) throws IOException, ClassNotFoundException {
        boolean result=userService.verifyPermission(token,permission);
            return ResponseBean.success(null,"验证通过");
    }


    //@ApiOperation()

    @GetMapping("/getUserList")
    public ResponseBean getUserList(PageUserDto pageUserDto, HttpServletRequest request) throws IOException, ClassNotFoundException {
        UserVO userVOList= userService.getUserList(pageUserDto,request);
        return  ResponseBean.success(userVOList,null);
    }



    @PostMapping("/updatePermission")
    public ResponseBean updatePermission(@RequestBody PermissionDto permissionDto){
        userService.updatePermission(permissionDto);
        return null;
    }

    @GetMapping("/getPermissionList")
    public ResponseBean getPermissionList(Integer currentPage){
       PermissionVO permissionVO= permissionService.getPermissionList(currentPage);
       return ResponseBean.success(permissionVO,null);
    }
    @PostMapping("/locked")
    public ResponseBean locked(@RequestBody LockedDto lockedDto){
        userService.lockedUser(lockedDto);
        return null;
    }
}

