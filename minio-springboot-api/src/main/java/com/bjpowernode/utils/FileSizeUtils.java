package com.bjpowernode.utils;

/**
 * @belongsProject: minio-springboot-module
 * @belongsPackage: com.bjpowernode.utils
 * @author: Xiang想
 * @createTime: 2025-02-10  11:07
 * @description: TODO
 * @version: 1.0
 */
public class FileSizeUtils {
    private static final long KILOBYTE = 1024;
    private static final long MEGABYTE = KILOBYTE * 1024;
    private static final long GIGABYTE = MEGABYTE * 1024;
    private static final long TERABYTE = GIGABYTE * 1024;

    public static String formatFileSize(long bytes) {
        if (bytes < KILOBYTE) {
            return bytes + " B";
        } else if (bytes < MEGABYTE) {
            return formatSize(bytes, KILOBYTE, "KB");
        } else if (bytes < GIGABYTE) {
            return formatSize(bytes, MEGABYTE, "MB");
        } else if (bytes < TERABYTE) {
            return formatSize(bytes, GIGABYTE, "GB");
        } else {
            return formatSize(bytes, TERABYTE, "TB");
        }
    }

    private static String formatSize(long bytes, long unit, String unitName) {
        double size = (double) bytes / unit;
        return String.format("%.2f %s", size, unitName);
    }

    public static void main(String[] args) {
        // 测试用例
        System.out.println(formatFileSize(999));           // 输出: 0.98 KB
        System.out.println(formatFileSize(1724));          // 输出: 0.98 KB
        System.out.println(formatFileSize(1048577));       // 输出: 1.00 MB
        System.out.println(formatFileSize(1073741824));    // 输出: 1.00 GB
        System.out.println(formatFileSize(1099511627776L)); // 输出: 1.00 TB
    }
}
