package cn.iwenjuan.storage.service.impl;

import cn.iwenjuan.storage.config.StorageProperties;
import cn.iwenjuan.storage.domain.UploadResponse;
import cn.iwenjuan.storage.exception.FileDownloadException;
import cn.iwenjuan.storage.exception.FileUploadException;
import cn.iwenjuan.storage.exception.StorageException;
import cn.iwenjuan.storage.exception.enums.StorageErrorCode;
import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;

/**
 * @author li1244
 * @date 2023/3/27 17:27
 */
@Slf4j
public class DefaultStorageService extends AbstractStorageService {

    public DefaultStorageService(StorageProperties storageProperties) {
        super(storageProperties);
        printErrorConfigLog();
    }

    @Override
    public UploadResponse uploadForInputStream(InputStream inputStream, String originalFilename, String md5, long fileSize) throws Exception {
        printErrorConfigLog();
        throw new FileUploadException(StorageErrorCode.CONFIG_ERROR);
    }

    @Override
    public void delete(String objectName) {
        printErrorConfigLog();
        throw new StorageException(StorageErrorCode.CONFIG_ERROR);
    }

    @Override
    protected InputStream getInputStream(String objectName) throws Exception {
        printErrorConfigLog();
        throw new FileDownloadException(StorageErrorCode.CONFIG_ERROR);
    }

    /**
     * 打印错误日志
     */
    private void printErrorConfigLog() {
        StorageProperties.PlatformType platform = storageProperties.getPlatform();
        if (platform == null) {
            log.error("未检测到存储平台，请检查配置：{}", storageProperties);
        } else {
            Object properties = storageProperties.getLocal();
            switch (platform) {
                case minio:
                    properties = storageProperties.getMinio();
                    break;
                case fastdfs:
                    properties = storageProperties.getFastdfs();
                    break;
                case aliyun:
                    properties = storageProperties.getAliyun();
                    break;
                case qiniu:
                    properties = storageProperties.getQiniu();
                    break;
                default:
                    break;
            }
            log.error("检测到【{}】存储平台，但未配置相关配置或相关配置不全，请检查配置：{}", platform, properties);
        }
    }

}
