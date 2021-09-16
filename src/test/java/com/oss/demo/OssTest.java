package com.oss.demo;

import com.oss.demo.properties.OssProperties;
import com.oss.demo.system.OssComponent;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;

/**
 * @Date 2021/8/12 19:31
 * @Authoer yangcheng
 * @Version 1.0
 */

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class OssTest {
    @Autowired
    private OssProperties ossProperties;

    @Autowired
    private OssComponent ossComponent;

    @Test
    public void testpro(){
        log.info("{}",ossProperties);
    }


    @Test
    public void saveFile() throws IOException {
        File file = new File("d:/2.txt");
        if(!file.exists()){
            file.createNewFile();
        }
        FileWriter fw = new FileWriter(file);
        fw.append("hello oss");
        fw.close();;

        FileInputStream fis = new FileInputStream(file) ;
        String simpleUpload = ossComponent.simpleUpload(fis, "txt", "dir3/");
        log.info(simpleUpload);
        String resourceUrl = ossComponent.resourceUrl(simpleUpload);
        log.info("https://"+resourceUrl);
    }

    @Test
    public void saveStringFile() {
        String filePath = ossComponent.uploadString("{\"test\":1}", "test",  "fileName.json");
        log.info("保存的路径"+filePath);
        String resourceUrl = ossComponent.resourceUrl(filePath);
        log.info("https://"+resourceUrl);
    }

    @Test
    public void saveUrlFile(){
       String url="https://yangcheng-nengyu.oss-cn-beijing.aliyuncs.com/oss/dir1/test/fileName.json?versionId=CAEQERiBgMDX9JDp2RciIDQ0YjVhNWRiMDQyMjQ5NjA5ODdmZGE4NGRkYjdiYmM4";
        String filePath = ossComponent.uploadUrl(url, "txt",  "urlfile/");
        log.info(filePath);
        String resourceUrl = ossComponent.resourceUrl(filePath);
        log.info("https://"+resourceUrl);

    }

    @Test
    public void downloadFile(){
        //上传后相应,返回对应的地址
        //自动保存对应的本地磁盘
        ossComponent.getObject("oss/dir1/urlfile/2041dcf1c1b5486bb60937e4c1f15ad8.txt",new File("d:/1.txt"));
    }


}
