package com.hzvtc1063.filemanage.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @author hangzhi1063
 * @date 2020/12/15 13:27
 */
@Data
public class TxtDto implements Serializable {
    String fileName;
    String content;
    String filePath;
}
