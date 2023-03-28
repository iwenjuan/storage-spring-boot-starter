package cn.iwenjuan.storage.service.impl;

import cn.iwenjuan.storage.config.StorageConfig;
import cn.iwenjuan.storage.context.SpringApplicationContext;
import cn.iwenjuan.storage.domain.UploadResponse;
import cn.iwenjuan.storage.exception.FileDownloadException;
import cn.iwenjuan.storage.exception.FileUploadException;
import cn.iwenjuan.storage.exception.StorageException;
import cn.iwenjuan.storage.exception.enums.StorageErrorCode;
import cn.iwenjuan.storage.service.IStorageService;
import cn.iwenjuan.storage.utils.IdUtils;
import cn.iwenjuan.storage.utils.Md5Utils;
import cn.iwenjuan.storage.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;

/**
 * @author li1244
 * @date 2023/3/21 10:25
 */
@Slf4j
public abstract class AbstractStorageService implements IStorageService {

    private StorageConfig storageConfig;

    protected StorageConfig getStorageConfig() {
        if (storageConfig == null) {
            storageConfig = SpringApplicationContext.getBean(StorageConfig.class);
        }
        if (storageConfig == null) {
            log.error("未检测到存储平台，请检查配置：{}", storageConfig);
            throw new StorageException(StorageErrorCode.CONFIG_ERROR);
        }
        return storageConfig;
    }

    @Override
    public UploadResponse upload(MultipartFile multipartFile) {

        if (multipartFile == null || multipartFile.isEmpty()) {
            throw new FileUploadException(StorageErrorCode.FILE_IS_EMPTY);
        }
        String md5;
        try {
            md5 = Md5Utils.md5(multipartFile.getInputStream());
        } catch (IOException e) {
            log.error("【文件上传异常】：originalFilename：{}，{}", multipartFile.getOriginalFilename(), e);
            throw new FileUploadException(StorageErrorCode.FILE_UPLOAD_ERROR_CODE);
        }
        return upload(multipartFile, md5);
    }

    @Override
    public UploadResponse upload(MultipartFile multipartFile, String md5) {
        if (multipartFile == null || multipartFile.isEmpty()) {
            throw new FileUploadException(StorageErrorCode.FILE_IS_EMPTY);
        }
        String originalFilename = multipartFile.getOriginalFilename();
        long fileSize = multipartFile.getSize();

        InputStream inputStream;
        try {
            inputStream = multipartFile.getInputStream();
            return upload(inputStream, originalFilename, md5, fileSize);
        } catch (Exception e) {
            throw new FileUploadException(StorageErrorCode.FILE_UPLOAD_ERROR_CODE);
        }
    }

    @Override
    public UploadResponse upload(File file) {

        if (file == null || !file.exists()) {
            throw new FileUploadException(StorageErrorCode.FILE_IS_EMPTY);
        }
        String md5;
        try {
            md5 = Md5Utils.md5(new FileInputStream(file));
        } catch (IOException e) {
            log.error("【文件上传异常】：filename：{}，{}", file.getName(), e);
            throw new FileUploadException(StorageErrorCode.FILE_UPLOAD_ERROR_CODE);
        }
        return upload(file, md5);
    }

    @Override
    public UploadResponse upload(File file, String md5) {
        if (file == null || !file.exists()) {
            throw new FileUploadException(StorageErrorCode.FILE_IS_EMPTY);
        }
        String originalFilename = file.getName();
        long fileSize = file.length();
        InputStream inputStream;
        try {
            inputStream = new FileInputStream(file);
            return upload(inputStream, originalFilename, md5, fileSize);
        } catch (Exception e) {
            throw new FileUploadException(StorageErrorCode.FILE_UPLOAD_ERROR_CODE);
        }
    }

    @Override
    public UploadResponse upload(InputStream inputStream, String originalFilename, String md5, long fileSize) throws Exception {

        // 判断文件是否允许上传
        allowedToUpload(originalFilename);
        // 判断文件大小
        exceedMaxSize(fileSize);

        return uploadForInputStream(inputStream, originalFilename, md5, fileSize);
    }

