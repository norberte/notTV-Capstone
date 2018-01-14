package springbackend;

import java.nio.file.Paths;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@SpringBootApplication
public class Application {
    
    //Window and Linux commands to open default browser to localhost:8080
    //private static String command =  "cmd /C start http://localhost:8080";
    private static String command =  "xdg-open http://localhost:8080";
    
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
        
        /*
        try {
            Runtime.getRuntime().exec(command);
        } catch (IOException e) {
            System.out.println("unable to start browser.");
            e.printStackTrace();
        }
        */
    }
}
