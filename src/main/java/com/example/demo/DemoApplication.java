package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.web.WebApplicationInitializer;

@SpringBootApplication
public class DemoApplication extends SpringBootServletInitializer  {
//    @Override
//    protected SpringApplicationBuilder configure(SpringApplicationBuilder applicationBuilder){
//        return applicationBuilder.sources(DemoApplication.class);
//    }
    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

}
