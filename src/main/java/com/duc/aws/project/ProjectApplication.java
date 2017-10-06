package com.duc.aws.project;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ServletComponentScan(value="com.duc.aws.project.security")
@ComponentScan("com.duc.aws.project") 
public class ProjectApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProjectApplication.class, args);
	}
}
