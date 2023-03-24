package cn.iwenjuan.storage.exception;

/**
 * @author li1244
 * @date 2023/3/24 11:55
 */
public class BaseException extends RuntimeException {

    private int code;

    private String message;

    public BaseException(int code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }
}
