package com.hzvtc1063.filemanage;

import com.hzvtc1063.filemanage.utils.PathUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.io.File;

@SpringBootApplication
@EnableTransactionManagement
public class FilemanageApplication extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(FilemanageApplication.class, args);
        File file =new File(PathUtils.getSystemPath());
        if (!file.exists()){
            file.mkdirs();
        }
        
    }

/*    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(FilemanageApplication.class);
    }*/

}
