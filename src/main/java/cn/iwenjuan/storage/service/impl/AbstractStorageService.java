package cn.iwenjuan.storage.service.impl;

import cn.iwenjuan.storage.config.StorageProperties;
import cn.iwenjuan.storage.domain.UploadResponse;
import cn.iwenjuan.storage.exception.FileDownloadException;
import cn.iwenjuan.storage.exception.FileUploadException;
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

    protected static final String SLASH = "/";

    protected StorageProperties storageProperties;

    public AbstractStorageService(StorageProperties storageProperties) {
        this.storageProperties = storageProperties;
    }

    @Override
    public UploadResponse upload(MultipartFile multipartFile) {

        if (multipartFile == null || multipartFile.isEmpty()) {
            throw new FileUploadException(StorageErrorCode.FILE_IS_EMPTY);
        }
        String uid;
        try {
            uid = Md5Utils.md5(multipartFile.getInputStream());
        } catch (IOException e) {
            log.error("【文件上传异常】：originalFilename：{}，{}", multipartFile.getOriginalFilename(), e);
            throw new FileUploadException(StorageErrorCode.FILE_UPLOAD_ERROR_CODE);
        }
        return upload(multipartFile, uid);
    }

    @Override
    public UploadResponse upload(MultipartFile multipartFile, String uid) {
        if (multipartFile == null || multipartFile.isEmpty()) {
            throw new FileUploadException(StorageErrorCode.FILE_IS_EMPTY);
        }
        String originalFilename = multipartFile.getOriginalFilename();
        long fileSize = multipartFile.getSize();

        InputStream inputStream;
        try {
            inputStream = multipartFile.getInputStream();
            return upload(inputStream, originalFilename, uid, fileSize);
        } catch (Exception e) {
            throw new FileUploadException(StorageErrorCode.FILE_UPLOAD_ERROR_CODE);
        }
    }

    @Override
    public UploadResponse upload(File file) {

        if (file == null || !file.exists()) {
            throw new FileUploadException(StorageErrorCode.FILE_IS_EMPTY);
        }
        String uid;
        try {
            uid = Md5Utils.md5(new FileInputStream(file));
        } catch (IOException e) {
            log.error("【文件上传异常】：filename：{}，{}", file.getName(), e);
            throw new FileUploadException(StorageErrorCode.FILE_UPLOAD_ERROR_CODE);
        }
        return upload(file, uid);
    }

    @Override
    public UploadResponse upload(File file, String uid) {
        if (file == null || !file.exists()) {
            throw new FileUploadException(StorageErrorCode.FILE_IS_EMPTY);
        }
        String originalFilename = file.getName();
        long fileSize = file.length();
        InputStream inputStream;
        try {
            inputStream = new FileInputStream(file);
            return upload(inputStream, originalFilename, uid, fileSize);
        } catch (Exception e) {
            throw new FileUploadException(StorageErrorCode.FILE_UPLOAD_ERROR_CODE);
        }
    }

    @Override
    public UploadResponse upload(InputStream inputStream, String originalFilename, String uid, long fileSize) throws Exception {

        // 判断文件是否允许上传
        allowedToUpload(originalFilename);
        // 判断文件大小
        exceedMaxSize(fileSize);

        return uploadForInputStream(inputStream, originalFilename, uid, fileSize);
    }

    @Override
    public boolean allowedToUpload(String originalFilename) {
        if (StringUtils.isBlank(originalFilename)) {
            throw new FileUploadException(StorageErrorCode.FILE_TYPE_IS_NOT_ALLOWED);
        }
        String deny = storageProperties.getDeny();
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
        String allowed = storageProperties.getAllowed();
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
        long maxSize = storageProperties.getMaxSize();
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
     * @param uid
     * @return
     */
    protected String getFileUrl(String originalFilename, String uid) {
        String suffix = originalFilename.substring(originalFilename.lastIndexOf(".") + 1).toLowerCase();
        String fileName = StringUtils.isBlank(uid) ? IdUtils.randomUUID().concat(".").concat(suffix) : uid.concat(".").concat(suffix);
        return getPath().concat(fileName);
    }

    /**
     * 获取存储路径
     * @return
     */
    protected String getPath() {
        StorageProperties.PlatformType platform = storageProperties.getPlatform();
        switch (platform) {
            case local:
                StorageProperties.LocalProperties local = storageProperties.getLocal();
                return getPath(local.getPath(), local.getClassify());
            case minio:
                StorageProperties.MinioProperties minio = storageProperties.getMinio();
                return getPath(minio.getPath(), minio.getClassify());
            case aliyun:
                StorageProperties.AliyunOssProperties aliyun = storageProperties.getAliyun();
                return getPath(aliyun.getPath(), aliyun.getClassify());
            case qiniu:
                StorageProperties.QiniuOssProperties qiniu = storageProperties.getQiniu();
                return getPath(qiniu.getPath(), qiniu.getClassify());
            case fastdfs:
            default:
                return "";
        }
    }

    /**
     * 获取存储路径
     * @param path
     * @param classify
     * @return
     */
    protected String getPath(String path, StorageProperties.Classify classify) {
        if (path == null || "".equals(path)) {
            path = SLASH;
        }
        if (!path.endsWith(SLASH)) {
            path = path.concat(SLASH);
        }
        path = path.concat(classify.classifyPath());
        if (!path.endsWith(SLASH)) {
            path = path.concat(SLASH);
        }
        return path;
    }

    /**
     * 获取存储平台名称
     * @return
     */
    protected String getPlatformName() {
        return storageProperties.getPlatform().name();
    }

    /**
     * 上传文件
     * @param inputStream
     * @param originalFilename
     * @param uid
     * @param fileSize
     * @return
     * @throws Exception
     */
    protected abstract UploadResponse uploadForInputStream(InputStream inputStream, String originalFilename, String uid, long fileSize) throws Exception;

    /**
     * 获取文件流
     * @param objectName
     * @return
     * @throws Exception
     */
    protected abstract InputStream getInputStream(String objectName) throws Exception;

}
