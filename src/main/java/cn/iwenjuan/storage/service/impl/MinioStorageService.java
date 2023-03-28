package cn.iwenjuan.storage.service.impl;

import cn.iwenjuan.storage.config.StorageConfig;
import cn.iwenjuan.storage.domain.UploadResponse;
import cn.iwenjuan.storage.exception.FileDownloadException;
import cn.iwenjuan.storage.exception.enums.StorageErrorCode;
import cn.iwenjuan.storage.utils.DateUtils;
import io.minio.*;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author li1244
 * @date 2023/3/24 12:52
 */
@Slf4j
public class MinioStorageService extends AbstractStorageService {

    private MinioClient minioClient;

    private StorageConfig.MinioProperties minioProperties;

    public MinioStorageService(MinioClient minioClient) {
        this.minioClient = minioClient;
        this.minioProperties = getStorageConfig().getMinio();
    }

    @Override
    public UploadResponse uploadForInputStream(InputStream inputStream, String originalFilename, String md5, long fileSize) throws Exception {

        try {

            String path = minioProperties.getPath();
            String fileUrl = getFileUrl(originalFilename, md5, path);

            UploadResponse response = new UploadResponse()
                    .setPlatform(getPlatformName())
                    .setFileName(originalFilename)
                    .setFileSize(fileSize)
                    .setFileUrl(fileUrl)
                    .setPath(path)
                    .setMd5(md5)
                    .setUploadTime(DateUtils.now());

            if (objectExist(fileUrl)) {
                // 文件已上传过，不再上传
                return response;
            }

            // 上传文件到minio
            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(minioProperties.getBucketName())
                    .stream(inputStream, fileSize, -1)
                    .object(fileUrl)
                    .build());
            return response;
        } catch (Exception e) {
            log.error("【文件上传异常】：originalFilename：{}，{}", originalFilename, e);
            throw e;
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 判断文件是否已经上传
     *
     * @param objectName
     * @return
     */
    private boolean objectExist(String objectName) {

        try {
            GetObjectResponse object = minioClient.getObject(GetObjectArgs.builder()
                    .bucket(minioProperties.getBucketName())
                    .object(objectName)
                    .build());
            if (object != null) {
                return true;
            }
        } catch (Exception e) {
            log.error("【获取存储对象异常】：objectName：{}，{}", objectName, e);
            return false;
        }
        return true;
    }

    @Override
    protected InputStream getInputStream(String objectName) throws Exception {
        try {
            return minioClient.getObject(GetObjectArgs.builder().bucket(minioProperties.getBucketName()).object(objectName).build());
        } catch (Exception e) {
            log.error("【文件下载异常】：objectName：{}，{}", objectName, e);
            throw new FileDownloadException(StorageErrorCode.FILE_NOT_EXIST);
        }
    }

    @Override
    public void delete(String objectName) {
        try {
            minioClient.removeObject(RemoveObjectArgs.builder()
                    .bucket(minioProperties.getBucketName())
                    .object(objectName)
                    .build());
        } catch (Exception e) {
            log.error("【删除已上传文件异常】：objectName：{}，{}", objectName, e);
        }
    }
}
