package com.hzvtc1063.filemanage.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hzvtc1063.filemanage.dto.RenameDto;
import com.hzvtc1063.filemanage.entity.File;
import com.hzvtc1063.filemanage.entity.Mask;
import com.hzvtc1063.filemanage.entity.User;
import com.hzvtc1063.filemanage.enums.FileEnum;
import com.hzvtc1063.filemanage.exception.FileException;
import com.hzvtc1063.filemanage.mapper.FileMapper;
import com.hzvtc1063.filemanage.mapper.MaskMapper;
import com.hzvtc1063.filemanage.service.FileService;
import com.hzvtc1063.filemanage.utils.*;
import com.hzvtc1063.filemanage.vo.FileVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.FileSystemUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author 1063
 * @since 2020-12-09
 */
@Service
@Slf4j
public class FileServiceImpl extends ServiceImpl<FileMapper, File> implements FileService {

    @Autowired
    private FileMapper fileMapper;
    @Autowired
    private MaskMapper maskMapper;
    @Autowired
    private RedisTemplate<Object, Object> redisTemplate;

    //如果登录哪些不可见
    @Override
    public List<FileVO> findFileListByfilePath(String filePath, Long userId) {
        QueryWrapper<Mask> maskWrapper = new QueryWrapper<>();
        maskWrapper.eq("user_id", userId);

        List<Mask> masks = maskMapper.selectList(maskWrapper);
        List<Long> fileIds = new ArrayList<>();
        for (Mask mask : masks) {
            fileIds.add(mask.getFileId());
        }
        QueryWrapper<File> wrapper = new QueryWrapper<>();
        wrapper.eq("file_path", filePath);
        if (fileIds.size() != 0) {
            wrapper.notIn("id", fileIds);
        }
        return getFileVOlist(wrapper);
    }

    @Override
    public File selectDirByUrl(String absolutePath) {
        QueryWrapper<File> wrapper = new QueryWrapper<>();
        wrapper.eq("file_url", absolutePath);
        File file = fileMapper.selectOne(wrapper);
        if (file == null) {
            log.info("通过路径查询文件失败");
            throw new FileException(FileEnum.FILE_NOTEXIST);
        }

        return file;
    }

    @Override
    public void updateFolder(List<File> fileList) {

        for (File file : fileList) {
            fileMapper.updateById(file);
        }
    }

    @Override
    public List<FileVO> selectByFileName(String fileName, long userId) {
        QueryWrapper<Mask> maskWrapper = new QueryWrapper<>();
        maskWrapper.eq("user_id", userId);

        List<Mask> masks = maskMapper.selectList(maskWrapper);
        List<Long> fileIds = new ArrayList<>();
        for (Mask mask : masks) {
            fileIds.add(mask.getFileId());
        }
        QueryWrapper<File> wrapper = new QueryWrapper<>();
        wrapper.like("file_name", fileName);
        if (fileIds.size() != 0) {
            wrapper.notIn("id", fileIds);
        }
        List<FileVO> fileVOlist = getFileVOlist(wrapper);
        return fileVOlist;
    }

    @Override
    @Transactional
    public void deleteFolder(File file) {
        log.info("删除文件夹");
        //删除文件夹及其子目录
        //文件夹下的目录
        String path = file.getFilePath() + file.getFileName() + "/";
        QueryWrapper<File> wrapper = new QueryWrapper<>();
        //select * from file where file_path like /xxxx/xxx/%
        wrapper.likeRight("file_path", path);
        List<File> fileList = fileMapper.selectList(wrapper);
        //删除所有
        int count = 0;
        for (File file1 : fileList) {
            count += fileMapper.deleteById(file1);
            java.io.File diskFile = new java.io.File(file1.getFileUrl());
            boolean b = FileSystemUtils.deleteRecursively(diskFile);
        }
        //最后删除父文件夹
        int i = fileMapper.deleteById(file);
        if (i != 1 || count != fileList.size()) {
            throw new FileException(FileEnum.FILE_DEL_FAIL);
        }
        java.io.File diskFile = new java.io.File(file.getFileUrl());
        if (diskFile.exists()) {
            boolean delete = FileSystemUtils.deleteRecursively(diskFile);
            if (!delete) {
                throw new FileException(FileEnum.FILE_DEL_FAIL);
            }
        }
    }

   /* @Override
    public List<FileVO> findFileListByfilePath(String filePath) {
        QueryWrapper<File> wrapper = new QueryWrapper<>();
        wrapper.eq("file_path", filePath);
        if (fileIds.size() != 0) {
            wrapper.notIn("id", fileIds);
        }
        return getFileVOlist(wrapper);
    }*/

    @Override
    public File selectOne(String fileName) {
        QueryWrapper<File> wrapper = new QueryWrapper<>();
        wrapper.eq("file_name", fileName);
        File file = fileMapper.selectOne(wrapper);
        if (file == null) {
            throw new FileException(FileEnum.FILE_NOTEXIST);
        }
        return file;
    }

    @Override
    public int selectNum(String fileName, String extFilename, String filePath) {
        QueryWrapper<File> wrapper = new QueryWrapper<>();
        wrapper.eq("file_name", fileName);
        wrapper.eq("file_path", filePath);
        wrapper.eq("is_dir", 0);
        int count = -1;
        File file = fileMapper.selectOne(wrapper);
        if (file != null) {
            String str = fileName.substring(0, fileName.lastIndexOf("."));
            str = str + "(%)" + "." + extFilename;
            count = fileMapper.selectNum(str, filePath);
        }
        return count;
    }

