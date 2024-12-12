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
                .credentials("I7ZLyrAR64D3ZfzTY2uo", "1OYsnV8OoDZb4kVAM24W9olQHNN1nTkv9f8YmpSf")
                .build();
    }
}
