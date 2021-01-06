package com.hzvtc1063.filemanage.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;

/**
 * @author hangzhi1063
 * @date 2020/12/27 14:33
 */
@Data
public class PermissionDetailVO {
    @JsonSerialize(using = com.fasterxml.jackson.databind.ser.std.ToStringSerializer.class)
    private Long id;
    private String userName;
    private boolean rname=false;
    private boolean del=false;
    private boolean upload=false;
    private boolean mkdir=false;
}
