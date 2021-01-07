package com.hzvtc1063.filemanage.controller;


import com.hzvtc1063.filemanage.Result.ResponseBean;
import com.hzvtc1063.filemanage.dto.MaskDto;
import com.hzvtc1063.filemanage.service.MaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author 1063
 * @since 2020-12-09
 */
@RestController
@RequestMapping("/mask")
public class MaskController {

    @Autowired
    private MaskService maskService;
    @PostMapping("/addMask")
    public ResponseBean addMask(@RequestBody MaskDto maskDto){
     boolean b=   maskService.addMask(maskDto);
     return ResponseBean.success(null,"添加成功");
    }
}