    @Override
    public int selectDirNum(String foldName, String filePath) {
        QueryWrapper<File> wrapper = new QueryWrapper<>();
        wrapper.eq("file_name", foldName);
        wrapper.eq("file_path", filePath);
        wrapper.eq("is_dir", 1);
        int count = -1;
        File file = fileMapper.selectOne(wrapper);
        if (file != null) {
            String str = foldName + "(%)";
            count = fileMapper.selectDirNum(str, filePath);
        }
        return count;
    }

    @Override
    @Transactional
    public String reName(RenameDto renameDto, String token) throws IOException, ClassNotFoundException {
        String rootPath = PathUtils.getSystemPath();
        String separator = java.io.File.separator;
        /*QueryWrapper<File> wrapper = new QueryWrapper<>();
        wrapper.eq("file_name", renameDto.getOldName());*/
        //File file = fileMapper.selectOne(wrapper);
        File file = fileMapper.selectById(renameDto.getId());
        //文件夹与文件分开判断
        if (file.getIsDir() == 0) {
            //String fileName =renameDto.getFileName();
            String fileName = file.getFileName();
            String extName = fileName.substring(fileName.lastIndexOf(".") + 1);
            //统计重名数量,如果有重名文件抛出异常  /test/test/test
            //int count = fileMapper.selectNum(renameDto.getFileName() + "%." + extName,file.getFilePath());
            int count = selectNum(renameDto.getFileName(), extName, file.getFilePath());
            if (count != -1) {
                throw new FileException(FileEnum.FILENAME_ALEADRY_EXIST);
            }
            //fileName =fileName.substring(0,fileName.lastIndexOf("."))+"("+(count+1)+")"+"."+extName;
            //更改的是哪一个用户
            file.setFileName(renameDto.getFileName().substring(0,renameDto.getFileName().indexOf(".")) + "." + extName);
        } else {
            //是文件夹的话
            int count = selectDirNum(renameDto.getFileName(), file.getFilePath());
            if (count != -1) {
                throw new FileException(FileEnum.FILENAME_ALEADRY_EXIST);
            }
            //更改子目录的路径 path:父路径 /test/test/test  0 ”“ 1 test 2 test 3 test 要改的名字 length -1
            //更改磁盘的文件夹
            String path = file.getFilePath() + file.getFileName() + "/";
            java.io.File diskFile = new java.io.File((rootPath + file.getFilePath() + file.getFileName()).replace("/", separator));
            java.io.File newName = new java.io.File((rootPath + file.getFilePath() + renameDto.getFileName()).replace("/", separator));
            diskFile.renameTo(newName);
            String[] split = path.split("/");
            //查找所有path下的子目录
            QueryWrapper wrapper2 = new QueryWrapper();
            wrapper2.likeRight("file_path", path);
            List<File> fileList = fileMapper.selectList(wrapper2);
            for (File chFile : fileList) {
                //String newPath = chFile.getFilePath().replaceFirst(file.getFileName(), renameDto.getFileName());
                String[] split1 = chFile.getFilePath().split("/");
                split1[split.length - 1] = renameDto.getFileName();
                //拼接回路径
                String newPath = "";
                for (String s : split1) {
                    newPath += s + "/";
                }
                chFile.setFilePath(newPath);
                chFile.setFileUrl(chFile.getFileUrl().replace(diskFile.getPath(), newName.getPath()));
                fileMapper.updateById(chFile);
            }
            file.setFileName(renameDto.getFileName());
            file.setFileUrl(newName.getAbsolutePath());
        }
        String username = JWTUtil.getUsername(token);
        User user = getUser(username);
        file.setUserId(user.getId());
        int i = fileMapper.updateById(file);
        if (i != 1) {
            throw new FileException(FileEnum.FILE_UPDATE_FAIL);
        }
        return file.getFileName();
    }


    @Transactional
    @Override
    public boolean insert(File file) {
        int insert = fileMapper.insert(file);
        return insert > 0;
    }

    @Transactional
    @Override
    public void deleteFile(File file) {
        int result;
        //先删除数据库如果报错就回滚事务
        int updateRow = fileMapper.deleteById(file.getId());
        if (updateRow > 0) {
            boolean delete = false;
            java.io.File diskfile = new java.io.File(file.getFileUrl());
            if (diskfile.exists()) {
                delete = diskfile.delete();
            }
            if (!delete) {
                throw new FileException(FileEnum.FILE_DEL_FAIL);
            } else {
                return;
            }
        }
        throw new FileException(FileEnum.FILE_DEL_FAIL);
    }

    @Override
    @Transactional
    public void insertFolder(List<File> fileList) {
        int i = 0;
        for (File file : fileList) {
            int insert = fileMapper.insert(file);
            i += insert;
        }
        if (i != fileList.size()) {
            throw new FileException(FileEnum.FILE_UPLOAD_FAIL);
        }
    }

    public List<FileVO> getFileVOlist(QueryWrapper wrapper) {
        List<File> files = fileMapper.selectList(wrapper);
        List<FileVO> fileVOList = new ArrayList<>();
        FileVO fileVO = null;
        for (File file : files) {
            fileVO = new FileVO();
            BeanUtils.copyProperties(file, fileVO);
            //fileVO.setRemotePath( "/" + file.getFileUrl());
            Double size = Double.valueOf(file.getFileSize());
            if (size!=null){
                fileVO.setFileSize(FileUtil.getFileSize(size));
            }
            fileVOList.add(fileVO);
        }
        return fileVOList;
    }

    public User getUser(String username) throws IOException, ClassNotFoundException {
        byte[] b = (byte[]) redisTemplate.opsForValue().get(username);
        User user = (User) SerializeUtil.deserialize(b);
        return user;
    }
}
