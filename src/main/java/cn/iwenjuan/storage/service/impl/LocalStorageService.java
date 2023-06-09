package cn.iwenjuan.storage.service.impl;

import cn.iwenjuan.storage.config.StorageProperties;
import cn.iwenjuan.storage.domain.UploadResponse;
import cn.iwenjuan.storage.exception.FileDownloadException;
import cn.iwenjuan.storage.exception.enums.StorageErrorCode;
import cn.iwenjuan.storage.utils.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.IOUtils;

import java.io.*;

/**
 * @author li1244
 * @date 2023/3/24 12:52
 */
@Slf4j
public class LocalStorageService extends AbstractStorageService {

    private StorageProperties.LocalProperties localProperties;

    public LocalStorageService(StorageProperties storageProperties) {
        super(storageProperties);
        this.localProperties = storageProperties.getLocal();
    }

    @Override
    public UploadResponse uploadForInputStream(InputStream inputStream, String originalFilename, String uid, long fileSize) throws Exception {

        OutputStream outputStream = null;
        try {
            String path = getPath();
            mkdir(path);
            String fileUrl = getFileUrl(originalFilename, uid);

            UploadResponse response = new UploadResponse()
                    .setPlatform(getPlatformName())
                    .setFileName(originalFilename)
                    .setFileSize(fileSize)
                    .setFileUrl(fileUrl)
                    .setPath(path)
                    .setUid(uid)
                    .setUploadTime(DateUtils.now());

            File file = new File(fileUrl);
            if (file.exists()) {
                // 文件已上传过，不再上传
                return response;
            }
            outputStream = new FileOutputStream(fileUrl);
            IOUtils.copy(inputStream, outputStream);
            return response;
        } catch (Exception e) {
            log.error("【文件上传异常】：originalFilename：{}，{}", originalFilename, e);
            throw e;
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
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
     * 创建目录
     * @param path
     */
    private void mkdir(String path) {
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }
    }

    @Override
    protected InputStream getInputStream(String objectName) {
        try {
            return new FileInputStream(objectName);
        } catch (FileNotFoundException e) {
            log.error("【文件下载异常】：objectName：{}，{}", objectName, e);
            throw new FileDownloadException(StorageErrorCode.FILE_NOT_EXIST);
        }
    }

    @Override
    public void delete(String objectName) {
        File file = new File(objectName);
        if (file.exists()) {
            file.delete();
        }
    }
}
