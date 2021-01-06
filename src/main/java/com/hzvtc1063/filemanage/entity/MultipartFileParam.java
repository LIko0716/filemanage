package com.hzvtc1063.filemanage.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author hangzhi1063
 * @date 2020/12/25 14:34
 */
@Data
@ApiModel("大文件分片入参实体")
public class MultipartFileParam {
    @ApiModelProperty("文件传输任务ID")
    private String taskId;

    @ApiModelProperty("当前为第几分片")
    private int chunkNumber;

    @ApiModelProperty("每个分块的大小")
    private long chunkSize;


    @ApiModelProperty("分片总数")
    private int totalChunks;
    @ApiModelProperty("文件唯一标识")
    private String identifier;
    private String relativePath;
    private Object href;

    @ApiModelProperty("分块文件传输对象")
    private MultipartFile file;


}

