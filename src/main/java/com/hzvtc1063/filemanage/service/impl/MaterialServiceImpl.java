package com.hzvtc1063.filemanage.service.impl;

import com.hzvtc1063.filemanage.entity.MultipartFileParam;
import com.hzvtc1063.filemanage.mapper.FileMapper;
import com.hzvtc1063.filemanage.service.FileService;
import com.hzvtc1063.filemanage.service.MaterialService;
import com.hzvtc1063.filemanage.utils.LogUtil;
import com.hzvtc1063.filemanage.utils.PathUtils;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @author hangzhi1063
 * @date 2020/12/25 14:41
 */
@Service
public class MaterialServiceImpl implements MaterialService{
    @Autowired
    private FileMapper fileMapper;

    @Override
    public String chunkUploadByMappedByteBuffer(MultipartFileParam param, String filePath, String logicPath, HttpServletRequest request,String username) throws IOException, NoSuchMethodException, InvocationTargetException, IllegalAccessException{
        if(param.getTaskId() == null || "".equals(param.getTaskId())){
            param.setTaskId(UUID.randomUUID().toString());
        }
        /**
         *
         * 1：创建临时文件，和源文件一个路径
         * 2：如果文件路径不存在重新创建
         */
        String fileName = param.getFile().getOriginalFilename();
        String tempFileName = param.getTaskId() + fileName.substring(fileName.lastIndexOf(".")) + "_tmp";
        File fileDir = new File(filePath);
        if(!fileDir.exists()){
            fileDir.mkdirs();
        }
        File tempFile = new File(filePath,tempFileName);
        //第一步
        RandomAccessFile raf = new RandomAccessFile(tempFile,"rw");
        //第二步
        FileChannel fileChannel = raf.getChannel();
        //第三步 计算偏移量
        long position = (param.getChunkNumber()-1) * param.getChunkSize();
        //第四步
        byte[] fileData = param.getFile().getBytes();
        //第五步
        long end=position+fileData.length-1;
        fileChannel.position(position);
        fileChannel.write(ByteBuffer.wrap(fileData));
        //使用 fileChannel.map的方式速度更快，但是容易产生IO操作，无建议使用
//        MappedByteBuffer mappedByteBuffer = fileChannel.map(FileChannel.MapMode.READ_WRITE,position,fileData.length);
//        //第六步
//        mappedByteBuffer.put(fileData);
        //第七步
//       freedMappedByteBuffer(mappedByteBuffer);
//        Method method = FileChannelImpl.class.getDeclaredMethod("unmap", MappedByteBuffer.class);
//        method.setAccessible(true);
//        method.invoke(FileChannelImpl.class, mappedByteBuffer);
        fileChannel.force(true);
        fileChannel.close();
        raf.close();
        //第八步
        boolean isComplete = checkUploadStatus(param,fileName,filePath);
        if(isComplete){
            renameFile(tempFile,fileName);
            File newFile =new File(tempFile.getParent() + File.separatorChar + fileName);
            com.hzvtc1063.filemanage.entity.File file =new com.hzvtc1063.filemanage.entity.File();
            file.setFileName(newFile.getName());
            file.setFileSize(newFile.length());
            file.setExtName(newFile.getName().substring(newFile.getName().lastIndexOf(".")+1));
            file.setFileUrl(newFile.getAbsolutePath());
            String relativePath = param.getRelativePath();
            file.setFilePath(logicPath+relativePath.substring(0,relativePath.lastIndexOf("/")+1));
            file.setIsDir(0);
            fileMapper.insert(file);
            //java.io.File parentFile = new java.io.File(filePath);
            String[] msg = {username, fileName};
            LogUtil.log(request, "upload", msg);
        }
        return param.getTaskId();
    }

    public void createFolder(java.io.File file, List<com.hzvtc1063.filemanage.entity.File> dirList, String filePath) {
        //先判断文件夹是否已经存在 如果存在直接返回
        if (file.exists()) {
            return;
        } else {
            String separator =File.separator;
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

    /**
     * 文件重命名
     * @param toBeRenamed   将要修改名字的文件
     * @param toFileNewName 新的名字
     * @return
     */
    public void renameFile(File toBeRenamed, String toFileNewName) {
        //检查要重命名的文件是否存在，是否是文件
        if (!toBeRenamed.exists() || toBeRenamed.isDirectory()) {
            System.out.println("文件不存在");
            return;
        }
        String p = toBeRenamed.getParent();
        File newFile = new File(p + File.separatorChar + toFileNewName);
        //修改文件名
        toBeRenamed.renameTo(newFile);
    }

    /**
     * 检查文件上传进度
     * @return
     */
    public boolean checkUploadStatus(MultipartFileParam param,String fileName,String filePath) throws IOException {
        File confFile = new File(filePath,fileName+".conf");
        RandomAccessFile confAccessFile = new RandomAccessFile(confFile,"rw");
        //设置文件长度
        confAccessFile.setLength(param.getTotalChunks());
        //设置起始偏移量
        confAccessFile.seek(param.getChunkNumber()-1);
        //将指定的一个字节写入文件中 127，
        confAccessFile.write(Byte.MAX_VALUE);
        byte[] completeStatusList = FileUtils.readFileToByteArray(confFile);
        confAccessFile.close();//不关闭会造成无法占用
        //这一段逻辑有点复杂，看的时候思考了好久，创建conf文件文件长度为总分片数，每上传一个分块即向conf文件中写入一个127，那么没上传的位置就是默认的0,已上传的就是Byte.MAX_VALUE 127
        for(int i = 0; i<completeStatusList.length; i++){
            if(completeStatusList[i]!=Byte.MAX_VALUE){
                return false;
            }
        }
        //如果全部文件上传完成，删除conf文件
        confFile.delete();
        return true;
    }

    /**
     * 在MappedByteBuffer释放后再对它进行读操作的话就会引发jvm crash，在并发情况下很容易发生
     * 正在释放时另一个线程正开始读取，于是crash就发生了。所以为了系统稳定性释放前一般需要检 查是否还有线程在读或写
     * @param mappedByteBuffer
     */
    public static void freedMappedByteBuffer(final MappedByteBuffer mappedByteBuffer) {
        try {
            if (mappedByteBuffer == null) {
                return;
            }
            mappedByteBuffer.force();
            AccessController.doPrivileged(new PrivilegedAction<Object>() {
                @Override
                public Object run() {
                    try {
                        Method getCleanerMethod = mappedByteBuffer.getClass().getMethod("cleaner", new Class[0]);
                        //可以访问private的权限
                        getCleanerMethod.setAccessible(true);
                        //在具有指定参数的 方法对象上调用此 方法对象表示的底层方法
                        sun.misc.Cleaner cleaner = (sun.misc.Cleaner) getCleanerMethod.invoke(mappedByteBuffer,
                                new Object[0]);
                        cleaner.clean();
                    } catch (Exception e) {
                        e.printStackTrace();
                        System.out.println("清理缓存出错!!!"+e.getMessage());
                    }
                    System.out.println("缓存清理完毕!!!");
                    return null;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
