package com.bjpowernode.config;

import io.minio.MinioClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Config {

    //单例的，那么MinioClient对象有没有线程安全问题呢？答案是：没有线程安全问题
    @Bean
    public MinioClient minioClient() {
        //链式编程，构建MinioClient对象
        return MinioClient.builder()
                .endpoint("http://127.0.0.1:9000")
                .credentials("FlycpduMTK7MvV7nbwWX", "rC7cTHEbeR0jxagQcTkaALDvXC2MdmTIYnnHPI5q")
                .build();
    }
}
