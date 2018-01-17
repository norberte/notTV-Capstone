package spring;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;

import spring.storage.StorageProperties;
import spring.storage.StorageService;

@SpringBootApplication
@EnableConfigurationProperties(StorageProperties.class)
public class Application implements CommandLineRunner{
    
    @Autowired
    JdbcTemplate jdbcTemplate;
    
    public static void main(String[] args) {
	SpringApplication.run(Application.class, args);
    }
    
   //just for setting up test database stuff, don't leave this in the final app
    @Override
    public void run(String... strings){
        jdbcTemplate.execute("Drop Table Video If Exists;");
        String sql = "Create table Video("
                + "id Serial Primary Key,"
                + "title Varchar"
                + ");";
        
        jdbcTemplate.execute(sql);
        
        sql = "Insert Into Video (title) Values ('test');";
        jdbcTemplate.update(sql);
    }
    
    @Bean
    CommandLineRunner init(StorageService storageService) {
	return (args) -> {
	    storageService.init();
	};
    }
}
