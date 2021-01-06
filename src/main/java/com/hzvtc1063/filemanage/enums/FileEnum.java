package com.hzvtc1063.filemanage.enums;

import lombok.Getter;

/**
 * @author hangzhi1063
 * @date 2020/12/10 14:01
 */
@Getter
public enum FileEnum{
    FILE_UPLOAD_FAIL("文件上传失败"),
    FILE_NOTEXIST("文件不存在"),
    FILENAME_ALEADRY_EXIST("文件名已存在"),
    FILE_UPDATE_FAIL("文件更新失败"),
    FILE_DEL_FAIL("删除失败");
    private String msg;

    FileEnum(String msg){
        this.msg=msg;
    }
}
