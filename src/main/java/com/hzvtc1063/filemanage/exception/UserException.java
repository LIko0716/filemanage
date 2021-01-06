package com.hzvtc1063.filemanage.exception;

import com.hzvtc1063.filemanage.enums.UserEnum;

/**
 * @author hangzhi1063
 * @date 2020/12/9 19:18
 */

public class UserException extends RuntimeException{


   public UserException(){
      super();
   }
   public UserException(UserEnum userEnum){
        super(userEnum.getMsg());
   }
}
