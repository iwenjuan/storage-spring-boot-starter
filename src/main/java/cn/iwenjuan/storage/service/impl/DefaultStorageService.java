package cn.iwenjuan.storage.service.impl;

import cn.iwenjuan.storage.domain.UploadResponse;
import cn.iwenjuan.storage.exception.FileDownloadException;
import cn.iwenjuan.storage.exception.FileUploadException;
import cn.iwenjuan.storage.exception.enums.StorageErrorCode;
import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;

/**
 * @author li1244
 * @date 2023/3/27 17:27
 */
@Slf4j
public class DefaultStorageService extends AbstractStorageService {

    public DefaultStorageService() {
        printErrorConfigLog();
    }

    @Override
    public UploadResponse upload(InputStream inputStream, String originalFilename, String md5, long fileSize) throws Exception {
        printErrorConfigLog();
        throw new FileUploadException(StorageErrorCode.CONFIG_ERROR);
    }

    @Override
    public void delete(String objectName) {
        printErrorConfigLog();
    }

    @Override
    protected InputStream getInputStream(String objectName) throws Exception {
        printErrorConfigLog();
        throw new FileDownloadException(StorageErrorCode.CONFIG_ERROR);
    }

}
