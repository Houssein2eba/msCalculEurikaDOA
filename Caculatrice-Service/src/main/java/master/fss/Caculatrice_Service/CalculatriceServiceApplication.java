package master.fss.Caculatrice_Service;

import master.fss.Caculatrice_Service.model.Resultat;
import master.fss.Caculatrice_Service.repository.ResultatRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class CalculatriceServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(CalculatriceServiceApplication.class, args);
    }


    @Bean
    public CommandLineRunner initDatabase(ResultatRepository resultatRepository) {
        return args -> {
            // Exemple d'insertion
            Resultat addition = new Resultat(5, 3, "addition", 8);
            Resultat multiplication = new Resultat(4, 2, "multiplication", 8);

            resultatRepository.save(addition);
            resultatRepository.save(multiplication);

            // Affichage
            resultatRepository.findAll().forEach(System.out::println);
        };
    }

}
