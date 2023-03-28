package cn.iwenjuan.storage.config;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

/**
 * @author li1244
 * @date 2023/3/24 11:33
 */
@Data
@ConfigurationProperties("spring.storage")
@Slf4j
public class StorageProperties {

    public static final String SLASH = "/";

    /**
     * 允许上传文件最大大小，单位kb，默认10M
     */
    private long maxSize = 10485760;
    /**
     * 允许上传的文件格式，默认允许所有
     */
    private String allowed = "*";
    /**
     * 不允许上传的文件格式
     */
    private String deny;
    /**
     * 存储平台，支持本地存储和minio
     */
    private PlatformType platform = PlatformType.local;
    /**
     * 本地存储配置
     */
    private LocalProperties local = new LocalProperties();
    /**
     * minio配置
     */
    private MinioProperties minio;
    /**
     * fastdfs配置
     */
    private FastDfsProperties fastdfs;
    /**
     * 阿里云OSS配置
     */
    private AliyunOssProperties aliyun;
    /**
     * 七牛云OSS配置
     */
    private QiniuOssProperties qiniu;

    @Data
    public static class LocalProperties {
        /**
         * 本地存储路径
         */
        private String path = "/data/files";

        public String getPath() {
            if (path == null || "".equals(path)) {
                path = SLASH;
            }
            if (!path.endsWith(SLASH)) {
                path = path.concat(SLASH);
            }
            return path;
        }
    }

    @Data
    public static class MinioProperties {
        /**
         * minio地址
         */
        private String endpoint;
        /**
         * minio accessKey
         */
        private String accessKey;
        /**
         * minio秘钥
         */
        private String secretKey;
        /**
         * minio存储桶名
         */
        private String bucketName;
        /**
         * minio存储桶下的路径
         */
        private String path;

        public String getPath() {
            if (path == null) {
                path = "";
            }
            if (!path.endsWith(SLASH)) {
                path = path.concat(SLASH);
            }
            if (SLASH.equals(path)) {
                path = "";
            }
            return path;
        }
    }

    @Data
    public static class FastDfsProperties {

        /**
         * 读取时间
         */
        private int soTimeout;
        /**
         * 连接超时时间
         */
        private int connectTimeout;
        /**
         * tracker服务配置地址列表
         */
        private List<String> trackerList = new ArrayList<>();
    }

    @Data
    public static class AliyunOssProperties {

        /**
         * OSS 节点地址
         */
        private String endpoint;
        /**
         * OSS 节点accessKey
         */
        private String accessKey;
        /**
         * OSS 节点secretKey
         */
        private String secretKey;
        /**
         * 存储桶名
         */
        private String bucketName;
        /**
         * 存储桶下的路径
         */
        private String path;

        public String getPath() {
            if (path == null) {
                path = "";
            }
            if (!path.endsWith(SLASH)) {
                path = path.concat(SLASH);
            }
            if (path.startsWith(SLASH)) {
                path = path.substring(1);
            }
            if (SLASH.equals(path)) {
                path = "";
            }
            return path;
        }
    }

    @Data
    public static class QiniuOssProperties {
        /**
         * OSS 节点accessKey
         */
        private String accessKey;
        /**
         * OSS 节点secretKey
         */
        private String secretKey;
        /**
         * 存储桶名
         */
        private String bucketName;
        /**
         * 存储桶下的路径
         */
        private String path;
        /**
         * 访问七牛云的域名
         */
        public String domain;

        public String getPath() {
            if (path == null) {
                path = "";
            }
            if (!path.endsWith(SLASH)) {
                path = path.concat(SLASH);
            }
            if (path.startsWith(SLASH)) {
                path = path.substring(1);
            }
            if (SLASH.equals(path)) {
                path = "";
            }
            return path;
        }
    }

    public enum PlatformType {

        local,
        minio,
        fastdfs,
        aliyun,
        qiniu;
    }

}
