package com.oss.demo.system;


import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.common.comm.ResponseMessage;
import com.aliyun.oss.internal.OSSHeaders;
import com.aliyun.oss.model.*;
import com.oss.demo.properties.OssProperties;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Objects;
import java.util.UUID;

/**
 * oss上传及下载主键
 * @Date 2021/8/12 20:15
 * @Authoer yangcheng
 * @Version 1.0
 */
@Slf4j
@Component
public class OssComponent {

    private final OssProperties ossProperties;

    public OssComponent(OssProperties ossProperties) {
        this.ossProperties = ossProperties;
    }


    /**
     * 上传字符串
     *
     * @param content   字符串内容
     * @param directory 上传的目录，以"/"结尾
     * @param fileName  字符串保存的文件名，带扩展名的文件名
     * @return
     */
    public String uploadString(@NonNull String content, @NonNull String directory, @NonNull String fileName) {
        ByteArrayInputStream inputStream = new ByteArrayInputStream(content.getBytes(Charset.forName("UTF-8")));
        String objectName = String.format("%s%s/%s", ossProperties.getDirectory(), directory, fileName);
        this.put(objectName, inputStream);
        return objectName;
    }

    /**
     * 进行文件上传，支持各种形式的流
     *
     * @param inputStream 输入流
     * @param suffix      文件后缀
     * @param dir         存放目录地址。开头不可以“/”开始
     * @return 返回上传后的对象url 地址
     */
    public String simpleUpload(@NonNull InputStream inputStream, @NonNull String suffix, @NonNull String dir) {
        if (Objects.isNull(inputStream)) {
            log.error("通过OSS 流式上传时，输入流为空");
            return "";
        }

        String filename = ossProperties.getDirectory() + dir + uuid() + "." + suffix;
        PutObjectResult result = this.put(filename, inputStream);
        // 从响应结果中获取具体响应消息
        ResponseMessage responseMessage = result.getResponse();

        // 根据响应状态码判断请求是否成功
        if (Objects.isNull(responseMessage)) {
            // 如需返回OSS的完整路径，resourceUrl(filename)
            return filename;
        }
        throw new RuntimeException("上传文件失败");
    }

    public void getObject(@NonNull String objectKey, File file) {
        OSS ossClient = this.buildOss();
        GetObjectRequest request = new GetObjectRequest(ossProperties.getBucket(), objectKey);
        ossClient.getObject(request, file);
        ossClient.shutdown();
    }

    private String uuid() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    /**
     * 上传某个链接地址的内容
     */
    @SneakyThrows(Exception.class)
    public String uploadUrl(@NonNull String url, @NonNull String suffix, @NonNull String dir) {
        return simpleUpload(new URL(url).openStream(), suffix, dir);
    }


    /**
     * 构建请求对象
     *
     * @param filename    文件名
     * @param inputStream 流
     */
    private PutObjectRequest buildRequest(String filename, InputStream inputStream) {
        return new PutObjectRequest(ossProperties.getBucket(), filename, inputStream);
    }

    /**
     * 构建oss 组件
     */
    private OSS buildOss() {
        return new OSSClientBuilder().build(
                ossProperties.getEndpoint(), ossProperties.getAccessKeyId(), ossProperties.getAccessKeySecret());
    }

    /**
     * 设置存储类型与访问权限
     *
     * @param request 文件请求
     */
    private static PutObjectRequest jurisdiction(PutObjectRequest request) {
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setHeader(OSSHeaders.OSS_STORAGE_CLASS, StorageClass.Standard.toString());
        metadata.setObjectAcl(CannedAccessControlList.Private);
        request.setMetadata(metadata);
        return request;
    }

    /**
     * 获取上传到 oss 后文件的链接
     */
    public String resourceUrl(@NonNull String filename) {
        return ossProperties.getBucket() + "." + ossProperties.getEndpoint() + "/" + filename;
    }

    /**
     * 把文件传到  oss
     *
     * @return
     */
    private PutObjectResult put(String filename, InputStream inputStream) {
        OSS ossClient = this.buildOss();
        PutObjectResult putObjectResult = ossClient.putObject(jurisdiction(this.buildRequest(filename, inputStream)));
        ossClient.shutdown();
        return putObjectResult;
    }

    @PostConstruct
    public void init() {
        log.info("aliyun oss component is loading");
    }

}
