package com.is4103.matchub;

import com.is4103.matchub.service.InitService;
import java.io.File;
import java.io.IOException;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.AsyncConfigurerSupport;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableAsync
@EnableScheduling
public class MatchubApplication extends AsyncConfigurerSupport {

    public static String IMAGE_DIR;

    public static void main(String[] args) throws IOException {
        IMAGE_DIR = new File(".").getCanonicalPath() + "/build/resources/main/files/";
        SpringApplication.run(MatchubApplication.class, args);
        Wordnet.run();
    }

    @Bean
    CommandLineRunner init(InitService initService) {
        return (evt) -> initService.init();
    }
}
