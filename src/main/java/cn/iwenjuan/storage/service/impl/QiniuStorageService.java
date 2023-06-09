package cn.iwenjuan.storage.service.impl;

import cn.iwenjuan.storage.config.StorageProperties;
import cn.iwenjuan.storage.domain.UploadResponse;
import cn.iwenjuan.storage.exception.FileDownloadException;
import cn.iwenjuan.storage.exception.enums.StorageErrorCode;
import cn.iwenjuan.storage.utils.DateUtils;
import cn.iwenjuan.storage.utils.HttpUtils;
import cn.iwenjuan.storage.utils.StringUtils;
import com.qiniu.common.QiniuException;
import com.qiniu.storage.BucketManager;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.Region;
import com.qiniu.storage.UploadManager;
import com.qiniu.util.Auth;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author li1244
 * @date 2023/3/27 14:55
 */
@Slf4j
public class QiniuStorageService extends AbstractStorageService {

    private StorageProperties.QiniuOssProperties qiniuOssProperties;

    private Auth auth;

    private UploadManager uploadManager;

    private BucketManager bucketManager;

    public QiniuStorageService(StorageProperties storageProperties, Auth auth) {
        super(storageProperties);
        this.qiniuOssProperties = storageProperties.getQiniu();
        this.auth = auth;
        getUploadManager();
        getBucketManager();
    }

    /**
     * 构建配置对象
     *
     * @return
     */
    private Configuration configuration() {
        // 构造一个带指定 Region 对象的配置类
        Configuration configuration = new Configuration(Region.autoRegion());
        return configuration;
    }

    /**
     * 构建上传管理实例
     *
     * @return
     */
    private UploadManager getUploadManager() {
        if (uploadManager == null) {
            uploadManager = new UploadManager(configuration());
        }
        return uploadManager;
    }

    /**
     * 构建桶管理实例
     *
     * @return
     */
    private BucketManager getBucketManager() {
        if (bucketManager == null) {
            bucketManager = new BucketManager(auth, configuration());
        }
        return bucketManager;
    }

    @Override
    public UploadResponse uploadForInputStream(InputStream inputStream, String originalFilename, String uid, long fileSize) throws Exception {

        try {
            String path = getPath();
            String fileUrl = getFileUrl(originalFilename, uid);
            UploadResponse uploadResponse = new UploadResponse()
                    .setPlatform(getPlatformName())
                    .setFileName(originalFilename)
                    .setFileSize(fileSize)
                    .setFileUrl(fileUrl)
                    .setPath(path)
                    .setUid(uid)
                    .setUploadTime(DateUtils.now());

            String key = fileUrl;
            if (key.startsWith(SLASH)) {
                key = key.substring(1);
            }
            // 生成上传凭证，然后准备上传
            String uploadToken = auth.uploadToken(qiniuOssProperties.getBucketName(), key);
            // 上传文件
            getUploadManager().put(inputStream, key, uploadToken, null, null);

            return uploadResponse;
        } catch (Exception e) {
            log.error("【文件上传异常】：originalFilename：{}，{}", originalFilename, e);
            throw e;
        }
    }

    @Override
    public void delete(String objectName) {
        String key = objectName;
        if (key.startsWith(SLASH)) {
            key = key.substring(1);
        }
        try {
            getBucketManager().delete(qiniuOssProperties.getBucketName(), key);
        } catch (QiniuException e) {
            log.error("【删除文件失败】：objectName：{}，{}", objectName, e);
        }
    }

    @Override
    public void download(OutputStream outputStream, String objectName) {
        if (StringUtils.isBlank(qiniuOssProperties.getDomain())) {
            log.error("检测到七牛云OSS存储平台，但未配置七牛云访问域名，无法下载文件：{}", qiniuOssProperties);
            throw new FileDownloadException(StorageErrorCode.CONFIG_ERROR);
        }
        String key = objectName;
        if (key.startsWith(SLASH)) {
            key = key.substring(1);
        }
        String domain = qiniuOssProperties.getDomain();
        if (!domain.endsWith(SLASH)) {
            domain = domain.concat(SLASH);
        }
        try {
            String privateDownloadUrl = auth.privateDownloadUrl(domain.concat(key));
            byte[] bytes = HttpUtils.getWithHeaders(privateDownloadUrl, null, null, byte[].class);
            outputStream.write(bytes);
        } catch (IOException e) {
            log.error("【文件下载异常】：objectName：{}，{}", objectName, e);
            throw new FileDownloadException(StorageErrorCode.FILE_NOT_EXIST);
        }
    }

    @Override
    protected InputStream getInputStream(String objectName) throws Exception {
        String key = objectName;
        if (key.startsWith(SLASH)) {
            key = key.substring(1);
        }
        String domain = qiniuOssProperties.getDomain();
        if (!domain.endsWith(SLASH)) {
            domain = domain.concat(SLASH);
        }
        try {
            String privateDownloadUrl = auth.privateDownloadUrl(domain.concat(key));
            byte[] bytes = HttpUtils.getWithHeaders(privateDownloadUrl, null, null, byte[].class);
            return new ByteArrayInputStream(bytes);
        } catch (Exception e) {
            log.error("【文件下载异常】：objectName：{}，{}", objectName, e);
            throw new FileDownloadException(StorageErrorCode.FILE_NOT_EXIST);
        }
    }
}
