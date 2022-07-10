package reggie.config;


import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import reggie.common.JacksonObjectMapper;

import java.util.List;

@Slf4j
@Configuration
public class WebMvcConfig extends WebMvcConfigurationSupport {
//    /**
//     * 设置静态资源映射
//     * @param registry
//     */
//    @Override
//    protected void addResourceHandlers(ResourceHandlerRegistry registry) {
//        log.info("开始进行静态资源映射...");
////        registry.addResourceHandler("/backend/**").addResourceLocations("classpath:/backend/");
////        registry.addResourceHandler("/front/**").addResourceLocations("classpath:/front/");
//    }


    /**
     * 在使用WebMvcConfigurer接口时候，重写了参数解析器，而忽略了配置springMVC默认拦截静态资源
     * 换句话说，因为继承了WebMvcConfigurationSupport，所以 addResourceHandlers方法已经存在
     * 下面也只是对这个方法的重写，所以即使我不重写，只要继承了WebMvcConfigurationSupport就会修改MVC的静态资源映射
     * 所以手动加上这个代码，并且映射到static就好了
     * @param registry
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/**").addResourceLocations("classpath:/static/");
    }


    /**
     * 拓展MVC框架的消息转换器
     */
    @Override
    protected void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        log.info("扩展消息转换器...");
        //创建消息转换器对象
        MappingJackson2HttpMessageConverter messageConverter = new MappingJackson2HttpMessageConverter();
        //设置对象转换器，底层使用Jackson将Java对象转化为json
        messageConverter.setObjectMapper(new JacksonObjectMapper());
        //将上面的消息转换器对象追加到MVC框架的转换器集合中（List<HttpMessageConverter<?>> converters）
        converters.add(0,messageConverter);
    }
}


