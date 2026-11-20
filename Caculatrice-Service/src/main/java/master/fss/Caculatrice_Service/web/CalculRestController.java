package master.fss.Caculatrice_Service.web;

import master.fss.Caculatrice_Service.metier.Calculatrice;
import master.fss.Caculatrice_Service.model.Resultat;
import master.fss.Caculatrice_Service.repository.ResultatRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/calcul")
public class CalculRestController {

    private final Calculatrice calculatrice;
    private final ResultatRepository resultatRepository;

    public CalculRestController(Calculatrice calculatrice, ResultatRepository resultatRepository) {
        this.calculatrice = calculatrice;
        this.resultatRepository = resultatRepository;
    }

    @GetMapping("/somme")
    public double somme(@RequestParam double a, @RequestParam double b) {
        double val = calculatrice.somme(a, b);
        Resultat r = new Resultat(a, b, "addition", val);
        resultatRepository.save(r);
        return val;
    }





    @GetMapping("/resultats")
    public List<Resultat> list() {
        return resultatRepository.findAll();
    }


    // autres opérations...
    @GetMapping("/test")
    public String test() {
        return "Service de statistiques actif !";
    }

}