package com.example.karaoke;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class KaraokeApplication {

    public static void main(String[] args) {
        SpringApplication.run(KaraokeApplication.class, args);
    }

}
