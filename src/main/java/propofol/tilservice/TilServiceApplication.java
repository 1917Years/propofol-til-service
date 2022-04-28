package propofol.tilservice;

import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
@EnableJpaAuditing
@EnableFeignClients(basePackages = "propofol.tilservice.api.feign")
@ConfigurationPropertiesScan(basePackages = "propofol.tilservice.api.common.properties")
@EnableEurekaClient
public class TilServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(TilServiceApplication.class, args);
	}

	@Bean
	public BCryptPasswordEncoder createEncoder(){
		return new BCryptPasswordEncoder();
	}

	@Bean
	public ModelMapper createModelMapper(){
		return new ModelMapper();
	}
}
