package cn.iwenjuan.storage.config;

import cn.iwenjuan.storage.context.SpringApplicationContext;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author li1244
 * @date 2023/3/24 13:10
 */
@Configuration
@EnableConfigurationProperties({StorageConfig.class})
public class StorageAutoConfig {

    @Bean
    public SpringApplicationContext springApplicationContext() {
        return new SpringApplicationContext();
    }
}
