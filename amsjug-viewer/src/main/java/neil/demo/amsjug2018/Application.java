package neil.demo.amsjug2018;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * <p>Start, run and end under {@code Spring Boot}  control.
 * </p>
 */
@SpringBootApplication
public class Application {
	
	static {
		// No controlling parent window
		System.setProperty("java.awt.headless", "false");
		
		// Reduce contention, data updates all sent to same panel
		System.setProperty("hazelcast.client.event.thread.count", "1");
	}

    public static void main(String[] args) throws Exception {
    	SpringApplication.run(Application.class, args);
    }

}
