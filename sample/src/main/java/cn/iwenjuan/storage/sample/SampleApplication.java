package cn.iwenjuan.storage.sample;

import cn.iwenjuan.storage.annotation.EnableStorage;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author li1244
 */
@SpringBootApplication
@EnableStorage
public class SampleApplication {

    public static void main(String[] args) {
        SpringApplication.run(SampleApplication.class, args);
    }

}
