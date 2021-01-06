package com.hzvtc1063.filemanage.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * @author hangzhi1063
 * @date 2020/12/10 16:09
 */
@Data
@EqualsAndHashCode(callSuper = false)

public class DirDto implements Serializable {
    //@NotBlank(message = "无法解析路径")
    private String filePath;
    @NotBlank(message = "文件夹名不能为空")
    private String dirName;
}
