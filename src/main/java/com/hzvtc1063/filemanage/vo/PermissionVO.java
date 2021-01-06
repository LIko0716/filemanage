package com.hzvtc1063.filemanage.vo;

import lombok.Data;

import java.util.List;

/**
 * @author hangzhi1063
 * @date 2020/12/27 14:32
 */
@Data
public class PermissionVO {

    private Long totalSize;

    private List<PermissionDetailVO> PermissionDetail;
}
