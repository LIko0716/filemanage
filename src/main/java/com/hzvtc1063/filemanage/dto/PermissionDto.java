package com.hzvtc1063.filemanage.dto;

import lombok.Data;

/**
 * @author hangzhi1063
 * @date 2020/12/27 14:26
 */
@Data
public class PermissionDto {
    private Long id;
    private String permissionName;
    private boolean status;
}
