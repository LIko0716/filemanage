package com.hzvtc1063.filemanage;


import cn.hutool.core.collection.ListUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hzvtc1063.filemanage.entity.User;
import com.hzvtc1063.filemanage.enums.UserEnum;
import com.hzvtc1063.filemanage.exception.UserException;
import com.hzvtc1063.filemanage.mapper.FileMapper;
import com.hzvtc1063.filemanage.mapper.UserMapper;
import com.hzvtc1063.filemanage.service.FileService;
import com.hzvtc1063.filemanage.utils.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.io.*;
import java.util.*;
import java.util.concurrent.TimeUnit;

@SpringBootTest
@EnableTransactionManagement
class FilemanageApplicationTests {

    @Autowired
    private FileService fileService;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private RedisTemplate<Object, Object> redisTemplate;

    @Test
    public void ddd() throws FileNotFoundException {

    }

    @Test
    public void page(){
       String fileName ="asdada.txt.dsd.sd";
        System.out.println(fileName.substring(0, fileName.indexOf(".")));
    }
    private long getTotalSizeOfFilesInDir(File file) {
        if (file.isFile())
            return file.length();
        final File[] children = file.listFiles();
        long total = 0;
        if (children != null)
            for (File child : children)
                total += getTotalSizeOfFilesInDir(child);
        return total;
    }

    @Test
    public void test() {
        String filePath = "/39王加淳/";
        System.out.println(filePath.substring(0, filePath.length() - 1));

    }

    @Test
    public void testJwt() throws UnsupportedEncodingException {
        long l = System.currentTimeMillis();
        User user = new User();
        user.setUserName("zhangsan");
        user.setPassword("123");
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("user_name", user.getUserName());
        User mUser = userMapper.selectOne(wrapper);
        if (mUser == null) {

            throw new UserException(UserEnum.USER_NOTEXIST);
        }

        if (!user.getPassword().equals(mUser.getPassword())) {
            throw new UserException(UserEnum.PASSWORD_NOTSAME);
        }
        String token = JWTUtil.sign(user.getUserName(), user.getPassword());
        long l1 = System.currentTimeMillis();
        System.out.println(l1 - l);
        redisTemplate.opsForValue().set(user.getUserName() + ":" + user.getPassword() + ":token", token, 30, TimeUnit.MINUTES);
        byte[] serializeUser = SerializeUtil.serialize(user);
        redisTemplate.opsForValue().set(user.getUserName(), serializeUser);


        System.out.println(l1 - l);
    }

    /* @Autowired
     private RedisTemplate<Object, Object> redisTemplate;

     @Autowired
     private FileMapper fileMapper;
    *//* @Test
    public void test(){

        String newFilename="test";
        String oldName="39王加淳";

        QueryWrapper wrapper =new QueryWrapper();
        wrapper.likeRight("file_path","/39王加淳/");
        List<File> fileList = fileMapper.selectList(wrapper);
        for (File file : fileList) {
            String s = file.getFilePath().replaceFirst(oldName, newFilename);
            System.out.println(s);
        }

    }*//*

    @Test
    public void testuploadtxt() throws IOException {
        java.io.File file =new java.io.File("classes/static/test.txt");
        // int read = resourceAsStream.read();
        FileOutputStream fop =new FileOutputStream(file);
        fop.write("hhhhhhhhhhhasdhhc".getBytes());
        fop.flush();
        fop.close();
        System.out.println(file.getName());
        String[] upload = FastDFSUtils.upload();
        for (String s : upload) {
            System.out.println(s);
        }
    }

    @Test
    public void test(){
        List<String> list = new ArrayList<>();
        list.add("aaa");
        list.add("aaa");
        list.add("aaa");
        list.add("aaa");
        list.add("aaa");
        for (int i=0;i<10;i++){
            if (list.size()-i>0){
                System.out.println(list.get(i));
            }
        }
    }

    @Test
    public void stesd(){

    }*/
    @Test
    public void test2() {
    }
}
