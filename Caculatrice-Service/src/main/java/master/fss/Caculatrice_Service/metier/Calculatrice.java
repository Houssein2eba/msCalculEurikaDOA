package master.fss.Caculatrice_Service.metier;

import org.springframework.stereotype.Service;

import java.time.Clock;

@Service
public class Calculatrice implements ICalculatrice {
    public double somme(double a, double b) {
        System.out.println("a="+a+" b="+b);
        return a + b; }
    public double soustraction(double a, double b) { return a - b; }
    public double multiplication(double a, double b) { return a * b; }
    public double division(double a, double b) {
        if (b == 0) throw new ArithmeticException("Division par zéro");
        return a / b;
    }
}