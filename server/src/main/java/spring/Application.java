package spring;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ImportResource;

import spring.storage.StorageProperties;
import spring.storage.StorageService;


@SpringBootApplication
@ImportResource("classpath:app-config.xml")
@EnableConfigurationProperties(StorageProperties.class)
public class Application {
    public static void main(String[] args) {
	SpringApplication.run(Application.class, args);
    }

    @Bean
    CommandLineRunner init(StorageService storageService) {
	return (args) -> {
	    storageService.init();
	};
    }
}
