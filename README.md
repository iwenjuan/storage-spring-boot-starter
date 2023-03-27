# storage-spring-boot-starter

## 介绍
1. 文件存储服务，支持本地存储、minio、fastdfs、阿里云OSS、七牛云OSS
2. 目前只测试了SpringBoot 2.7.X版本

## 使用说明

### maven引入依赖
~~~
<dependency>
    <groupId>cn.iwenjuan</groupId>
    <artifactId>storage-spring-boot-starter</artifactId>
    <version>1.0.1-SNAPSHOT</version>
</dependency>
~~~
### application.yml配置示例
~~~
spring:
  # 文件上传配置
  storage:
    # 允许上传文件最大大小，单位kb，默认10M
    maxSize: 10485760
    # 允许上传的文件格式，默认允许所有
    allowed: "*"
    # 不允许上传的文件格式
    deny: ".sh,.java,.class,.py,.php"
    # 存储平台，可选值：local，minio，fastdfs，aliyun，qiniu
    platform: local
    # 本地存储配置
    local:
      # 本地存储路径
      path: /data/files
    # minio配置
    minio:
      # minio地址
      endpoint: http://minio.dev:9000
      # minio账号
      accessKey: minio
      # minio秘钥
      secretKey: nji9VFR$
      # minio存储桶名
      bucketName: demo
      # minio存储桶下的路径
      path: /files
    # fastdfs配置
    fastdfs:
      # 读取时间
      so-timeout: 1000
      # 连接超时时间
      connect-timeout: 200
      # tracker服务配置地址列表
      tracker-list:
        - fastdfs.dev:22122
    # 阿里云OSS配置
    aliyun:
      # OSS节点地址
      endpoint: https://xxx.aliyuncs.com
      # accessKey
      access-key: 阿里云平台的AccessKey
      # secretKey
      secret-key: 阿里云平台的SecretKey
      # 存储桶名
      bucket-name: 存储桶名
      # 存储桶下的路径
      path: /files
    # 七牛云配置
    qiniu:
      # accessKey
      access-key: 七牛云平台的AccessKey
      # secretKey
      secret-key: 七牛云平台的SecretKey
      # 存储桶名
      bucket-name: 存储桶名
      # 存储桶下的路径
      path: /files
      # 访问七牛云的域名，不配置无法实现下载功能
      domain: http://xxx.com
~~~
### 引入IStorageService类
~~~
@Resource
private IStorageService storageService;
~~~
### IStorageService类API说明
~~~
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
~~~
### UploadResponse上传结果类
~~~
/**
 * 文件MD5值
 */
private String md5;
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
~~~
## 注意事项
### 选择minio做为存储平台需要额外引入相关依赖
~~~
<dependency>
    <groupId>io.minio</groupId>
    <artifactId>minio</artifactId>
    <version>8.4.3</version>
</dependency>

<dependency>
    <groupId>com.squareup.okhttp3</groupId>
    <artifactId>okhttp</artifactId>
    <version>4.9.2</version>
</dependency>
~~~
### 选择fastdfs做为存储平台需要额外引入相关依赖
~~~
<dependency>
    <groupId>com.github.tobato</groupId>
    <artifactId>fastdfs-client</artifactId>
    <version>1.27.2</version>
</dependency>
~~~
### 选择阿里云OSS做为存储平台需要额外引入相关依赖
~~~
<dependency>
    <groupId>com.aliyun.oss</groupId>
    <artifactId>aliyun-sdk-oss</artifactId>
    <version>3.15.1</version>
</dependency>
~~~
### 选择七牛云OSS做为存储平台需要额外引入相关依赖
~~~
<dependency>
    <groupId>com.qiniu</groupId>
    <artifactId>qiniu-java-sdk</artifactId>
    <version>7.7.0</version>
</dependency>

<dependency>
    <groupId>com.squareup.okhttp3</groupId>
    <artifactId>okhttp</artifactId>
    <version>4.9.2</version>
</dependency>
~~~