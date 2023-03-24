package cn.iwenjuan.storage.exception;

import cn.iwenjuan.storage.exception.enums.StorageErrorCode;

/**
 * @author li1244
 * @date 2023/3/24 11:58
 */
public class FileDownloadException extends BaseException {

    public FileDownloadException(StorageErrorCode errorCode) {
        super(errorCode.getCode(), errorCode.getMessage());
    }
}
