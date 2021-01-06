package com.hzvtc1063.filemanage.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * @author hangzhi1063
 * @date 2020/12/14 9:14
 */
@Data
public class RenameDto  implements Serializable {

    Long id;
    String oldName;
    @NotBlank(message = "您没有输入新的文件名")
    String fileName;
}
