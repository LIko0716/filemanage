package com.hzvtc1063.filemanage.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

/**
 * @author hangzhi1063
 * @date 2020/12/27 9:37
 */
@Data
public class UserVO  implements Serializable {

    private Long totalSize;

   private List<UserDetailVO> userDetail;
}
