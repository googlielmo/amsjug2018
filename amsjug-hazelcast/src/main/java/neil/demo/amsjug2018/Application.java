package neil.demo.amsjug2018;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * <p>Start, run and end under {@code Spring Boot}  control,
 * but also using {@code Spring Shell} for interactive CLI.
 * </p>
 */
@SpringBootApplication
public class Application {

    public static void main(String[] args) throws Exception {
    	SpringApplication.run(Application.class, args);
    }
}