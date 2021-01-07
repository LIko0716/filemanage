package com.hzvtc1063.filemanage.controller;


import cn.hutool.core.collection.ListUtil;
import com.hzvtc1063.filemanage.Result.ResponseBean;
import com.hzvtc1063.filemanage.dto.DirDto;
import com.hzvtc1063.filemanage.dto.RenameDto;
import com.hzvtc1063.filemanage.dto.TxtDto;
import com.hzvtc1063.filemanage.entity.File;
import com.hzvtc1063.filemanage.entity.MultipartFileParam;
import com.hzvtc1063.filemanage.entity.User;
import com.hzvtc1063.filemanage.enums.FileEnum;
import com.hzvtc1063.filemanage.exception.FileException;
import com.hzvtc1063.filemanage.service.FileService;
import com.hzvtc1063.filemanage.service.MaterialService;
import com.hzvtc1063.filemanage.utils.*;
import com.hzvtc1063.filemanage.vo.FileVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.connector.ClientAbortException;
import org.apache.commons.io.FileUtils;
import org.apache.tomcat.util.http.fileupload.servlet.ServletFileUpload;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author 1063
 * @since 2020-12-09
 */
@Api("文件操作类")
@Slf4j
@RestController
@RequestMapping("/file")
public class FileController {

    @Autowired
    private FileService fileService;
    @Autowired
    private RedisTemplate<Object, Object> redisTemplate;
    @Autowired
    private MaterialService materialService;
    private
    String separator = java.io.File.separator;

    @ApiOperation("新建文件夹")
    @PostMapping("/mkdir")
    public ResponseBean mkdir(@Validated @RequestBody DirDto dirDto, BindingResult bindingResult,
                              @RequestHeader("token") String token, HttpServletRequest request) throws IOException, ClassNotFoundException {
        log.info("新建文件夹");

        if (bindingResult.hasErrors()) {
            return ResponseBean.error(400, bindingResult.getAllErrors().get(0).getDefaultMessage());
        }
        //Long userId = userService.getUserIdByToken(token);
        User user = getUser(JWTUtil.getUsername(token));
        File file = new File();
        String filePath = "/";// /分隔符
        String diskPath;
        if ("".equals(dirDto.getFilePath()) || null == dirDto.getFilePath() || "/".equals(dirDto.getFilePath())) {

            diskPath = PathUtils.getSystemPath() + separator + dirDto.getDirName();
        } else {
            filePath = dirDto.getFilePath() + "/";
            diskPath = PathUtils.getSystemPath() + dirDto.getFilePath() + separator + dirDto.getDirName();
        }
        int count = fileService.selectDirNum(dirDto.getDirName(), filePath);
        if (count != -1) {
            throw new FileException(FileEnum.FILENAME_ALEADRY_EXIST);
        }
        file.setIsDir(1);
        file.setFileSize(0L);
        file.setUserId(user.getId());
        file.setFilePath(filePath);
        file.setFileName(dirDto.getDirName());
        file.setFileUrl(diskPath);
        java.io.File dirFile = new java.io.File(diskPath);
        if (!dirFile.exists()) {
            dirFile.mkdirs();
        }
        boolean result = fileService.insert(file);
        if (result) {
            String[] msg = {user.getUserName(), dirDto.getDirName()};
            LogUtil.log(request, "create a directory", msg);
            return ResponseBean.success(null, "新建文件夹成功");
        } else {
            return ResponseBean.error("新建文件夹失败");
        }
    }

    @PostMapping("/createTxt")
    public ResponseBean createTxt(@RequestBody TxtDto txtDto, @RequestHeader("token") String token, HttpServletRequest request) throws IOException, ClassNotFoundException {

        String rootPath = PathUtils.getSystemPath();
        String filePath = "/";
        if (!txtDto.getFilePath().equals("/")) {
            filePath = txtDto.getFilePath() + "/";
            rootPath = rootPath + filePath;
        }
        java.io.File file = new java.io.File(rootPath + txtDto.getFileName());
        // int read = resourceAsStream.read();
        if (!file.exists()) {
            file.createNewFile();
        } else {
            throw new FileException(FileEnum.FILENAME_ALEADRY_EXIST);
        }
        FileOutputStream fop = new FileOutputStream(file);
        fop.write(txtDto.getContent().getBytes());
        fop.flush();
        fop.close();
        File upFile = new File();
        String fileName = txtDto.getFileName();
        upFile.setIsDir(0);
        upFile.setFileName(fileName);
        upFile.setFilePath(filePath);
        upFile.setExtName("txt");
        upFile.setFileSize(file.length());
        upFile.setFileUrl(file.getAbsolutePath());
        String username = JWTUtil.getUsername(token);
        User user = getUser(username);
        upFile.setUserId(user.getId());
        boolean insert = fileService.insert(upFile);
        List<File> fileList = updateFolder(upFile.getFileUrl());
        if (fileList != null) {
            fileService.updateFolder(fileList);
        }

        String[] msg = {user.getUserName(), upFile.getFilePath() + upFile.getFileName()};
        LogUtil.log(request, "create a txt", msg);
        return ResponseBean.success(null, "上传成功");

    }


