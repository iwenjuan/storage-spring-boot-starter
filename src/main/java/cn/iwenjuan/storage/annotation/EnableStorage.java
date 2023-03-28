package cn.iwenjuan.storage.annotation;

import cn.iwenjuan.storage.config.StorageConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @author li1244
 * @date 2023/3/28 9:18
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(StorageConfiguration.class)
@Documented
public @interface EnableStorage {
}
