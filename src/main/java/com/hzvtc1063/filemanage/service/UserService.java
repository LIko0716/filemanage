package com.hzvtc1063.filemanage.service;

import com.hzvtc1063.filemanage.dto.*;
import com.hzvtc1063.filemanage.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;
import com.hzvtc1063.filemanage.vo.UserVO;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author 1063
 * @since 2020-12-09
 */
public interface UserService extends IService<User> {

    boolean insert(User user);

    String verify(User user) throws UnsupportedEncodingException;

    boolean verifyPermission(String token,String permission) throws IOException, ClassNotFoundException;

    //Long getUserIdByToken(String token) throws UnsupportedEncodingException;

    String logout(String token) throws IOException, ClassNotFoundException;

    UserVO getUserList(PageUserDto pageUserDto, HttpServletRequest request) throws IOException, ClassNotFoundException;

    String verifyAdmin(User user) throws UnsupportedEncodingException;

    void lockedUser(LockedDto lockedDto);

    void updatePermission(PermissionDto permissionDto);

    void deleteUser(DeleteUserDto deleteUserDto);

    void updateUser(UpUserDto upUserDto);
}