    @Override
    public boolean allowedToUpload(String originalFilename) {
        if (StringUtils.isBlank(originalFilename)) {
            throw new FileUploadException(StorageErrorCode.FILE_TYPE_IS_NOT_ALLOWED);
        }
        String deny = getStorageConfig().getDeny();
        if (StringUtils.isBlank(deny)) {
            deny = "";
        }
        deny = deny.trim().toLowerCase();
        if ("*".equals(deny)) {
            // 所有文件类型都不允许上传
            throw new FileUploadException(StorageErrorCode.FILE_TYPE_IS_NOT_ALLOWED);
        }
        String ext = originalFilename.substring(originalFilename.lastIndexOf(".") + 1).toLowerCase();
        if (deny.contains(ext)) {
            throw new FileUploadException(StorageErrorCode.FILE_TYPE_IS_NOT_ALLOWED);
        }
        String allowed = getStorageConfig().getAllowed();
        if (StringUtils.isBlank(allowed)) {
            allowed = "";
        }
        allowed = allowed.trim().toLowerCase();
        if ("*".equals(allowed)) {
            return true;
        }
        if (allowed.contains(ext)) {
            return true;
        }
        throw new FileUploadException(StorageErrorCode.FILE_TYPE_IS_NOT_ALLOWED);
    }

    @Override
    public boolean exceedMaxSize(long fileSize) {
        if (fileSize <= 0) {
            throw new FileUploadException(StorageErrorCode.FILE_IS_EMPTY);
        }
        long maxSize = getStorageConfig().getMaxSize();
        if (maxSize < 0) {
            // 配置的最大限制小于0，则不作限制
            return true;
        }
        if (fileSize > maxSize) {
            throw new FileUploadException(StorageErrorCode.FILE_IS_TOO_LARGE);
        }
        return true;
    }

    @Override
    public void download(OutputStream outputStream, String objectName) {
        InputStream inputStream = null;
        try {
            inputStream = getInputStream(objectName);
            int len;
            byte[] buffer = new byte[8192];
            while ((len = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, len);
            }
        } catch (FileDownloadException e) {
            throw e;
        } catch (Exception e) {
            log.error("【文件下载异常】：objectName：{}，{}", objectName, e);
            throw new FileDownloadException(StorageErrorCode.FILE_NOT_EXIST);
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
     * 获取文件地址
     *
     * @param originalFilename
     * @param md5
     * @param path
     * @return
     */
    protected String getFileUrl(String originalFilename, String md5, String path) {
        if (path == null) {
            path = "";
        }
        String suffix = originalFilename.substring(originalFilename.lastIndexOf(".") + 1).toLowerCase();
        String fileName = StringUtils.isBlank(md5) ? IdUtils.randomUUID().concat(".").concat(suffix) : md5.concat(".").concat(suffix);
        return path.concat(fileName);
    }

    /**
     * 获取存储平台名称
     * @return
     */
    protected String getPlatformName() {
        return getStorageConfig().getPlatform().name();
    }

    /**
     * 打印错误日志
     */
    protected void printErrorConfigLog() {
        StorageConfig storageConfig = getStorageConfig();
        StorageConfig.PlatformType platform = storageConfig.getPlatform();
        if (platform == null) {
            log.error("未检测到存储平台，请检查配置：{}", storageConfig);
        } else {
            Object properties = storageConfig.getLocal();
            switch (platform) {
                case minio:
                    properties = storageConfig.getMinio();
                    break;
                case fastdfs:
                    properties = storageConfig.getFastdfs();
                    break;
                case aliyun:
                    properties = storageConfig.getAliyun();
                    break;
                case qiniu:
                    properties = storageConfig.getQiniu();
                    break;
                default:
                    break;
            }
            log.error("检测到【{}】存储平台，但未配置相关配置或相关配置不全，请检查配置：{}", platform, properties);
        }
    }

    /**
     * 上传文件
     * @param inputStream
     * @param originalFilename
     * @param md5
     * @param fileSize
     * @return
     * @throws Exception
     */
    protected abstract UploadResponse uploadForInputStream(InputStream inputStream, String originalFilename, String md5, long fileSize) throws Exception;

    /**
     * 获取文件流
     * @param objectName
     * @return
     * @throws Exception
     */
    protected abstract InputStream getInputStream(String objectName) throws Exception;

}
