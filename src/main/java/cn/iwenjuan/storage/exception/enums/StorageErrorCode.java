package cn.iwenjuan.storage.exception.enums;

/**
 * @author li1244
 * @date 2023/3/10 16:56
 */
public enum StorageErrorCode {

    FILE_IS_EMPTY(107001, "文件为空"),

    FILE_TYPE_IS_NOT_ALLOWED(107002, "文件类型不允许"),

    FILE_IS_TOO_LARGE(107003, "文件超过最大限制"),

    FILE_UPLOAD_ERROR_CODE(107004, "文件上传失败"),

    FILE_NOT_EXIST(107005, "文件不存在"),

    FILE_DOWNLOAD_ERROR(107006, "文件下载异常"),

    CONFIG_ERROR(107007, "配置异常");

    private int code;

    private String message;

    StorageErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
