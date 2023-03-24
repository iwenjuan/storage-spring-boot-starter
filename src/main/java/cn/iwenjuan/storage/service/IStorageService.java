package cn.iwenjuan.storage.service;

import cn.iwenjuan.storage.domain.UploadResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author li1244
 * @date 2023/3/21 10:23
 */
public interface IStorageService {

    /**
     * 文件上传
     *
     * @param multipartFile
     * @return
     */
    UploadResponse upload(MultipartFile multipartFile);

    /**
     * 文件上传
     *
     * @param multipartFile
     * @param md5
     * @return
     */
    UploadResponse upload(MultipartFile multipartFile, String md5);

    /**
     * 文件上传
     *
     * @param file
     * @return
     */
    UploadResponse upload(File file);

    /**
     * 文件上传
     *
     * @param file
     * @param md5
     * @return
     */
    UploadResponse upload(File file, String md5);

    /**
     * 文件上传
     *
     * @param inputStream
     * @param originalFilename
     * @param md5
     * @param fileSize
     * @return
     * @throws Exception
     */
    UploadResponse upload(InputStream inputStream, String originalFilename, String md5, long fileSize) throws Exception;

    /**
     * 判断文件是否允许上传
     *
     * @param originalFilename
     * @return
     */
    boolean allowedToUpload(String originalFilename);

    /**
     * 判断文件大小是否超过最大限制
     *
     * @param fileSize
     * @return
     */
    boolean exceedMaxSize(long fileSize);

    /**
     * 文件下载
     *
     * @param outputStream
     * @param objectName
     */
    void download(OutputStream outputStream, String objectName);

    /**
     * 删除已上传文件
     *
     * @param objectName
     */
    void delete(String objectName);
}
