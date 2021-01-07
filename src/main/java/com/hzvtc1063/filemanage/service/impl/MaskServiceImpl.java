package com.hzvtc1063.filemanage.service.impl;

import com.hzvtc1063.filemanage.dto.MaskDto;
import com.hzvtc1063.filemanage.entity.Mask;
import com.hzvtc1063.filemanage.mapper.MaskMapper;
import com.hzvtc1063.filemanage.service.MaskService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author 1063
 * @since 2020-12-09
 */
@Service
public class MaskServiceImpl extends ServiceImpl<MaskMapper, Mask> implements MaskService {

    @Autowired
    private MaskMapper maskMapper;
    @Override
    public boolean addMask(MaskDto maskDto) {
        Mask mask =new Mask();
        mask.setUserId(maskDto.getUserId());
        mask.setFileId(maskDto.getFileId());
        int insert = maskMapper.insert(mask);
        return insert>0;
    }
}
