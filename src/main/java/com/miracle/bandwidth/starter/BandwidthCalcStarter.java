package com.miracle.bandwidth.starter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;

import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@ComponentScan({ "com.miracle.database.connection", "com.miracle.controller", "com.miracle.config",
		"com.miracle.utility", "com.miracle.bandwidth.*" })
@EnableSwagger2
//@EnableMongoRepositories({ "com.miracle.database.repositories" })
public class BandwidthCalcStarter extends SpringBootServletInitializer {

	public static void main(String[] args) {
		SpringApplication.run(BandwidthCalcStarter.class, args);
	}

	@Override
	public SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(BandwidthCalcStarter.class);
	}
}
