package com.hzvtc1063.filemanage.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author hangzhi1063
 * @date 2020/12/10 13:46
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FileVO {

    @JsonSerialize(using = com.fasterxml.jackson.databind.ser.std.ToStringSerializer.class)
    private Long id;

    private String fileName;

    private String fileSize;

    private Date updateTime;

    private Integer isDir;


}
