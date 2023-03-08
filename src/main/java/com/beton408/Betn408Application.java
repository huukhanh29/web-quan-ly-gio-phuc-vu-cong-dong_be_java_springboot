package com.beton408;

import com.beton408.service.FilesStorageService;
import jakarta.annotation.Resource;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Betn408Application implements CommandLineRunner {
    @Resource
    FilesStorageService storageService;
    public static void main(String[] args) {
        SpringApplication.run(Betn408Application.class, args);
    }
    @Override
    public void run(String... arg) throws Exception {
        storageService.init();
    }
}
