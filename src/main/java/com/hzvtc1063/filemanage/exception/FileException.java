package com.hzvtc1063.filemanage.exception;

import com.hzvtc1063.filemanage.enums.FileEnum;

/**
 * @author hangzhi1063
 * @date 2020/12/10 14:01
 */
public class FileException  extends RuntimeException{

    public FileException(){

    }
    public FileException(FileEnum fileEnum){
        super(fileEnum.getMsg());
    }
}
