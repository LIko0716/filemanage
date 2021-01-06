package com.hzvtc1063.filemanage.enums;

import com.hzvtc1063.filemanage.entity.Permission;
import lombok.Data;
import lombok.Getter;

/**
 * @author hangzhi1063
 * @date 2020/12/10 10:39
 */
@Getter
public enum  PermissionEnum {
    PERMISSION_NOTENOUGH("权限不足");

   private String msg;
    PermissionEnum(String msg){
        this.msg =msg;
    }
}
