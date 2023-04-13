package cn.iwenjuan.storage.service.impl;

import cn.iwenjuan.storage.config.StorageProperties;
import cn.iwenjuan.storage.domain.UploadResponse;
import cn.iwenjuan.storage.exception.FileDownloadException;
import cn.iwenjuan.storage.exception.enums.StorageErrorCode;
import cn.iwenjuan.storage.utils.DateUtils;
import com.aliyun.oss.OSS;
import com.aliyun.oss.model.OSSObject;
import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;

/**
 * @author li1244
 * @date 2023/3/27 9:43
 */
@Slf4j
public class AliyunStorageService extends AbstractStorageService {

    private StorageProperties.AliyunOssProperties aliyunOssProperties;

    private OSS ossClient;

    public AliyunStorageService(StorageProperties storageProperties, OSS ossClient) {
        super(storageProperties);
        this.aliyunOssProperties = storageProperties.getAliyun();
        this.ossClient = ossClient;
    }

    @Override
    public UploadResponse uploadForInputStream(InputStream inputStream, String originalFilename, String uid, long fileSize) throws Exception {

        try {
            String path = getPath();
            String fileUrl = getFileUrl(originalFilename, uid);

            UploadResponse response = new UploadResponse()
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
            try {
                OSSObject ossObject = ossClient.getObject(aliyunOssProperties.getBucketName(), key);
                if (ossObject != null) {
                    return response;
                }
            } catch (Exception e) {

            }

            ossClient.putObject(aliyunOssProperties.getBucketName(), key, inputStream);
            return response;
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
        ossClient.deleteObject(aliyunOssProperties.getBucketName(), key);
    }

    @Override
    protected InputStream getInputStream(String objectName) throws Exception {
        String key = objectName;
        if (key.startsWith(SLASH)) {
            key = key.substring(1);
        }
        try {
            OSSObject ossObject = ossClient.getObject(aliyunOssProperties.getBucketName(), key);
            return ossObject.getObjectContent();
        } catch (Exception e) {
            log.error("【文件下载异常】：objectName：{}，{}", objectName, e);
            throw new FileDownloadException(StorageErrorCode.FILE_NOT_EXIST);
        }
    }
}
