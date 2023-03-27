package cn.iwenjuan.storage.utils;

import cn.iwenjuan.storage.context.SpringApplicationContext;
import org.springframework.http.*;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author li1244
 * @date 2023/3/23 17:12
 */
public class HttpUtils {

    public static final int HTTP_READ_TIMEOUT = 60000;
    public static final int HTTP_CONN_TIMEOUT = 60000;

    private static RestTemplate restTemplate;

    private static RestTemplate getRestTemplate() {
        if (restTemplate != null) {
            return restTemplate;
        }
        try {
            restTemplate = SpringApplicationContext.getBean("restTemplate");
            if (restTemplate != null) {
                return restTemplate;
            }
        } catch (Exception e) {
        }
        TrustManager[] trustManagers = new TrustManager[]{
                new X509TrustManager() {

                    @Override
                    public X509Certificate[] getAcceptedIssuers() {
                        return new X509Certificate[0];
                    }

                    @Override
                    public void checkClientTrusted(X509Certificate[] chain, String authType) {
                    }

                    @Override
                    public void checkServerTrusted(X509Certificate[] chain, String authType) {
                    }
                }
        };
        SSLContext sslContext;
        try {
            sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, trustManagers, new SecureRandom());
            sslContext.getSocketFactory();
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            throw new RuntimeException(e);
        }
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory() {
            @Override
            protected void prepareConnection(HttpURLConnection connection, @NotNull String httpMethod) throws IOException {
                super.prepareConnection(connection, httpMethod);
                if (connection instanceof HttpsURLConnection) {
                    // 设置SSL连接工厂，忽略证书配置
                    ((HttpsURLConnection) connection).setSSLSocketFactory(sslContext.getSocketFactory());
                }
            }
        };
        // 设置连接超时时间
        factory.setConnectTimeout(HTTP_CONN_TIMEOUT);
        // 设置响应超时时间
        factory.setReadTimeout(HTTP_READ_TIMEOUT);
        restTemplate = new RestTemplate(factory);
        // 解决中文乱码问题
        List<HttpMessageConverter<?>> httpMessageConverters = restTemplate.getMessageConverters();
        httpMessageConverters.stream().forEach(httpMessageConverter -> {
            if (httpMessageConverter instanceof StringHttpMessageConverter) {
                StringHttpMessageConverter messageConverter = (StringHttpMessageConverter) httpMessageConverter;
                messageConverter.setDefaultCharset(Charset.forName("UTF-8"));
            }
        });
        return restTemplate;
    }

    /**
     * 发送get请求
     * @param url   请求地址
     * @return
     */
    public static String get(String url) {
        return get(url, null);
    }

    /**
     * 发送get请求
     * @param url       请求地址
     * @param params    请求参数
     * @return
     */
    public static String get(String url, Map<String, String> params) {
        return getWithHeaders(url, params, null);
    }

    /**
     * 发送get请求
     * @param url       请求地址
     * @param headers   请求头
     * @return
     */
    public static String getWithHeaders(String url, Map<String, String> headers) {
        return getWithHeaders(url, null, headers);
    }

    /**
     * 发送get请求
     * @param url       请求地址
     * @param params    请求参数
     * @param headers   请求头
     * @return
     */
    public static String getWithHeaders(String url, Map<String, String> params, Map<String, String> headers) {
        return getWithHeaders(url, params, headers, String.class);
    }

    /**
     * 发送get请求
     * @param url           请求地址
     * @param params        请求参数
     * @param headers       请求头
     * @param responseType  返回结果类型
     * @return
     */
    public static <T> T getWithHeaders(String url, Map<String, String> params, Map<String, String> headers, Class<T> responseType) {

        HttpHeaders httpHeaders = new HttpHeaders();
        if (Objects.nonNull(headers)) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                httpHeaders.add(entry.getKey(), entry.getValue());
            }
        }
        RestTemplate restTemplate = getRestTemplate();
        HttpEntity httpEntity = new HttpEntity(null, httpHeaders);
        ResponseEntity<T> responseEntity = restTemplate.exchange(getUrlWithParams(url, params), HttpMethod.GET, httpEntity, responseType);
        return responseEntity.getBody();
    }

    /**
     * 发送post请求
     * @param url       请求地址
     * @param body      请求参数（body参数）
     * @return
     */
    public static String post(String url, Object body) {
        return post(url, null, body);
    }

    /**
     * 发送post请求
     * @param url       请求地址
     * @param params    请求参数（url参数）
     * @param body      请求参数（body参数）
     * @return
     */
    public static String post(String url, Map<String, String> params, Object body) {
        return postWithHeaders(url, params, body, null);
    }

    /**
     * 发送post请求
     * @param url       请求地址
     * @param body      请求参数（body参数）
     * @param headers   请求头
     * @return
     */
    public static String postWithHeaders(String url, Object body, Map<String, String> headers) {
        return postWithHeaders(url, null, body, headers);
    }

    /**
     * 发送post请求
     * @param url       请求地址
     * @param params    请求参数（url参数）
     * @param body      请求参数（body参数）
     * @param headers   请求头
     * @return
     */
    public static String postWithHeaders(String url, Map<String, String> params, Object body, Map<String, String> headers) {
        return postWithHeaders(url, params, body, headers, String.class);
    }

    /**
     * 发送post请求
     * @param url           请求地址
     * @param params        请求参数（url参数）
     * @param body          请求参数（body参数）
     * @param headers       请求头
     * @param responseType  返回结果类型
     * @return
     */
    public static <T> T postWithHeaders(String url, Map<String, String> params, Object body, Map<String, String> headers, Class<T> responseType) {
        if (Objects.isNull(body)) {
            body = new HashMap<>(4);
        }
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        if (Objects.nonNull(headers)) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                httpHeaders.add(entry.getKey(), entry.getValue());
            }
        }
        RestTemplate restTemplate = getRestTemplate();
        HttpEntity httpEntity = new HttpEntity(body, httpHeaders);
        ResponseEntity<T> responseEntity = restTemplate.exchange(getUrlWithParams(url, params), HttpMethod.POST, httpEntity, responseType);
        return responseEntity.getBody();
    }

    /**
     * 处理请求参数
     * @param url
     * @param params
     * @return
     */
    private static String getUrlWithParams(String url, Map<String, String> params) {
        StringBuffer buffer = new StringBuffer();
        if (Objects.nonNull(params)) {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                String value = entry.getValue();
                try {
                    value = URLEncoder.encode(value, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                buffer.append(entry.getKey()).append("=").append(value).append("&");
            }
        }
        if (buffer.length() > 0) {
            String paramsStr = buffer.toString();
            url = url.concat("?").concat(paramsStr);
        }
        return url;
    }

}
