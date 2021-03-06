package com.hzvtc1063.filemanage.service;

import com.hzvtc1063.filemanage.entity.MultipartFileParam;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

/**
 * @author hangzhi1063
 * @date 2020/12/25 14:40
 */
public interface MaterialService {
    String chunkUploadByMappedByteBuffer(MultipartFileParam param, String filePath, String logicPath, HttpServletRequest request,String username)throws IOException, NoSuchMethodException, InvocationTargetException, IllegalAccessException;
}
