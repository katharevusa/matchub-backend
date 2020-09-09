package com.is4103.matchub;

import com.is4103.matchub.service.InitService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.AsyncConfigurerSupport;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class MatchubApplication extends AsyncConfigurerSupport {

    public static void main(String[] args) {
        SpringApplication.run(MatchubApplication.class, args);
    }

    @Bean
    CommandLineRunner init(InitService initService) {
        return (evt) -> initService.init();
    }
}
