package com.itheima.reggie.config;

import com.itheima.reggie.common.JacksonObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.cbor.MappingJackson2CborHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import java.util.List;

@Slf4j
@Configuration
public class WebMvcConfig extends WebMvcConfigurationSupport {

    /**
     *  设置 静态资源映射
     * @param registry
     */
    @Override
    protected void addResourceHandlers(ResourceHandlerRegistry registry) {
        log.info("开始进行静态资源映射..");
        registry.addResourceHandler("/backend/**")
                .addResourceLocations("classpath:/backend/");
        registry.addResourceHandler("/front/**")
                .addResourceLocations("classpath:/front/");
    }

    /**  mvc自带默认转化器
     *
     *  扩展mvc框架的消息转换器
     * @param converters
     */
    @Override
    protected void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        log.info("扩展消息转化器···");
        //  创建新的转化器   将java对象转化为JSON数据
        MappingJackson2HttpMessageConverter messageConverter= new MappingJackson2HttpMessageConverter();

        // 设置对象转化器  自己封装的 JacksonObjectMapper 该通用类
        messageConverter.setObjectMapper(new JacksonObjectMapper());

        /*
             将上面转换器对象追加到mvc框架的转换器集合中
           注意  转化器会根据索引顺序来进行调用
        *   注意 要将索引设置为0 这样调用的就是我们 创建新的这个转化器
        * */
        converters.add(0,messageConverter);
    }
}
