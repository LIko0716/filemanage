package com.hzvtc1063.filemanage.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author hangzhi1063
 * @date 2020/12/27 21:36
 */
@Data
public class UpUserDto {
    @NotBlank(message = "用户名不能为空")
    private String userName;

    private Long id;
    private String role;
}
