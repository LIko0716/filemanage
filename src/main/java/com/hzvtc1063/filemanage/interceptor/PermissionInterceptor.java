package com.hzvtc1063.filemanage.interceptor;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hzvtc1063.filemanage.Result.ResponseBean;
import com.hzvtc1063.filemanage.entity.Permission;
import com.hzvtc1063.filemanage.entity.User;
import com.hzvtc1063.filemanage.mapper.PermissionMapper;
import com.hzvtc1063.filemanage.mapper.UserMapper;
import com.hzvtc1063.filemanage.utils.JWTUtil;
import com.hzvtc1063.filemanage.utils.SerializeUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author hangzhi1063
 * @date 2020/12/27 17:04
 */
@Slf4j

public class PermissionInterceptor implements HandlerInterceptor {
    @Autowired
    private PermissionMapper permissionMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private RedisTemplate<Object,Object> redisTemplate;
    private void returnJson(HttpServletResponse response, Object obj) {
        PrintWriter writer = null;
        ObjectMapper om = new ObjectMapper();
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=utf-8");
        try {
            String json = om.writeValueAsString(obj);
            writer = response.getWriter();
            writer.print(json);

        } catch (IOException e) {
            log.error("response error", e);
        } finally {
            if (writer != null)
                writer.close();
        }
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = request.getHeader("token");
        String username = JWTUtil.getUsername(token);
        User user = getUser(username);
        QueryWrapper<Permission> wrapper=new QueryWrapper<>();
        wrapper.eq("user_id",user.getId());
        Permission permission = permissionMapper.selectOne(wrapper);
        String uri = request.getRequestURI();
        if ("/api/file/mkdir".equals(uri)){
            if (permission.getMkdir()==0){
                return true;
            }else{
                returnJson(response, ResponseBean.error(360, "你的权限不足"));
                return false;
            }
        }else if("/api/file/upload".equals(uri)||"/api/file/uploadFloder".equals(uri)){
            if (permission.getUpload()==1){
                returnJson(response, ResponseBean.error(360, "你的权限不足"));
                return false;
            }else{
                return true;
            }
        }else if("/api/file/reName".equals(uri)){
            if (permission.getRname()==1){
                returnJson(response, ResponseBean.error(360, "你的权限不足"));
                return false;
            }else{
                return true;
            }
        }else if("/api/file/deleteFile".equals(uri)){
            if (permission.getDel()==1){
                returnJson(response, ResponseBean.error(360, "你的权限不足"));
                return false;
            }else{
                return true;
            }
        }
        return false;
    }

    public User getUser(String username) throws IOException, ClassNotFoundException {
        byte[] b = (byte[]) redisTemplate.opsForValue().get(username);
        User user = (User) SerializeUtil.deserialize(b);
        return user;
    }
}
