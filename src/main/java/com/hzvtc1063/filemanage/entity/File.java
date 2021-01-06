package com.hzvtc1063.filemanage.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import java.util.Objects;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 
 * </p>
 *
 * @author 1063
 * @since 2020-12-09
 */
@Data
public class File implements Serializable {
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        File file = (File) o;
        return Objects.equals(fileName, file.fileName) &&
                Objects.equals(filePath, file.filePath) &&
                Objects.equals(isDir, file.isDir);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fileName, filePath, isDir);
    }

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 原文件名
     */
    private String fileName;

    /**
     * 扩展名
     */
    private String extName;

    /**
     * 逻辑路径
     */
    private String filePath;

    /**
     * 文件大小
     */
    private Long fileSize;

    /**
     * 远程地址
     */
    private String fileUrl;


    /**
     * 是否是文件夹
     */
    private Integer isDir;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    /**
     * 修改时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

    /**
     * 用户id
     */
    private Long userId;


}
