/**
 * It's a Spring Boot application that scans the current package and all sub-packages for Spring components
 */
package com.scoreDEI.scoreDEI;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages={
		"com.scoreDEI.Services", "com.scoreDEI.Repositories"})
@EnableJpaRepositories("com.scoreDEI.*")
@EntityScan("com.scoreDEI.*")
@ComponentScan(basePackages={"com.scoreDEI.*"})
public class ScoreDeiApplication {

	public static void main(String[] args) {
		SpringApplication.run(ScoreDeiApplication.class, args);
	}

}
