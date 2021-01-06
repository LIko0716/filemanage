package com.hzvtc1063.filemanage.controller;

import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.hzvtc1063.filemanage.Result.ResponseBean;
import com.hzvtc1063.filemanage.exception.FileException;
import com.hzvtc1063.filemanage.exception.UserException;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.connector.ClientAbortException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.UnsupportedEncodingException;

/**
 * @author hangzhi1063
 * @date 2020/12/9 14:43
 */
@RestControllerAdvice
@Slf4j
public class ExceptionController {


   /* @ExceptionHandler(TokenExpiredException.class)
    public ResponseBean tokenExpiredException(HttpServletRequest request, TokenExpiredException e) {
        log.info("token过期");
        log.info(e.getMessage());
        return new ResponseBean(10086, "token已过期", null);
    }
*/
    //捕获token异常
    @ExceptionHandler({UnsupportedEncodingException.class,UnsupportedEncodingException.class, JWTVerificationException.class})
    public ResponseBean JWTException(HttpServletRequest request) {
        log.info("身份信息异常");
        return new ResponseBean(10086, "身份信息异常", null);
    }

    //捕获用户异常
    @ExceptionHandler(UserException.class)
    public ResponseBean userException(HttpServletRequest request, UserException e) {
        log.info("用户异常处理");
        log.info(e.getMessage());
        return ResponseBean.error(400, e.getMessage());
    }
    /*@ExceptionHandler(ClientAbortException.class)
    public void clientAbortException( ClientAbortException e){
        log.info("---------qq浏览器导致下载错误---------");
    }*/
    //捕捉文件异常
    @ExceptionHandler(FileException.class)
    public ResponseBean fileException(HttpServletRequest request, FileException e) {
        log.info("文件处理异常");
        log.info(e.getMessage());
        return ResponseBean.error(400, e.getMessage());
    }

    // 捕捉其他所有异常
    @ExceptionHandler(Exception.class)
    public ResponseBean globalException(HttpServletRequest request, Exception e) {
        log.info("全局异常处理");
        return new ResponseBean(getStatus(request).value(), e.getMessage(), null);
    }

    private HttpStatus getStatus(HttpServletRequest request) {
        Integer statusCode = (Integer) request.getAttribute("javax.servlet.error.status_code");
        if (statusCode == null) {
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }
        return HttpStatus.valueOf(statusCode);
    }
}