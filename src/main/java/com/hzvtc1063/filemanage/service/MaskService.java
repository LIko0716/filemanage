package com.hzvtc1063.filemanage.service;

import com.hzvtc1063.filemanage.dto.MaskDto;
import com.hzvtc1063.filemanage.entity.Mask;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author 1063
 * @since 2020-12-09
 */
public interface MaskService extends IService<Mask> {

    boolean addMask(MaskDto maskDto);
}
