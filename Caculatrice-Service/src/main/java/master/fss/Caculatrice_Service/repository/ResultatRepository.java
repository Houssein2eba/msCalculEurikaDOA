package master.fss.Caculatrice_Service.repository;

import master.fss.Caculatrice_Service.model.Resultat;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResultatRepository  extends JpaRepository<Resultat, Long> {

    // Pas besoin de méthodes supplémentaires pour save(), findAll(), deleteById(), etc.
}
