package com.hzvtc1063.filemanage.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author hangzhi1063
 * @date 2020/12/27 10:11
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDetailVO implements Serializable {

    @JsonSerialize(using = com.fasterxml.jackson.databind.ser.std.ToStringSerializer.class)
    private Long id;

    private String userName;

    private String email;

    private boolean bstatus;

    private String role;
}
