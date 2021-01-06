package com.hzvtc1063.filemanage.interceptor;

import cn.hutool.core.date.DateUtil;
import com.hzvtc1063.filemanage.service.FileService;
import com.hzvtc1063.filemanage.utils.JWTUtil;
import com.hzvtc1063.filemanage.utils.PathUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileWriter;

/**
 * @author hangzhi1063
 * @date 2020/12/22 19:30
 */
@Slf4j
public class FileInterceptor implements HandlerInterceptor {

    @Autowired
    private FileService fileService;
    @Autowired
    private RedisTemplate<Object,Object> redisTemplate;
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        log.info("----------进入日志拦截器-----------");
        //String uri = request.getRequestURI();
        //String fileName = (String) request.getAttribute("fileName");

        //if (("".equals(fileName)||null==fileName)&&!"/api/file/reName".equals(uri)){
          //  return;
        //}
        //String token = request.getHeader("token");
        String now = DateUtil.now();
        String ip = PathUtils.getIpAddr(request);
        String username;
        String str;
        //HandlerMethod handlerMethod = (HandlerMethod) handler;
        //if (token == null || "".equals(token) || "null".equals(token)) {
        /*    username = "guest";
        } else {
            username = JWTUtil.getUsername(token);
        }
        if ("/api/file/reName".equals(uri)) {
            String newName = (String) request.getAttribute("newName");
            String oldName = (String) request.getAttribute("oldName");
            str = now + " - " + ip + " - " + username + " " + handlerMethod.getMethod().getName() + " '" + oldName + "' to '" + newName + "'";
            log.info(str);
        } else {
            //   2020-12-14 14:10:43.067 - 192.168.15.1 - user() create a text file:'s安定'
            str = now + " - " + ip + " - " + username + " " + handlerMethod.getMethod().getName() + " '" + fileName + "'";
            log.info(str);
        }
        File file =new File(PathUtils.getSystemPath()+"/log.txt");

        if (!file.exists()){
            File parentFile = file.getParentFile();
            if (!parentFile.exists()){
                parentFile.mkdirs();
            }
            file.createNewFile();
            com.hzvtc1063.filemanage.entity.File file1=new com.hzvtc1063.filemanage.entity.File();
            file1.setFileName(file.getName());
            file1.setFileSize(file.length());
            file1.setFileUrl(file.getAbsolutePath());
            file1.setIsDir(0);
            file1.setFilePath("/");
            file1.setExtName("txt");
            fileService.insert(file1);
            log.info("新建日志文件完成");
        }
        FileWriter out = new FileWriter(file, true);
        out.write(str);
        out.write("\r\n");
        out.flush();
        out.close();
*/

    }
}
