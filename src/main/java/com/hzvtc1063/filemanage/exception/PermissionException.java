package com.hzvtc1063.filemanage.exception;

import com.hzvtc1063.filemanage.enums.PermissionEnum;

/**
 * @author hangzhi1063
 * @date 2020/12/10 10:37
 */
public class PermissionException extends RuntimeException{

    public PermissionException(){

    }
    public PermissionException(PermissionEnum permissionEnum){
        super(permissionEnum.getMsg());
    }
}
