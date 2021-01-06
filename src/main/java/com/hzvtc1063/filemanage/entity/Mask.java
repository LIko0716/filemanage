package com.hzvtc1063.filemanage.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
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
@EqualsAndHashCode(callSuper = false)
public class Mask implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     *  主键
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    private Long fileId;

    private Long userId;


}
