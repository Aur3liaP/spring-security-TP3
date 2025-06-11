package fr.diginamic.spring_security;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class SpringSecurityApplication {

	public static void main(String[] args) {
		ConfigurableApplicationContext context = SpringApplication.run(SpringSecurityApplication.class, args);
		String secret = context.getEnvironment().getProperty("jwt.secret");
		System.out.println("Secret Key from context: " + secret);
	}

}
