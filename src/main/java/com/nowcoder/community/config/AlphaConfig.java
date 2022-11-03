package com.nowcoder.community.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.text.SimpleDateFormat;

@Configuration //表明这是个配置类
public class AlphaConfig {
    //表明这是第三方Bean 要加注解
    @Bean
    //方法名为bean的名字  这个方法返回的对象被装配到容器里
    public SimpleDateFormat simpleDateFormat(){
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    }
}
