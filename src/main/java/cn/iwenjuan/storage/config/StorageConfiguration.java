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
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

/**
 * @author li1244
 * @date 2023/3/24 13:10
 */
@Configuration
@Slf4j
public class StorageConfiguration {

    @Bean
    public SpringApplicationContext springApplicationContext() {
        return new SpringApplicationContext();
    }

    @Bean
    @ConfigurationProperties("spring.storage")
    public StorageProperties storageConfig() {
        return new StorageProperties();
    }

    @Bean
    @ConditionalOnMissingBean(IStorageService.class)
    public IStorageService storageService(StorageProperties storageProperties) {
        StorageProperties.PlatformType platform = storageProperties.getPlatform();
        switch (platform) {
            case local:
                return new LocalStorageService(storageProperties);
            case minio:
                StorageProperties.MinioProperties minio = storageProperties.getMinio();
                if (minio == null || !StringUtils.isNotBlank(minio.getEndpoint(), minio.getAccessKey(), minio.getSecretKey(), minio.getBucketName())) {
                    return new DefaultStorageService(storageProperties);
                }
                MinioClient minioClient = MinioClient.builder().endpoint(minio.getEndpoint())
                        .credentials(minio.getAccessKey(), minio.getSecretKey())
                        .build();
                return new MinioStorageService(storageProperties, minioClient);
            case fastdfs:
                StorageProperties.FastDfsProperties fastdfs = storageProperties.getFastdfs();
                if (fastdfs == null || ObjectUtils.isEmpty(fastdfs.getTrackerList())) {
                    return new DefaultStorageService(storageProperties);
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
                return new FastDfsStorageService(storageProperties, fastFileStorageClient);
            case aliyun:
                StorageProperties.AliyunOssProperties aliyun = storageProperties.getAliyun();
                if (aliyun == null || !StringUtils.isNotBlank(aliyun.getEndpoint(), aliyun.getAccessKey(), aliyun.getSecretKey(), aliyun.getBucketName())) {
                    return new DefaultStorageService(storageProperties);
                }
                ClientBuilderConfiguration configuration = new ClientBuilderConfiguration();
                // 私有云要关闭CNAME
                configuration.setSupportCname(false);
                OSS ossClient = new OSSClientBuilder().build(aliyun.getEndpoint(), aliyun.getAccessKey(), aliyun.getSecretKey(), configuration);
                return new AliyunStorageService(storageProperties, ossClient);
            case qiniu:
                StorageProperties.QiniuOssProperties qiniu = storageProperties.getQiniu();
                if (qiniu == null || !StringUtils.isNotBlank(qiniu.getAccessKey(), qiniu.getSecretKey(), qiniu.getBucketName())) {
                    return new DefaultStorageService(storageProperties);
                }
                if (StringUtils.isBlank(qiniu.getDomain())) {
                    log.error("检测到七牛云OSS存储平台，但未配置七牛云访问域名，将无法正常下载文件：{}", qiniu);
                }
                Auth auth = Auth.create(qiniu.getAccessKey(), qiniu.getSecretKey());
                return new QiniuStorageService(storageProperties, auth);
            default:
                return new DefaultStorageService(storageProperties);
        }
    }
}
