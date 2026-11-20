package master.fss.Eureka_Service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
public class EurekaServiceApplication {
//localhost:8761
	public static void main(String[] args) {
		SpringApplication.run(EurekaServiceApplication.class, args);
	}

}
