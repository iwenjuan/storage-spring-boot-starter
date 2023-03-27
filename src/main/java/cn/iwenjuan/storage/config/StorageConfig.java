package cn.iwenjuan.storage.config;

import cn.iwenjuan.storage.context.SpringApplicationContext;
import cn.iwenjuan.storage.service.IStorageService;
import cn.iwenjuan.storage.service.impl.AliyunStorageService;
import cn.iwenjuan.storage.service.impl.FastDfsStorageService;
import cn.iwenjuan.storage.service.impl.LocalStorageService;
import cn.iwenjuan.storage.service.impl.MinioStorageService;
import com.aliyun.oss.ClientBuilderConfiguration;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.OSSClientBuilder;
import com.github.tobato.fastdfs.domain.conn.PooledConnectionFactory;
import com.github.tobato.fastdfs.domain.conn.TrackerConnectionManager;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import io.minio.MinioClient;
import lombok.Data;
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
    private MinioProperties minio = new MinioProperties();
    /**
     * fastdfs配置
     */
    private FastDfsProperties fastdfs = new FastDfsProperties();
    /**
     * 阿里云OSS配置
     */
    private AliyunOssProperties aliyun = new AliyunOssProperties();

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

    public enum PlatformType {

        local,
        minio,
        fastdfs,
        aliyun;
    }

    @Bean
    @ConditionalOnMissingBean(IStorageService.class)
    public IStorageService storageService() {
        switch (platform) {
            case local:
                return new LocalStorageService();
            case minio:
                MinioClient minioClient = MinioClient.builder().endpoint(minio.getEndpoint())
                        .credentials(minio.getAccessKey(), minio.getSecretKey())
                        .build();
                return new MinioStorageService(minioClient);
            case fastdfs:
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
                ClientBuilderConfiguration configuration = new ClientBuilderConfiguration();
                // 私有云要关闭CNAME
                configuration.setSupportCname(false);
                OSS ossClient = new OSSClientBuilder().build(aliyun.getEndpoint(), aliyun.getAccessKey(), aliyun.getSecretKey(), configuration);
                return new AliyunStorageService(aliyun, ossClient);
            default:
                throw new IllegalStateException("未找到对应的存储平台");
        }
    }
}
