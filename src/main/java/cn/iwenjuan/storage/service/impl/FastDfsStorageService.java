package cn.iwenjuan.storage.service.impl;

import cn.iwenjuan.storage.domain.UploadResponse;
import cn.iwenjuan.storage.exception.FileUploadException;
import cn.iwenjuan.storage.exception.enums.StorageErrorCode;
import cn.iwenjuan.storage.utils.DateUtils;
import com.github.tobato.fastdfs.domain.fdfs.StorePath;
import com.github.tobato.fastdfs.domain.proto.storage.DownloadCallback;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author li1244
 * @date 2023/3/24 12:54
 */
@Slf4j
public class FastDfsStorageService extends AbstractStorageService {

    private FastFileStorageClient fastFileStorageClient;

    public FastDfsStorageService(FastFileStorageClient fastFileStorageClient) {
        this.fastFileStorageClient = fastFileStorageClient;
    }

    @Override
    public UploadResponse upload(InputStream inputStream, String originalFilename, String md5, long fileSize) throws Exception {

        try {
            String extName = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
            StorePath storePath = fastFileStorageClient.uploadFile(inputStream, fileSize, extName, null);
            String fullPath = storePath.getFullPath();
            UploadResponse response = new UploadResponse()
                    .setPlatform(getPlatformName())
                    .setFileName(originalFilename)
                    .setFileSize(fileSize)
                    .setFileUrl("/".concat(fullPath))
                    .setPath("")
                    .setMd5(md5)
                    .setUploadTime(DateUtils.now());
            return response;
        } catch (Exception e) {
            log.error("【文件上传失败】：{}", e);
            throw new FileUploadException(StorageErrorCode.FILE_UPLOAD_ERROR_CODE);
        }
    }

    @Override
    public void delete(String objectName) {
        fastFileStorageClient.deleteFile(objectName);
    }

    @Override
    protected InputStream getInputStream(String objectName) throws Exception {
        if (objectName.startsWith("/")) {
            objectName = objectName.substring(1);
        }
        int index = objectName.indexOf("/");
        String group = objectName.substring(0,index);
        String path = objectName.substring(index + 1);
        return fastFileStorageClient.downloadFile(group, path, new DownloadCallback<InputStream>() {
            @Override
            public InputStream recv(InputStream ins) throws IOException {
                return ins;
            }
        });
    }
}
