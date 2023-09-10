package com.fly.config;

import com.alibaba.fastjson2.JSONReader;
import com.alibaba.fastjson2.JSONWriter;
import com.alibaba.fastjson2.support.config.FastJsonConfig;
import com.alibaba.fastjson2.support.spring.http.converter.FastJsonHttpMessageConverter;
import com.alibaba.fastjson2.support.spring.webservlet.view.FastJsonJsonView;
import com.fly.handler.AccessLimitHandlerInterceptor;
import com.fly.handler.PageHandlerInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ViewResolverRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;

import static com.fly.constant.GenericConst.ZERO;

/**
 * Spring MVC 配置类
 * @author Milk
 */

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Resource
    private PageHandlerInterceptor pageHandlerInterceptor;

    @Resource
    private AccessLimitHandlerInterceptor accessLimitHandlerInterceptor;


    /**
     * 执行 HTTP 请求
     */
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
    /**
     * 将拦截器注册到 Spring MVC中
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(accessLimitHandlerInterceptor);
        registry.addInterceptor(pageHandlerInterceptor);
    }

    /**
     * 配置全局序列化方式
     */
    @Bean
    public FastJsonConfig fastJsonConfig(){
        FastJsonConfig config = new FastJsonConfig();
        config.setReaderFeatures(JSONReader.Feature.FieldBased);
        config.setWriterFeatures(JSONWriter.Feature.FieldBased);
        return config;
    }

    /**
     * 使用 FastJsonHttpMessageConverter 来替换 Spring MVC 默认的 HttpMessageConverter
     */
    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        FastJsonHttpMessageConverter converter = new FastJsonHttpMessageConverter();
        converter.setFastJsonConfig(fastJsonConfig());
        converter.setSupportedMediaTypes(Collections.singletonList(MediaType.APPLICATION_JSON));
        // 排到第一
        converters.add(ZERO, converter);
    }

    /**
     * 使用 FastJsonJsonView 来设置 Spring MVC 默认的视图模型解析器
     */
    @Override
    public void configureViewResolvers(ViewResolverRegistry registry) {
        FastJsonJsonView fastJsonJsonView = new FastJsonJsonView();
        fastJsonJsonView.setFastJsonConfig(fastJsonConfig());
        registry.enableContentNegotiation(fastJsonJsonView);
    }
}
