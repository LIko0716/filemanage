package com.hzvtc1063.filemanage.exception;

/**
 * @author hangzhi1063
 * @date 2020/12/25 11:52
 */
public class NotSameFileExpection extends Exception{
    public NotSameFileExpection() {
        super("File MD5 Different");
    }
}
