package cn.iwenjuan.storage.config;

import cn.iwenjuan.storage.context.SpringApplicationContext;
import cn.iwenjuan.storage.service.IStorageService;
import cn.iwenjuan.storage.service.impl.*;
import cn.iwenjuan.storage.utils.ObjectUtils;
import cn.iwenjuan.storage.utils.StringUtils;
import com.aliyun.oss.ClientBuilderConfiguration;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.github.tobato.fastdfs.domain.conn.PooledConnectionFactory;
import com.github.tobato.fastdfs.domain.conn.TrackerConnectionManager;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import com.qiniu.util.Auth;
import io.minio.MinioClient;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

/**
 * @author li1244
 * @date 2023/3/24 11:33
 */
@Data
@Configuration
@ConfigurationProperties("spring.storage")
@Slf4j
public class StorageConfig {

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

    @Bean
    @ConditionalOnMissingBean(IStorageService.class)
    public IStorageService storageService() {
        switch (platform) {
            case local:
                return new LocalStorageService();
            case minio:
                if (minio == null || !StringUtils.isNotBlank(minio.getEndpoint(), minio.getAccessKey(), minio.getSecretKey(), minio.getBucketName())) {
                    return new DefaultStorageService();
                }
                MinioClient minioClient = MinioClient.builder().endpoint(minio.getEndpoint())
                        .credentials(minio.getAccessKey(), minio.getSecretKey())
                        .build();
                return new MinioStorageService(minioClient);
            case fastdfs:
                if (fastdfs == null || ObjectUtils.isEmpty(fastdfs.getTrackerList())) {
                    return new DefaultStorageService();
                }
                PooledConnectionFactory pooledConnectionFactory = SpringApplicationContext.getBean(PooledConnectionFactory.class);
                pooledConnectionFactory.setSoTimeout(fastdfs.getSoTimeout());
                pooledConnectionFactory.setConnectTimeout(fastdfs.getConnectTimeout());
                List<String> trackerList = fastdfs.getTrackerList();
                if (trackerList == null) {
                    trackerList = new ArrayList<>();
                }
                TrackerConnectionManager trackerConnectionManager = SpringApplicationContext.getBean(TrackerConnectionManager.class);
                trackerConnectionManager.setTrackerList(trackerList);
                trackerConnectionManager.initTracker();
                FastFileStorageClient fastFileStorageClient = SpringApplicationContext.getBean(FastFileStorageClient.class);
                return new FastDfsStorageService(fastFileStorageClient);
            case aliyun:
                if (aliyun == null || !StringUtils.isNotBlank(aliyun.getEndpoint(), aliyun.getAccessKey(), aliyun.getSecretKey(), aliyun.getBucketName())) {
                    return new DefaultStorageService();
                }
                ClientBuilderConfiguration configuration = new ClientBuilderConfiguration();
                // 私有云要关闭CNAME
                configuration.setSupportCname(false);
                OSS ossClient = new OSSClientBuilder().build(aliyun.getEndpoint(), aliyun.getAccessKey(), aliyun.getSecretKey(), configuration);
                return new AliyunStorageService(aliyun, ossClient);
            case qiniu:
                if (qiniu == null || !StringUtils.isNotBlank(qiniu.getAccessKey(), qiniu.getSecretKey(), qiniu.getBucketName())) {
                    return new DefaultStorageService();
                }
                if (StringUtils.isBlank(qiniu.getDomain())) {
                    log.error("检测到七牛云OSS存储平台，但未配置七牛云访问域名，将无法正常下载文件：{}", qiniu);
                }
                Auth auth = Auth.create(qiniu.getAccessKey(), qiniu.getSecretKey());
                return new QiniuStorageService(qiniu, auth);
            default:
                return new DefaultStorageService();
        }
    }
}
