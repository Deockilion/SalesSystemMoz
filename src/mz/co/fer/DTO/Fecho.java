
package mz.co.fer.DTO;

import java.util.Date;

/**
 * Classe contendo dados do fecho
 * @author Fernando
 */
public class Fecho extends Id{
    
    private String nome;
    private Date data;
    private double valorTotal;
    private double valorFalta;
    
    public Fecho(String nome){
        this.nome = nome;
    }
    public Fecho(){
       this.nome = "";
       this.data = null;
       this.valorFalta = 0;
       this.valorTotal = 0;
    }

    /**
     * @return the data
     */
    public Date getData() {
        return data;
    }

    /**
     * @param data the data to set
     */
    public void setData(Date data) {
        this.data = data;
    }

    /**
     * @return the valorTotal
     */
    public double getValorTotal() {
        return valorTotal;
    }

    /**
     * @param valorTotal the valorTotal to set
     */
    public void setValorTotal(double valorTotal) {
        this.valorTotal = valorTotal;
    }

    /**
     * @return the valorFalta
     */
    public double getValorFalta() {
        return valorFalta;
    }

    /**
     * @param valorFalta the valorFalta to set
     */
    public void setValorFalta(double valorFalta) {
        this.valorFalta = valorFalta;
    }

    /**
     * @return the nome
     */
    public String getNome() {
        return nome;
    }

    /**
     * @param nome the nome to set
     */
    public void setNome(String nome) {
        this.nome = nome;
    }
    
    
}
