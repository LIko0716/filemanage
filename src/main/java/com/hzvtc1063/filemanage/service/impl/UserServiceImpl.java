package com.hzvtc1063.filemanage.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hzvtc1063.filemanage.dto.*;
import com.hzvtc1063.filemanage.entity.Permission;
import com.hzvtc1063.filemanage.entity.User;
import com.hzvtc1063.filemanage.enums.PermissionEnum;
import com.hzvtc1063.filemanage.enums.UserEnum;
import com.hzvtc1063.filemanage.exception.PermissionException;
import com.hzvtc1063.filemanage.exception.UserException;
import com.hzvtc1063.filemanage.mapper.PermissionMapper;
import com.hzvtc1063.filemanage.mapper.UserMapper;
import com.hzvtc1063.filemanage.service.UserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hzvtc1063.filemanage.utils.JWTUtil;
import com.hzvtc1063.filemanage.utils.SerializeUtil;
import com.hzvtc1063.filemanage.vo.UserDetailVO;
import com.hzvtc1063.filemanage.vo.UserVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author 1063
 * @since 2020-12-09
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Autowired
    private PermissionMapper permissionMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private RedisTemplate<Object, Object> redisTemplate;

    @Transactional
    @Override
    public boolean insert(User user) {
        int insert = userMapper.insert(user);
        QueryWrapper<User> wrapper =new QueryWrapper<>();
        wrapper.eq("user_name",user.getUserName());
        User newUser = userMapper.selectOne(wrapper);
        Permission newPermission = new Permission();
        //newPermission.setUserId(user.getId());
        //newPermission.setMkdir(1);
        newPermission.setUserId(newUser.getId());
        //newPermission.setUpload(1);
        permissionMapper.insert(newPermission);
        return insert > 0;
    }

    @Override
    public String verify(User user) throws UnsupportedEncodingException {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("user_name", user.getUserName());
        User mUser = userMapper.selectOne(wrapper);
        if (mUser == null) {

            throw new UserException(UserEnum.USER_NOTEXIST);
        }

        if (!user.getPassword().equals(mUser.getPassword())) {
            throw new UserException(UserEnum.PASSWORD_NOTSAME);
        }

        if (mUser.getStatus() == 1) {
            throw new UserException(UserEnum.USER_LOCKED);
        }
        String token = JWTUtil.sign(user.getUserName(), user.getPassword());
        redisTemplate.opsForValue().set(user.getUserName() + ":" + user.getPassword() + ":token", token, 30, TimeUnit.MINUTES);
        User newUser = userMapper.selectOne(wrapper);
        byte[] serializeUser = SerializeUtil.serialize(newUser);

        redisTemplate.opsForValue().set(user.getUserName(), serializeUser);
        return token;
    }

    @Override
    @Transactional
    public void deleteUser(DeleteUserDto deleteUserDto) {
        userMapper.deleteById(deleteUserDto.getId());
        QueryWrapper<Permission> wrapper= new QueryWrapper<>();
        wrapper.eq("user_id",deleteUserDto.getId());
        permissionMapper.delete(wrapper);
    }

    @Override
    public String verifyAdmin(User user) throws UnsupportedEncodingException {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("user_name", user.getUserName());
        User mUser = userMapper.selectOne(wrapper);
        if (mUser == null) {

            throw new UserException(UserEnum.USER_NOTEXIST);
        }

        if (!user.getPassword().equals(mUser.getPassword())) {
            throw new UserException(UserEnum.PASSWORD_NOTSAME);
        }

        if (mUser.getStatus() == 1) {
            throw new UserException(UserEnum.USER_LOCKED);
        }

        if (!"管理员".equals(mUser.getRole())) {
            throw new UserException(UserEnum.USER_PERMISSION_NOTENOUGH);
        }

        String token = JWTUtil.sign(user.getUserName(), user.getPassword());
        redisTemplate.opsForValue().set(user.getUserName() + ":" + user.getPassword() + ":token", token, 30, TimeUnit.MINUTES);
        byte[] serializeUser = SerializeUtil.serialize(mUser);
        redisTemplate.opsForValue().set(user.getUserName(), serializeUser);
        return token;

    }

    @Override
    @Transactional
    public boolean verifyPermission(String token, String permission) throws IOException, ClassNotFoundException {
        String username = JWTUtil.getUsername(token);
//        QueryWrapper<User> wrapper = new QueryWrapper<>();
//        wrapper.eq("user_name", username);
//        User user = userMapper.selectOne(wrapper);
//        if (user == null) {
//            throw new UserException(UserEnum.USER_NOTEXIST);
//        }
        User user = getUser(username);
        //JWTUtil.verify(token, username, user.getPassword());
        QueryWrapper<Permission> wrapper2 = new QueryWrapper<>();
        wrapper2.eq("user_id", user.getId());
        Permission permissions = permissionMapper.selectOne(wrapper2);
        //如果权限为空 为第一次登录 默认可以进行上传文件和上传文件夹

        if ("rename".equals(permission)) {
            if (permissions.getRname() == 0) {
                throw new PermissionException(PermissionEnum.PERMISSION_NOTENOUGH);
            }
        } else if ("move".equals(permission)) {
            if (permissions.getMove() == 0) {
                throw new PermissionException(PermissionEnum.PERMISSION_NOTENOUGH);
            }
        } else if ("del".equals(permission)) {
            if (permissions.getDel() == 0) {
                throw new PermissionException(PermissionEnum.PERMISSION_NOTENOUGH);
            }
        } else if ("upload".equals(permission)) {
            if (permissions.getUpload() == 0) {
                throw new PermissionException(PermissionEnum.PERMISSION_NOTENOUGH);
            }
        } else if ("mkdir".equals(permission)) {
            if (permissions.getMkdir() == 0) {
                throw new PermissionException(PermissionEnum.PERMISSION_NOTENOUGH);
            }
        } else {
            throw new PermissionException(PermissionEnum.PERMISSION_NOTENOUGH);
        }
        return true;
    }

   /* @Override
    public Long getUserIdByToken(String token) throws UnsupportedEncodingException {
        String username = JWTUtil.getUsername(token);
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("user_name", username);
        User user = userMapper.selectOne(wrapper);
        if (user == null) {
            throw new UserException(UserEnum.USER_NOTEXIST);
        }
        JWTUtil.verify(token, username, user.getPassword());
        return user.getId();
    }*/

    @Override
    public String logout(String token) throws IOException, ClassNotFoundException {
        String username = JWTUtil.getUsername(token);
        User user = getUser(username);
        redisTemplate.delete(user.getUserName() + ":" + user.getPassword() + ":token");
        return username;
    }

    @Override
    public UserVO getUserList(PageUserDto pageUserDto, HttpServletRequest request) throws IOException, ClassNotFoundException {
        String token = request.getHeader("token");
        Integer currentPage;
        if (pageUserDto.getCurrentPage() == null) {
            currentPage = 1;
        }else{
            currentPage =pageUserDto.getCurrentPage();
        }
        String username = JWTUtil.getUsername(token);
        User user = getUser(username);
        if (!"管理员".equals(user.getRole())) {
            throw new UserException(UserEnum.USER_PERMISSION_NOTENOUGH);
        }
        QueryWrapper<User> wrapper =new QueryWrapper<>();
        wrapper.ne("id",1);
        if (pageUserDto.getUserName()!=null){
            wrapper.likeRight("user_name",pageUserDto.getUserName());
        }
        Page<User> page = new Page<>(currentPage, 3);
        Page<User> page1 = userMapper.selectPage(page, wrapper);
        List<User> records = page1.getRecords();
        List<UserDetailVO> userDetailVOS = new ArrayList<>();
        for (User record : records) {
            UserDetailVO userDetailVO = new UserDetailVO();
            BeanUtils.copyProperties(record, userDetailVO);
            if (record.getStatus()==0){
                userDetailVO.setBstatus(true);
            }else{
                userDetailVO.setBstatus(false);
            }
            userDetailVOS.add(userDetailVO);
        }
        UserVO userVO = new UserVO();
        userVO.setTotalSize(page1.getTotal());
        userVO.setUserDetail(userDetailVOS);
        return userVO;
    }

    @Override
    public void lockedUser(LockedDto lockedDto) {
        User user = userMapper.selectById(lockedDto.getId());
        if (lockedDto.isStatus()){
            user.setStatus(0);
        }else{
            user.setStatus(1);
        }
        userMapper.updateById(user);
    }

    @Override
    public void updatePermission(PermissionDto permissionDto) {
        QueryWrapper<Permission> wrapper =new QueryWrapper<>();
        wrapper.eq("user_id",permissionDto.getId());
        Permission permission = permissionMapper.selectOne(wrapper);
        if ("mkdir".equals(permissionDto.getPermissionName())){
            if (permissionDto.isStatus()){
                permission.setMkdir(0);
            }else{
                permission.setMkdir(1);
            }
        }else if("rname".equals(permissionDto.getPermissionName())){
            if (permissionDto.isStatus()){
                permission.setRname(0);
            }else{
                permission.setRname(1);
            }
        }else if("del".equals(permissionDto.getPermissionName())){
            if (permissionDto.isStatus()){
                permission.setDel(0);
            }else{
                permission.setDel(1);
            }
        }else if("upload".equals(permissionDto.getPermissionName())){
            if (permissionDto.isStatus()){
                permission.setUpload(0);
            }else{
                permission.setUpload(1);
            }
        }
        permissionMapper.updateById(permission);
    }

    @Override
    public void updateUser(UpUserDto upUserDto) {
        User user = userMapper.selectById(upUserDto.getId());
        user.setUserName(upUserDto.getUserName());
        user.setRole(upUserDto.getRole());
        userMapper.updateById(user);
    }

    public User getUser(String username) throws IOException, ClassNotFoundException {
        byte[] b = (byte[]) redisTemplate.opsForValue().get(username);
        User user = (User) SerializeUtil.deserialize(b);
        return user;
    }
}
