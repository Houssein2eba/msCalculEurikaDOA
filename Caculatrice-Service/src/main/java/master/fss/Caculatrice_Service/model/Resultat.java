package master.fss.Caculatrice_Service.model;

import jakarta.persistence.*;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.Data;

import java.io.Serializable;

@XmlRootElement

@Entity
@Table(name = "resultat")
@Data // génère getters, setters, toString, equals/hashCode
public class Resultat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private double operande1;
    private double operande2;
    private String operation;
    private double valeur;

    public Resultat() {}

    public Resultat(double operande1, double operande2, String operation, double valeur) {
        this.operande1 = operande1;
        this.operande2 = operande2;
        this.operation = operation;
        this.valeur = valeur;
    }

    // Getters et Setters


}
