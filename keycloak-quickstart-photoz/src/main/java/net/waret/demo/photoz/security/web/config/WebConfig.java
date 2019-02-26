package net.waret.demo.photoz.security.web.config;

import net.waret.demo.photoz.security.web.resolver.UserDetailsArgumentResolver;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(createUserDetailsResolver());
    }

    @Bean
    public UserDetailsArgumentResolver createUserDetailsResolver() {
        return new UserDetailsArgumentResolver();
    }
}
