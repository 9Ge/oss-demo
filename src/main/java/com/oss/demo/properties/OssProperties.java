package com.oss.demo.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @Date 2021/8/12 19:25
 * @Author yangcheng
 * @Version 1.0
 */
@Data
@Configuration
@EnableConfigurationProperties(OssProperties.class)
@ConfigurationProperties(prefix = "oss")
public class OssProperties {
    /**
     * 空间名称
     */
    private String bucket;
    private String accessKeyId;
    private String accessKeySecret;
    private String directory;
    /**
     * OSS对应的区域
     */
    private String endpoint;
}
