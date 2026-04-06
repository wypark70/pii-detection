package com.atsoft.piidetection;

import com.atsoft.piidetection.config.AppConfig;
import com.atsoft.piidetection.model.User;
import com.atsoft.piidetection.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;
import java.util.Map;

@SpringBootApplication
@Slf4j
public class PiiDetectionApplication {

    public static void main(String[] args) {
        SpringApplication.run(PiiDetectionApplication.class, args);
    }

    @Bean
    CommandLineRunner run(AppConfig config, UserRepository repository) {
        return args -> {
            System.out.println("App Name: " + config.getName());

            // 데이터 삽입
            // jdbcTemplate.update("INSERT INTO users(name, email) VALUES (?, ?)", "홍길동", "hong@test.com");
            // jdbcTemplate.update("INSERT INTO users(name, email) VALUES (?, ?)", "김철수", "kim@test.com");

            // 데이터 조회
            List<User> users = repository.findAll();
            users.forEach(System.out::println);
        };
    }


}
