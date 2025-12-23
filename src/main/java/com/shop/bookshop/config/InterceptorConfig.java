package com.shop.bookshop.config;

import com.shop.bookshop.interceptor.AdminInterceptor;
import com.shop.bookshop.interceptor.ClientLoginInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class InterceptorConfig implements WebMvcConfigurer {

    //注册拦截器
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //注册客户端拦截器
        registry.addInterceptor(new ClientLoginInterceptor())
                .addPathPatterns("/user_center/**")
                .addPathPatterns("/cart/**")
                .addPathPatterns("/order/submit");
        //注册后台管理拦截器
        registry.addInterceptor(new AdminInterceptor())
                .addPathPatterns("/user/**").excludePathPatterns(new String[]{"/user/login","/user/register"})
                .addPathPatterns("/order/**").excludePathPatterns("/order/submit")
                .addPathPatterns("/category/**")
                .addPathPatterns("/upload/book_image")
                .addPathPatterns("/book/**");
    }
}
