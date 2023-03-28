package cn.iwenjuan.storage.config;

import cn.iwenjuan.storage.utils.DateUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * @author li1244
 * @date 2023/3/24 11:33
 */
@Data
@Slf4j
public class StorageProperties {

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
        /**
         * 归类
         */
        private Classify classify = Classify.non;

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
        /**
         * 归类
         */
        private Classify classify = Classify.non;

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
        /**
         * 归类
         */
        private Classify classify = Classify.non;

    }

    @Data
    public static class QiniuOssProperties {

        /**
         * 访问七牛云的域名
         */
        public String domain;
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
         * 归类
         */
        private Classify classify = Classify.non;

    }

    public enum Classify {

        non,
        year,
        month,
        day;

        public String classifyPath() {
            switch (this) {
                case year:
                    return DateUtils.format(DateUtils.now(), "yyyy");
                case month:
                    return DateUtils.format(DateUtils.now(), "yyyy/MM");
                case day:
                    return DateUtils.format(DateUtils.now(), "yyyy/MM/dd");
                case non:
                default:
                    return "";
            }
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
