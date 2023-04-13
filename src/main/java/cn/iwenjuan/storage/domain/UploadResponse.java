package cn.iwenjuan.storage.domain;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * @author li1244
 * @date 2023/3/21 10:24
 */
@Data
@Accessors(chain = true)
@NoArgsConstructor
public class UploadResponse implements Serializable {

    private static final long serialVersionUID = 5721028760255298291L;

    /**
     * 文件唯一标识，默认为文件的MD5值
     */
    private String uid;
    /**
     * 存储平台
     */
    private String platform;
    /**
     * 原始文件名称
     */
    private String fileName;
    /**
     * 文件大小
     */
    private long fileSize;
    /**
     * 上传文件路径
     */
    private String fileUrl;
    /**
     * 上传路径
     */
    private String path;
    /**
     * 上传时间
     */
    private Date uploadTime;
}
