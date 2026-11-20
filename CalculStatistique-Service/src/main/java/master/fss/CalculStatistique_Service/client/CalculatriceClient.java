package master.fss.CalculStatistique_Service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@FeignClient(name = "Calculatrice-Service")
public interface CalculatriceClient {
    @GetMapping("/api/calcul/somme")
    Double somme(@RequestParam("a") double a, @RequestParam("b") double b);
}




