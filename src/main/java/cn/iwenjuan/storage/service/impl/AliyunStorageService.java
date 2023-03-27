package cn.iwenjuan.storage.service.impl;

import cn.iwenjuan.storage.config.StorageConfig;
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

    private StorageConfig.AliyunOssProperties aliyunOssProperties;

    private OSS ossClient;

    public AliyunStorageService(StorageConfig.AliyunOssProperties aliyunOssProperties, OSS ossClient) {
        this.aliyunOssProperties = aliyunOssProperties;
        this.ossClient = ossClient;
    }

    @Override
    public UploadResponse upload(InputStream inputStream, String originalFilename, String md5, long fileSize) throws Exception {

        // 判断文件是否允许上传
        allowedToUpload(originalFilename);
        // 判断文件大小
        exceedMaxSize(fileSize);
        try {
            String fileUrl = getFileUrl(originalFilename, md5, aliyunOssProperties.getPath());
            ossClient.putObject(aliyunOssProperties.getBucketName(), fileUrl, inputStream);
            UploadResponse response = new UploadResponse()
                    .setPlatform(getPlatformName())
                    .setFileName(originalFilename)
                    .setFileSize(fileSize)
                    .setFileUrl(fileUrl)
                    .setPath("")
                    .setMd5(md5)
                    .setUploadTime(DateUtils.now());
            return response;
        } catch (Exception e) {
            log.error("【文件上传异常】：originalFilename：{}，{}", originalFilename, e);
            throw e;
        }
    }

    @Override
    public void delete(String objectName) {
        ossClient.deleteObject(aliyunOssProperties.getBucketName(), objectName);
    }

    @Override
    protected InputStream getInputStream(String objectName) throws Exception {

        try {
            OSSObject ossObject = ossClient.getObject(aliyunOssProperties.getBucketName(), objectName);
            return ossObject.getObjectContent();
        } catch (Exception e) {
            log.error("【文件下载异常】：objectName：{}，{}", objectName, e);
            throw new FileDownloadException(StorageErrorCode.FILE_NOT_EXIST);
        }
    }
}