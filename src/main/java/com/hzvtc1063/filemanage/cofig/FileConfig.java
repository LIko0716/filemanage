package com.hzvtc1063.filemanage.cofig;

import com.hzvtc1063.filemanage.interceptor.FileInterceptor;
import com.hzvtc1063.filemanage.interceptor.PermissionInterceptor;
import com.hzvtc1063.filemanage.interceptor.TokenInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 定位各种文件或头像地址
 */
@Configuration
public class FileConfig implements WebMvcConfigurer {

    @Bean
    public TokenInterceptor getTokenInterceptor() {
        return new TokenInterceptor();
    }

    @Bean
    public PermissionInterceptor getPermissionIntercepetor(){
        return new PermissionInterceptor();
    }


    //注册拦截器
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        InterceptorRegistration registration = registry.addInterceptor(getTokenInterceptor());
        registration.addPathPatterns("/**");                      //所有路径都被拦截
        registration.excludePathPatterns(                         //添加不拦截路径
                "/user/registry",            //登录
                "/user/login",
                "/file/download",
                "/file/getFileList",
                "/file/watch",
                "/user/admin/login",
                "/user/getPermissionList",
                "/file/selectByFileName",
                "/**/*.html",            //html静态资源
                "/**/*.js",
                //js静态资源
                "/**/*.css",             //css静态资源
                "/**/*.woff",
                "/**/*.ttf",
                "/images/**",
                "/upload/upload",
                "classpath:/static/**"
        );
        InterceptorRegistration registration1 = registry.addInterceptor(getPermissionIntercepetor());
        registration1.addPathPatterns(
                "/file/mkdir",
                "/file/upload",
                "/file/uploadFloder",
                "/file/reName",
                "/file/deleteFile"
        );
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        registry.addResourceHandler("/images/filePic/**").addResourceLocations("classpath:/static/images/filePic/");

    }
}

