    @PostMapping("/upload")
    //当前路径
    public ResponseBean upload(@RequestParam("file") MultipartFile mFile, @RequestHeader("token") String token, String filePath, HttpServletRequest request) throws IOException, ClassNotFoundException {
        log.info("进入文件上传");

        String rootPath = PathUtils.getSystemPath();
        if (mFile.isEmpty()) {
            throw new FileException(FileEnum.FILE_UPLOAD_FAIL);
        }
        if ("".equals(filePath) || null == filePath || "/".equals(filePath)) {
            filePath = "/";
        }
        //select count(*) from file where file_name like "fileName%.jpg"
        //获取输入流

        //获取文件名
        String fileName = mFile.getOriginalFilename();
        //获取扩展名
        String extFilename = fileName.substring(fileName.lastIndexOf(".") + 1);
        int count = fileService.selectNum(fileName, extFilename, filePath);
        if (count != -1) {
            //如果文件名已存在,在后方拼接上是第几个
            fileName = fileName.substring(0, fileName.lastIndexOf(".")) + "(" + count + ")" + "." + extFilename;
        }

        File file = new File();
        file.setExtName(extFilename);
        file.setFileName(fileName);
        file.setFileSize(mFile.getSize());
        file.setIsDir(0);
        String diskPath;
        if ("/".equals(filePath)) {
            file.setFilePath("/");//数据库存储虚拟文件路径
            diskPath = rootPath + separator + fileName ;
            file.setFileUrl(diskPath);
        } else {
            file.setFilePath(filePath + "/");
            diskPath = rootPath + filePath + separator + fileName;
            file.setFileUrl(diskPath);
        }
        java.io.File file1 = new java.io.File(diskPath);
        java.io.File parentFile = file1.getParentFile();
        //System.out.println(rootPath + filePath);
        if (!parentFile.exists()) {
            //待定 是否要自建文件夹
            File dir = new File();

            parentFile.mkdirs();
        }
        mFile.transferTo(file1);
        //Long userId=userService.getUserIdByToken(token);
        String username = JWTUtil.getUsername(token);
        User user = getUser(username);
        //User user = (User) redisTemplate.opsForValue().get(username.getBytes());
        file.setUserId(user.getId());

        boolean result = fileService.insert(file);
        List<File> fileList = updateFolder(file.getFileUrl());
        if (fileList != null) {
            fileService.updateFolder(fileList);
        }
        if (result) {
            String[] msg = {username, file.getFilePath() + file.getFileName()};
            LogUtil.log(request, "upload", msg);
            return ResponseBean.success(null, "上传成功");
        } else {
            return ResponseBean.error("上传失败");
        }
    }

    @GetMapping("/download")
    public Object downloadTest(Long id, HttpServletResponse response, HttpServletRequest request) throws IOException {
        log.info("------------进入下载控制器-------");

        File byId = fileService.getById(id);
        String[] msg = {"", byId.getFilePath() + byId.getFileName()};
        LogUtil.log(request, "download", msg);
        if (byId.getIsDir() == 0) {

            FileUtil.downloadFile(response, new java.io.File(byId.getFileUrl()));

        } else {
            Random random = new Random();
            int i = random.nextInt();
            String zipName = byId.getFileName() + ".zip";
            String zipPath = PathUtils.getSystemPath() + separator + zipName;


            FileUtil.toZip(byId.getFileUrl(), zipPath, true);

            java.io.File zipFile = new java.io.File(zipPath);
            //删除文件（防止下一次压缩时有重复文件名）

            FileUtil.downloadFile(response, zipFile);

            zipFile.delete();

        }
        return null;

    }

