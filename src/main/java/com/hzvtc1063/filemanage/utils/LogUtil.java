package com.hzvtc1063.filemanage.utils;

import cn.hutool.core.date.DateUtil;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletRequest;
import java.io.*;

/**
 * @author hangzhi1063
 * @date 2020/12/24 15:10
 */
@Slf4j
public class LogUtil {
                                                //msg[0] userName [1] 文件名 [2] 文件名2
    public static void log(HttpServletRequest request, String methodName, String[] msg) {
        log.info("----------进入日志生成器-----------");
        String ip = PathUtils.getIpAddr(request);
        String now = DateUtil.now();
        try {
            PrintStream out = new PrintStream(new FileOutputStream(PathUtils.getSystemPath() + "/"+"log.out"));
            PrintStream old = System.out;
            System.setOut(out);
            if (msg.length == 2) {
                System.out.println(now + " - " + ip + " - " + msg[0] + " " + methodName + " '" + msg[1] + "'");
            } else {
                System.out.println(now + " - " + ip + " - " + msg[0] + " " + methodName + " '" + msg[1] + "' to '" + msg[2] + "'");
            }
            System.setOut(old);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }


}
