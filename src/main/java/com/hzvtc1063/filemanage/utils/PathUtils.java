package com.hzvtc1063.filemanage.utils;

import com.hzvtc1063.filemanage.FilemanageApplication;

import javax.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @author hangzhi1063
 * @date 2020/12/19 19:46
 */
public class PathUtils {
    public static String getSystemPath() {
        String path = FilemanageApplication.class.getProtectionDomain().getCodeSource().getLocation().getPath();

        if (path.contains("jar")) {
            path = path.substring(0, path.lastIndexOf("."));
            System.out.println(path);
            String path1 = path.substring(0, path.lastIndexOf("/"));
            System.out.println(path1);

            return path1.replaceFirst("file:", "") + "/" + "fileManage";
        }
        return "d:\\myfileabc";
    }
        public static String getIpAddr(HttpServletRequest request) {
            String ipAddress = null;
            try {
                ipAddress = request.getHeader("x-forwarded-for");
                if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
                    ipAddress = request.getHeader("Proxy-Client-IP");
                }
                if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
                    ipAddress = request.getHeader("WL-Proxy-Client-IP");
                }
                if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
                    ipAddress = request.getRemoteAddr();
                    if (ipAddress.equals("127.0.0.1")) {
                        // 根据网卡取本机配置的IP
                        InetAddress inet = null;
                        try {
                            inet = InetAddress.getLocalHost();
                        } catch (UnknownHostException e) {
                            e.printStackTrace();
                        }
                        ipAddress = inet.getHostAddress();
                    }
                }
                // 对于通过多个代理的情况，第一个IP为客户端真实IP,多个IP按照','分割
                if (ipAddress != null && ipAddress.length() > 15) { // "***.***.***.***".length()
                    // = 15
                    if (ipAddress.indexOf(",") > 0) {
                        ipAddress = ipAddress.substring(0, ipAddress.indexOf(","));
                    }
                }
            } catch (Exception e) {
                ipAddress="";
            }
            // ipAddress = this.getRequest().getRemoteAddr();

            return ipAddress;
        }

    }