    @RequestMapping(value = "/watch", method = RequestMethod.GET)
    public void getFile(HttpServletResponse response,
                        Long id) {
        // 设置编码
        response.setCharacterEncoding("UTF-8");
        try {
            java.io.File file;


            File byId = fileService.getById(id);
            file = new java.io.File(byId.getFileUrl());
            String exName = byId.getExtName();

            //String path = folder + "/" + fileName;
            //boolean flag = ossClient.doesObjectExist(ossProperties.getBucket(), path);

            // 判断文件是否存在
            if (file.exists()) {
                // 清空response
                response.reset();
                // 设置response的Header，注意这句，如果开启，默认浏览器会进行下载操作，如果注释掉，浏览器会默认预览。

                // response.addHeader("Content-Length", "" + buf.length);

                OutputStream toClient = new BufferedOutputStream(response.getOutputStream());
                // ByteArrayOutputStream bos = new ByteArrayOutputStream(1024);
                //OSSObject ossObject = ossClient.getObject(ossProperties.getBucket(), path);


                //System.out.println(contentType);
                //注意contentType类型
                response.setContentType("text/html; charset=UTF-8");
                LinkedList<String> img = ListUtil.toLinkedList("bmp,jpg,png,tif,gif,jpeg,pcx,tga,exif,fpx,svg,psd,cdr,pcd,dxf,ufo,eps,ai,raw,WMF,webp,avif".split(","));
                LinkedList<String> video = ListUtil.toLinkedList("wmv,asf,asx,rm,rmvb,mp4,3gp,mov,m4v,avi,dat,mkv,flv,vob".split(","));
                List<String> txt = ListUtil.toList("txt", "html","out","text");
                if (img.contains(exName.toLowerCase())) {
                    response.setContentType("image/" + exName);
                } else if (video.contains(exName.toLowerCase())) {
                    response.setContentType("video/" + exName);
                } else if ("pdf".equals(exName.toLowerCase())) {
                    response.setContentType("application/pdf");
                } else if (txt.contains(exName.toLowerCase())) {

                } else {
                    response.addHeader("Content-Disposition",
                            "attachment;filename=" + byId.getFileName());
                }
                byte[] buf = new byte[1024];
                InputStream in = new FileInputStream(file);

                int L;
                while ((L = in.read(buf)) != -1) {
                    // if (buf.length != 0)
                    // {
                    toClient.write(buf, 0, L);
                    // }
                }
                in.close();
                // 写完以后关闭文件流
                toClient.flush();
                toClient.close();
                // response.getOutputStream().write(bos.toByteArray());
            } else {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "找不到相关资源");
            }

        } catch (ClientAbortException e) {

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /* @ApiOperation("上传文件夹")
     @PostMapping("/uploadFolder")
     public ResponseBean uploadFolder(@RequestParam("file") MultipartFile[] myfiles, @RequestHeader("token") String token, String filePath, HttpServletRequest request) throws IOException, ClassNotFoundException {
         log.info("上传文件夹");
         String rootPath = PathUtils.getSystemPath();
         if (myfiles.length == 0) {
             return ResponseBean.error(400, "文件上传失败:未能获取到文件");
         }
         *//*if ("".equals(filePath) || filePath == null) {
            filePath = "/";
        }*//*
        if ("/".equals(filePath) || filePath == null || "".equals(filePath)) {
            filePath = "/";
            rootPath = rootPath + separator;
        } else {
            filePath = filePath + "/";
            rootPath = rootPath + filePath + separator;
        }
        //顶层文件夹  如果已存在直接抛出异常
        String[] split = myfiles[0].getOriginalFilename().split("/");
        int i = fileService.selectDirNum(split[0], filePath);
        if (i != -1) {
            throw new FileException(FileEnum.FILENAME_ALEADRY_EXIST);
        }

        List<File> fileList = new ArrayList<>();
        List<File> dirList = new ArrayList<>();
        for (MultipartFile myfile : myfiles) {
            if (myfile.isEmpty()) {
                throw new FileException(FileEnum.FILE_UPLOAD_FAIL);
            }

            File file = new File();
            //原始文件名
            String fileName = myfile.getOriginalFilename();
            //扩展名
            String extFilename = fileName.substring(fileName.lastIndexOf(".") + 1);
            // 原始文件名的父文件夹目录
            String path = fileName.substring(0, fileName.lastIndexOf("/") + 1);

            String fileUrl = rootPath + path + TimeUtils.getFileName() + "." + extFilename;
            java.io.File file1 = new java.io.File(fileUrl);
            java.io.File parentFile = file1.getParentFile();

            createFolder(parentFile, dirList, filePath);
            if (!file1.exists()) {
                myfile.transferTo(file1);
            }
            file.setFilePath(filePath + path);
            file.setFileUrl(file1.getAbsolutePath());
            file.setExtName(extFilename);
            file.setFileSize(myfile.getSize());
            file.setIsDir(0);
            file.setFileName(fileName.substring(fileName.lastIndexOf("/") + 1));
            fileList.add(file);
        }
        String username = JWTUtil.getUsername(token);
        User user = getUser(username);
        for (File file : dirList) {
            file.setFileSize(FileUtil.getTotalSizeOfFilesInDir(new java.io.File(file.getFileUrl())));
        }
        fileList.addAll(dirList);
        Iterator<File> iterator = fileList.iterator();
        while (iterator.hasNext()) {
            iterator.next().setUserId(user.getId());
        }
        fileService.insertFolder(fileList);
        //  fileList.forEach(System.out::println);
        String[] msg = {username, filePath + split[0]};
        LogUtil.log(request, "uploadFolder", msg);
        return ResponseBean.success(null, "上传成功");
    }

    public void createFolder(java.io.File file, List<File> dirList, String filePath) {
        //先判断文件夹是否已经存在 如果存在直接返回
        if (file.exists()) {
            return;
        } else {

            //如果不存在继续向下递归 直接找到存在的顶层文件夹
            java.io.File parentFile = file.getParentFile();
            createFolder(parentFile, dirList, filePath);
            //创建文件夹 以及数据库对象File
            file.mkdir();
            File dir = new File();
            dir.setIsDir(1);
            //  String dirpath = parentFile.getPath().replaceAll("\\\\","/");
            String s;
            // /root/fileManage/39王加淳
            // /root/fileManage/39王加淳/img
            // /root/fileManage/39王加淳/39王加淳
            String path = parentFile.getAbsolutePath().replace(PathUtils.getSystemPath() + filePath.substring(0, filePath.length() - 1), "");

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
    }*/
    @ApiOperation("大文件分片上传")
    @PostMapping("/uploadFloder")
    public void fileChunkUpload(MultipartFileParam param, HttpServletRequest request, HttpServletResponse response, String filePath, @RequestHeader("token") String token) {


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
        java.io.File newFile = null;
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
                String relativePath = param.getRelativePath();
                String substring = relativePath.substring(0, relativePath.lastIndexOf("/"));
                String path = root + "/" + substring;
                java.io.File parentFile = new java.io.File(path);
                List<com.hzvtc1063.filemanage.entity.File> dirList = new ArrayList<>();
                createFolder(parentFile, dirList, filePath);
                String username = JWTUtil.getUsername(token);
                materialService.chunkUploadByMappedByteBuffer(param, path, filePath,request,username);//service层
                newFile = new java.io.File(parentFile.getAbsolutePath() + "/" + param.getFile().getOriginalFilename());

                User user = getUser(username);
                // 文件路径
                for (int i = 0; i < dirList.size(); i++) {
                    dirList.get(i).setFileSize(FileUtil.getTotalSizeOfFilesInDir(new java.io.File(dirList.get(i).getFileUrl())));
                    dirList.get(i).setUserId(user.getId());
                }
                //dirList.add(file);
                fileService.insertFolder(dirList);
                response.setStatus(200);
                response.getWriter().print("上传成功");
            }
            response.getWriter().flush();
        } catch (Exception e) {
            e.printStackTrace();
            if (newFile.exists()) {
                newFile.delete();
            }
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
            String path = parentFile.getAbsolutePath().replaceAll("\\\\", "/").replace(PathUtils.getSystemPath().replaceAll("\\\\", "/") + filePath.substring(0, filePath.length() - 1), "");

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
            if (!dirList.contains(dir)) {
                dirList.add(dir);
            }
        }
        return;
    }


    @ApiOperation("文件重命名")
    @PostMapping("/reName")
    public ResponseBean reName(@Valid @RequestBody RenameDto renameDto, BindingResult bindingResult, @RequestHeader("token") String token, HttpServletRequest request) throws IOException, ClassNotFoundException {
        log.info("重命名文件夹");
        if (bindingResult.hasErrors()) {
            log.info("前端信息有误");
            return ResponseBean.error(400, bindingResult.getAllErrors().get(0).getDefaultMessage());
        }
        String oldName = fileService.reName(renameDto, token);

        String[] msg = {JWTUtil.getUsername(token), oldName, renameDto.getFileName()};
        LogUtil.log(request, "reName", msg);
        return ResponseBean.success(null, "更改成功");
    }

    @ApiOperation("查询文件列表")
    @GetMapping("/getFileList")
    public ResponseBean getFileList(Long id
            , String fp,
                                    HttpServletRequest request) throws IOException, ClassNotFoundException {
        String token = request.getHeader("token");
        log.info("查询文件列表");
        String filePath;
        if (id != null) {
            File file = fileService.getById(id);
            filePath = file.getFilePath() + file.getFileName() + "/";
        } else {
            if ("/".equals(fp) || fp == null || "".equals(fp)) {
                filePath = "/";
            } else {
                filePath = fp + "/";
            }
        }

        // filePath=file.getFilePath()
        List<FileVO> fileVOList;
        int code = 204;
        if (token == null || "".equals(token)) {
            fileVOList = fileService.findFileListByfilePath(filePath, 0L);
        } else {
            code = 200;
            String username = JWTUtil.getUsername(token);

            User user = getUser(username);
            String cachetoken = (String) redisTemplate.opsForValue().get(user.getUserName() + ":" + user.getPassword() + ":token");

            if (cachetoken == null) {
                code = 10086;
                fileVOList = fileService.findFileListByfilePath(filePath, 0L);
            } else {
                fileVOList = fileService.findFileListByfilePath(filePath, user.getId());
            }
        }
        if (code == 10086) {
            return new ResponseBean(code, "登录信息失效", fileVOList);
        }
        return new ResponseBean(code, "null", fileVOList);
    }

    @ApiOperation("条件查询")
    @GetMapping("/selectByFileName")
    public ResponseBean selectByFileName(String fileName, @RequestHeader("token") String token) throws IOException, ClassNotFoundException {
        log.info("根据文件名查找");
        List<FileVO> fileVOList;
        if ("".equals(fileName) || null == fileName) {
            if (token == null || "".equals(token)) {
                fileVOList = fileService.findFileListByfilePath("/", 0L);
            } else {
                String username = JWTUtil.getUsername(token);
                User user = getUser(username);
                fileVOList = fileService.findFileListByfilePath("/", user.getId());
            }

        } else {
            if (token == null || "".equals(token)) {
                fileVOList = fileService.selectByFileName(fileName, 0L);
            } else {
                String username = JWTUtil.getUsername(token);
                User user = getUser(username);
                fileVOList = fileService.selectByFileName(fileName, user.getId());
            }
        }
        return ResponseBean.success(fileVOList, null);
    }

    @ApiOperation("删除文件")
    @PostMapping("/deleteFile")                 //传入id
    public ResponseBean deleteFile(@RequestBody File upFile, @RequestHeader("token") String token, HttpServletRequest request) {
        log.info("删除文件");
        File file = fileService.getById(upFile.getId());
        if (file.getIsDir() == 0) {
            fileService.deleteFile(file);
        } else {
            fileService.deleteFolder(file);
        }

        List<File> fileList = updateFolder(file.getFileUrl());
        if (fileList != null) {
            fileService.updateFolder(fileList);
        }
        String username = JWTUtil.getUsername(token);
        String[] msg = {username, file.getFilePath() + file.getFileName()};
        LogUtil.log(request, "delete", msg);
        return ResponseBean.success(null, "删除成功");
    }

    public List<File> updateFolder(String fileurl) {

        String replace = fileurl.replace(PathUtils.getSystemPath(), "").replace(separator, "/");

        String[] split = replace.split("/");
        //  /root/fileManage/39王加淳/css/2020_12_22_1712224970.map
        // d:\myfileabc\截图\2020_12_23_330284929.PNG
        // d:\myfileabc\截图
        //  /root/fileManage/39/abc.jpg
        List<File> dirList = new ArrayList<>();
        if (split.length <= 2) {
            return dirList;
        }
        java.io.File file = new java.io.File(fileurl);
        updateFolder2(file.getParentFile(), dirList);
        return dirList;

    }

    public void updateFolder2(java.io.File file, List<File> list) {
        if (PathUtils.getSystemPath().equals(file.getAbsolutePath())) {
            return;
        }
        updateFolder2(file.getParentFile(), list);
        File dir = fileService.selectDirByUrl(file.getAbsolutePath());
        dir.setFileSize(FileUtil.getTotalSizeOfFilesInDir(file));
        list.add(dir);
    }

    public User getUser(String username) throws IOException, ClassNotFoundException {
        byte[] b = (byte[]) redisTemplate.opsForValue().get(username);
        User user = (User) SerializeUtil.deserialize(b);
        return user;
    }
}

