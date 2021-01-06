package com.hzvtc1063.filemanage.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hzvtc1063.filemanage.entity.Permission;
import com.hzvtc1063.filemanage.entity.User;
import com.hzvtc1063.filemanage.mapper.PermissionMapper;
import com.hzvtc1063.filemanage.mapper.UserMapper;
import com.hzvtc1063.filemanage.service.PermissionService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hzvtc1063.filemanage.vo.PermissionDetailVO;
import com.hzvtc1063.filemanage.vo.PermissionVO;
import com.hzvtc1063.filemanage.vo.UserDetailVO;
import io.swagger.models.auth.In;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author 1063
 * @since 2020-12-09
 */
@Service
public class PermissionServiceImpl extends ServiceImpl<PermissionMapper, Permission> implements PermissionService {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private PermissionMapper permissionMapper;

    @Override
    public PermissionVO getPermissionList(Integer currentPage) {
        QueryWrapper<User> wrapper =new QueryWrapper<>();
        wrapper.ne("id",1);
        if (currentPage==null){
            currentPage=1;
        }
        Page<User> page = new Page<>(currentPage, 3);
        Page<User> page1 = userMapper.selectPage(page, wrapper);
        List<User> users = page1.getRecords();
        List<PermissionDetailVO> permissionDetailVOS = new ArrayList<>();
        List<Long> list =new ArrayList<>();
        for (User user : users) {
            list.add(user.getId());
        }
        QueryWrapper<Permission> wrapper1 =new QueryWrapper<>();
        wrapper1.in("user_id",list);
        wrapper1.orderByAsc("user_id");
        List<Permission> permissions = permissionMapper.selectList(wrapper1);
        for (Permission permission : permissions) {
            PermissionDetailVO permissionDetailVO =new PermissionDetailVO();
            if (permission.getRname()==0){
                permissionDetailVO.setRname(true);
            }
            if (permission.getMkdir()==0){
                permissionDetailVO.setMkdir(true);
            }
            if (permission.getUpload()==0){
                permissionDetailVO.setUpload(true);
            }
            if (permission.getDel()==0){
                permissionDetailVO.setDel(true);
            }
            for (User user : users) {
                if (user.getId()==permission.getUserId()){
                    permissionDetailVO.setUserName(user.getUserName());
                    permissionDetailVO.setId(user.getId());
                }
            }
            permissionDetailVOS.add(permissionDetailVO);
        }
        PermissionVO permissionVO =new PermissionVO();
        permissionVO.setTotalSize(page1.getTotal());
        permissionVO.setPermissionDetail(permissionDetailVOS);
        return permissionVO;
    }
}
