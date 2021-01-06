package com.hzvtc1063.filemanage.service;

import com.hzvtc1063.filemanage.entity.Permission;
import com.baomidou.mybatisplus.extension.service.IService;
import com.hzvtc1063.filemanage.vo.PermissionVO;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author 1063
 * @since 2020-12-09
 */
public interface PermissionService extends IService<Permission> {

    PermissionVO getPermissionList(Integer currentPage);
}
