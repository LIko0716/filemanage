package com.hzvtc1063.filemanage.mapper;

import com.hzvtc1063.filemanage.entity.File;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author 1063
 * @since 2020-12-09
 */
public interface FileMapper extends BaseMapper<File> {

    int selectNum(String str,String filePath);

    int selectDirNum(String str,String filePath);
}
