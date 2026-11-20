package master.fss.CalculStatistique_Service.service;

import master.fss.CalculStatistique_Service.client.CalculatriceClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/stats")
public class StatistiqueController {
    private final CalculatriceClient calculatriceClient;

    public StatistiqueController(CalculatriceClient calculatriceClient) {
        this.calculatriceClient = calculatriceClient;
    }

    @GetMapping("/moyenne")
    public Double moyenne(@RequestParam double a, @RequestParam double b) {
        Double somme = calculatriceClient.somme(a, b);
        return somme / 2;
    }


}