package com.hzvtc1063.filemanage.service;

import com.hzvtc1063.filemanage.dto.RenameDto;
import com.hzvtc1063.filemanage.vo.FileVO;
import com.hzvtc1063.filemanage.entity.File;
import com.baomidou.mybatisplus.extension.service.IService;

import java.io.IOException;
import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author 1063
 * @since 2020-12-09
 */
public interface FileService extends IService<File> {

    List<FileVO> findFileListByfilePath(String filePath,Long userId);
    //List<FileVO> findFileListByfilePath(String filePath);

    File selectOne(String fileName);

    int selectNum(String fileName, String extFilename,String filePath);

    boolean insert(File file);

    void deleteFile(File file);

    void insertFolder(List<File> fileList);

    int selectDirNum(String foldName,String filePath);

    String reName(RenameDto renameDto,String token) throws IOException, ClassNotFoundException;

    List<FileVO> selectByFileName(String fileName, long l);

    void deleteFolder(File file);

    File selectDirByUrl(String absolutePath);

    void updateFolder(List<File> fileList);
}
