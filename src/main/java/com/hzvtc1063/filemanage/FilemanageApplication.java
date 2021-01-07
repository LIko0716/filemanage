package com.hzvtc1063.filemanage;

import com.hzvtc1063.filemanage.service.FileService;
import com.hzvtc1063.filemanage.utils.PathUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.io.File;
import java.io.IOException;

@SpringBootApplication
@EnableTransactionManagement
public class FilemanageApplication extends SpringBootServletInitializer {

    public static void main(String[] args) {
        ConfigurableApplicationContext run = SpringApplication.run(FilemanageApplication.class, args);
        File file =new File(PathUtils.getSystemPath());
        if (!file.exists()){
            file.mkdirs();
        }
        FileService fileService = (FileService) run.getBean("fileServiceImpl");
        File log =new File(PathUtils.getSystemPath() + "/"+"log.out");
        if (!log.exists()){
            try {
                log.createNewFile();
                com.hzvtc1063.filemanage.entity.File file1 =new com.hzvtc1063.filemanage.entity.File();
                file1.setFileSize(log.length());
                file1.setFileUrl(log.getAbsolutePath());
                file1.setIsDir(1);
                file1.setFilePath("/");
                file1.setExtName("out");
                file1.setFileName("log");
                fileService.insert(file1);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }

/*    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(FilemanageApplication.class);
    }*/

}
