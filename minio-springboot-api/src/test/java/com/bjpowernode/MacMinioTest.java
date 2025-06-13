package com.bjpowernode;

import com.alibaba.fastjson2.JSON;
import com.bjpowernode.service.MinIOService;
import com.bjpowernode.utils.FileSizeUtils;
import io.minio.CopyObjectArgs;
import io.minio.CopySource;
import io.minio.GetObjectArgs;
import io.minio.ListObjectsArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import io.minio.Result;
import io.minio.messages.Bucket;
import io.minio.messages.Item;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;

/**
 * @belongsProject: minio-springboot-module
 * @belongsPackage: com.bjpowernode
 * @author: Xiang想
 * @createTime: 2025-01-20  11:52
 * @description: TODO
 * @version: 1.0
 */
@SpringBootTest
public class MacMinioTest {

    @Resource
    private MinIOService minIOService;

    @Resource
    private MinioClient minioClient;

    @Test
    void contextLoads() {
        minIOService.testMinIOClient();
    }

    /**
     * @description: 列出所有的桶 bucket 和 桶创建时间
     **/
    @Test
    void getAllBucket() throws Exception {
        List<Bucket> bucketList = minioClient.listBuckets();
        bucketList.forEach(bucket -> {
            System.out.println(bucket.name() + " -- " + bucket.creationDate());
        });
    }

    /**
     * @description: 打印桶下所有文件和文件夹
     */
    @Test
    void printPrefixPathObject() throws Exception {
        String bucketName = "exchange-module"; // 桶名
        String prefix = "upload/20250207/";  // 文件夹路径（以 / 结尾）
        // 获取以 "upload/20250207/" 为前缀的所有对象（文件和文件夹）
        Iterable<Result<Item>> items = minioClient.listObjects(
                ListObjectsArgs.builder()
                        .bucket(bucketName)  // 桶名
                        .prefix(prefix)      // 限定文件夹路径
                        .build());
        // 遍历并打印所有文件和文件夹
        for (Result<Item> result : items) {
            Item item = result.get();  // 获取实际的 Item 对象
            String fullPathName = item.objectName(); // 获取全路径名
            String fileName = new File(fullPathName).getName(); // 获取不包含路径的文件名
            long fileSize = item.size();
            ZonedDateTime zonedDateTime = item.isDir() ? null : item.lastModified(); // 如果是文件夹，设置为 null
            Map<String, String> map = item.userMetadata();

            String itemType = item.isDir() ? "文件夹" : "文件"; // 判断是文件还是文件夹

            String itemsLogs = "%s: 全路径名: %s , 文件名: %s , 文件大小: %s , 最后版本时间: %s , 元信息: %s";
            itemsLogs = String.format(itemsLogs, itemType, fullPathName, fileName, 
                item.isDir() ? "无" : FileSizeUtils.formatFileSize(fileSize), 
                zonedDateTime != null ? zonedDateTime : "无", JSON.toJSONString(map)); // 处理 zonedDateTime 为 null 的情况
            System.out.println(itemsLogs);
        }
    }


    /**
     * @description: 下载文件
     */
    @Test
    void getObject() throws Exception{
        String bucketName = "exchange-module"; // 桶名
        String file = "upload/20250208/387732253f36e7e8ddc0e13f5c957022.zip";
        File targetFile = new File("/Users/xiang/Desktop/cloud/local/387732253f36e7e8ddc0e13f5c957022.zip");

        try (InputStream stream = minioClient.getObject(
                GetObjectArgs.builder()
                        .bucket(bucketName)
                        .object(file)
                        .build())) {
            // 确保目录存在
            targetFile.getParentFile().mkdirs(); // 创建目录（如果不存在）
            try (FileOutputStream outputStream = new FileOutputStream(targetFile)) {
                byte[] buffer = new byte[1024];
                int length;
                // 从 InputStream 读取数据并写入文件
                while ((length = stream.read(buffer)) > 0) {
                    outputStream.write(buffer, 0, length);
                }
                System.out.println("文件已成功保存到: " + targetFile.getAbsolutePath());
            } catch (IOException e) {
                System.err.println("保存文件时发生错误: " + e.getMessage());
            } finally {
                try {
                    if (stream != null) {
                        stream.close(); // 关闭 InputStream
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * @description: 在指定目录新增文件夹
     */
    @Test
    void createFolder() throws Exception {
        String bucketName = "exchange-module"; // 桶名
        String folderName = "upload/20250207/new-folder/"; // 新文件夹路径（以 / 结尾）

        // 创建文件夹
        minioClient.putObject(
                PutObjectArgs.builder()
                        .bucket(bucketName)
                        .object(folderName) // 文件夹名称
                        .stream(new ByteArrayInputStream(new byte[0]), 0, -1) // 空流
                        .contentType("application/x-directory") // 设置内容类型
                        .build());
        System.out.println("文件夹已成功创建: " + folderName);
    }

    /**
     * @description: 修改文件或文件夹名称
     */
    @Test
    void renameObject() throws Exception {
        String bucketName = "exchange-module"; // 桶名

        String oldObjectName = "upload/20250207/1514d84171e77a888fc319291e06f964.jpeg"; // 旧文件名
        String newObjectName = "upload/20250208/rename-test-01.jpeg"; // 新文件名

        // 复制旧文件到新文件名
        minioClient.copyObject(
                CopyObjectArgs.builder()
                        .bucket(bucketName)
                        .object(newObjectName)
                        .source(CopySource.builder()
                                .bucket(bucketName)
                                .object(oldObjectName)
                                .build())
                        .build());

        // 删除旧文件
        minioClient.removeObject(RemoveObjectArgs.builder()
                .bucket(bucketName)
                .object(oldObjectName)
                .build());

        System.out.println("文件已成功重命名: " + oldObjectName + " -> " + newObjectName);
    }

}
