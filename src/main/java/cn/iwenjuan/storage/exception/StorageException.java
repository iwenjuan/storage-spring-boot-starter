package cn.iwenjuan.storage.exception;

import cn.iwenjuan.storage.exception.enums.StorageErrorCode;

/**
 * @author li1244
 * @date 2023/3/27 17:52
 */
public class StorageException extends BaseException {

    public StorageException(StorageErrorCode errorCode) {
        super(errorCode.getCode(), errorCode.getMessage());
    }
}
