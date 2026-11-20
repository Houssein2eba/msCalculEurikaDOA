package master.fss.CalculStatistique_Service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
public class CalculStatistiqueServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(CalculStatistiqueServiceApplication.class, args);
	}

}




