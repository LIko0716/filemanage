package com.hzvtc1063.filemanage.interceptor;

import com.auth0.jwt.exceptions.AlgorithmMismatchException;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hzvtc1063.filemanage.Result.ResponseBean;
import com.hzvtc1063.filemanage.entity.User;
import com.hzvtc1063.filemanage.enums.UserEnum;
import com.hzvtc1063.filemanage.exception.UserException;
import com.hzvtc1063.filemanage.service.UserService;
import com.hzvtc1063.filemanage.utils.JWTUtil;
import com.hzvtc1063.filemanage.utils.SerializeUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.TimeUnit;

/**
 * @author hangzhi1063
 * @date 2020/12/11 8:12
 */
@Slf4j
public class TokenInterceptor implements HandlerInterceptor {

    @Autowired
    private RedisTemplate<Object, Object> redisTemplate;

    @Autowired
    private UserService userService;



    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws ClassNotFoundException, IOException {

        if (HttpMethod.OPTIONS.toString().equals(request.getMethod())) {

            log.info("OPTIONS请求，放行");
            return true;
        }
        if ("/favicon.ico".equals(request.getServletPath())){
            return true;
        }
        String token = request.getHeader("token");
        log.info(token);


        if (token == null||"null".equals(token)||"".equals(token)) {

            log.info("用户未登录");
            returnJson(response, ResponseBean.error(10086, "用户未登录"));
            return false;
        }


        String username;
        try {
            username = JWTUtil.getUsername(token);
        } catch (Exception e) {
            returnJson(response, ResponseBean.error(10086, "登录信息失效"));
            return false;
        }
        User user;
        byte[] b = (byte[]) redisTemplate.opsForValue().get(username);

        if (b != null) {
            user = (User) SerializeUtil.deserialize(b);
        } else {
            QueryWrapper<User> wrapper = new QueryWrapper<>();
            wrapper.eq("user_name", username);
            user = userService.getOne(wrapper);
            if (user == null) {
                throw new UserException(UserEnum.USER_NOTEXIST);
            }
            byte[] serializeUser = SerializeUtil.serialize(user);
            redisTemplate.opsForValue().set(username, serializeUser, 300, TimeUnit.SECONDS);
        }
        String cachetoken = (String) redisTemplate.opsForValue().get(user.getUserName() + ":" + user.getPassword() + ":token");
        if (cachetoken==null){

            log.info("token过期");
            returnJson(response,ResponseBean.error(10086,"登录过期"));
            return false;
        }
        if (!token.equals(cachetoken)){
            returnJson(response,ResponseBean.error(10086,"你的登录异常"));
            return  false;
        }


        try {
            JWTUtil.verify(token, user.getUserName(), user.getPassword());
        } catch (SignatureVerificationException e) {
            returnJson(response, ResponseBean.error(10086, "无效签名"));
            return false;
        } catch (AlgorithmMismatchException e) {
            // e.printStackTrace();
            returnJson(response, ResponseBean.error(10086, "token算法不一致"));
            log.info("算法不一致");
            return false;
        } catch (TokenExpiredException e) {
            // e.printStackTrace();
            returnJson(response, ResponseBean.error(10086, "token过期"));
            log.info("token过期");
            return false;
        } catch (Exception e) {
            returnJson(response, ResponseBean.error(10086, "身份登录异常"));
            log.info("身份登录异常");
            return false;
        }

        //}catch (TokenExpiredException e){
        // returnJson(response,ResponseBean.error(10086,"token过期"));
        //}

        return true;
    }

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

}
