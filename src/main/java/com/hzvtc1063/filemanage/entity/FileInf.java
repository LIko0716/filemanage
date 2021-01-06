package com.hzvtc1063.filemanage.entity;

import java.util.Date;

/**
 * @author hangzhi1063
 * @date 2020/12/25 13:43
 */
public class FileInf {
    public FileInf(){}

    public String id="";

    public String pid="";

    public String pidRoot="";

    /**  * 表示当前项是否是一个文件夹项。    */

    public boolean fdTask=false;

    //   /// 是否是文件夹中的子文件  /// </summary>

    public boolean fdChild=false;

    /**  * 用户ID。与第三方系统整合使用。    */

    public int uid=0;

    /**  * 文件在本地电脑中的名称   */

    public String nameLoc="";

    /**  * 文件在服务器中的名称。   */

    public String nameSvr="";

    /**  * 文件在本地电脑中的完整路径。示例：D:\Soft\QQ2012.exe */

    public String pathLoc="";

    /**  * 文件在服务器中的完整路径。示例：F:\\ftp\\uer\\md5.exe     */

    public String pathSvr="";

    /**  * 文件在服务器中的相对路径。示例：/www/web/upload/md5.exe   */

    public String pathRel="";

    /**  * 文件MD5    */

    public String md5="";

    /**  * 数字化的文件长度。以字节为单位，示例：120125    */

    public long lenLoc=0;

    /**  * 格式化的文件尺寸。示例：10.03MB   */

    public String sizeLoc="";

    /**  * 文件续传位置。  */

    public long offset=0;

    /**  * 已上传大小。以字节为单位 */

    public long lenSvr=0;

    /**  * 已上传百分比。示例：10%  */

    public String perSvr="0%";

    public boolean complete=false;

    public Date PostedTime = new Date();

    public boolean deleted=false;

    /**  * 是否已经扫描完毕，提供给大型文件夹使用，大型文件夹上传完毕后开始扫描。  */

    public boolean scaned=false;

}
