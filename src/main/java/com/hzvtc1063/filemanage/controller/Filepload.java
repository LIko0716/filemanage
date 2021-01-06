package com.hzvtc1063.filemanage.controller;

import com.hzvtc1063.filemanage.entity.MultipartFileParam;
import com.hzvtc1063.filemanage.service.FileService;
import com.hzvtc1063.filemanage.service.MaterialService;
import com.hzvtc1063.filemanage.utils.FileUtil;
import com.hzvtc1063.filemanage.utils.PathUtils;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.servlet.ServletFileUpload;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author hangzhi1063
 * @date 2020/12/25 14:36
 */
@RestController
@RequestMapping("/test")
@Slf4j
public class Filepload {

    @Autowired
    private MaterialService materialService;
    @Autowired
    private FileService fileService;
    private String separator = File.separator;

    @ApiOperation("大文件分片上传")
    @PostMapping("/upload")
    public void fileChunkUpload(MultipartFileParam param, HttpServletRequest request, HttpServletResponse response, String filePath) {


        //自己的业务获取存储路径，可以换成自己的
        //   OSSInformation ossInformation = ossInformationService.queryOne();
        String systemPath = PathUtils.getSystemPath();
        String root;
        if (filePath == null || "".equals(filePath)) {
            //       String root = ossInformation.getRoot();
            filePath = "/";
            root = systemPath;
        } else {
            filePath = filePath + "/";
            root = systemPath + "/" + filePath;
        }
        //验证文件夹规则,不能包含特殊字符
       // File file = new File(root);
        //createDirectoryQuietly(file);

       // String path = file.getAbsolutePath();
        response.setContentType("text/html;charset=UTF-8");
        // response.setStatus对接前端插件
        //        200, 201, 202: 当前块上传成功，不需要重传。
        //        404, 415. 500, 501: 当前块上传失败，会取消整个文件上传。
        //        其他状态码: 出错了，但是会自动重试上传。

        try {
            /**
             * 判断前端Form表单格式是否支持文件上传
             */
            boolean isMultipart = ServletFileUpload.isMultipartContent(request);
            if (!isMultipart) {
                //这里是我向前端发送数据的代码，可理解为 return 数据; 具体的就不贴了
                System.out.println("不支持的表单格式");
                response.setStatus(404);
                response.getOutputStream().write("不支持的表单格式".getBytes());
            } else {

                param.setTaskId(param.getIdentifier());
                String relativePath =param.getRelativePath();
                log.info("------------------" + param.getFile().getOriginalFilename() + "------------------");
                String substring = relativePath.substring(0, relativePath.lastIndexOf("/"));
                System.out.println(substring);
                String path = root + "/" + substring;
                File parentFile = new File(path);
                List<com.hzvtc1063.filemanage.entity.File> dirList = new ArrayList<>();
                createFolder(parentFile, dirList, filePath);
                //materialService.chunkUploadByMappedByteBuffer(param, path);//service层


                // 文件路径
                for (com.hzvtc1063.filemanage.entity.File file1 : dirList) {
                    file1.setFileSize(FileUtil.getTotalSizeOfFilesInDir(new java.io.File(file1.getFileUrl())));
                }
                fileService.insertFolder(dirList);
                response.setStatus(200);
                response.getWriter().print("上传成功");
            }
            response.getWriter().flush();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("上传文件失败");
            response.setStatus(415);
        }
    }

    public void createFolder(java.io.File file, List<com.hzvtc1063.filemanage.entity.File> dirList, String filePath) {
        //先判断文件夹是否已经存在 如果存在直接返回
        if (file.exists()) {
            return;
        } else {

            //如果不存在继续向下递归 直接找到存在的顶层文件夹
            java.io.File parentFile = file.getParentFile();
            createFolder(parentFile, dirList, filePath);
            //创建文件夹 以及数据库对象File
            file.mkdir();
            com.hzvtc1063.filemanage.entity.File dir = new com.hzvtc1063.filemanage.entity.File();
            dir.setIsDir(1);
            //  String dirpath = parentFile.getPath().replaceAll("\\\\","/");
            String s;
            // /root/fileManage/39王加淳
            // /root/fileManage/39王加淳/img
            // /root/fileManage/39王加淳/39王加淳
            // d:\myfileabc\39王加淳\39王加淳    "/39王加淳"
            String path = parentFile.getAbsolutePath().replaceAll("\\\\","/").replace(PathUtils.getSystemPath().replaceAll("\\\\","/") + filePath.substring(0, filePath.length() - 1), "");

            if ("".equals(path)) {
                s = filePath;
            } else {
                //d:\myfileabc\39王加淳\39王加淳\js
                path = path.replace(separator, "/");
                s = filePath + path.substring(1) + "/";
            }

            dir.setFilePath(s);
            dir.setFileUrl(file.getAbsolutePath());
            dir.setFileName(file.getName());
            dirList.add(dir);
        }
        return;
    }
}
