
package mz.co.fer.DTO;

import java.util.Date;
import java.util.List;

/**
 * classe contendo o extrato da venda
 * @author Deockilion
 */
public class Caixa extends Fecho {

    private int idValor;
    private Date hora;
    private double cash;
    private double card;
    private List<Caixa> valores;
    
    public Caixa(String nome, double cash, double card){
        super(nome);
        this.cash = cash;
        this.card = card;
    }
    public Caixa(){
        this.idValor = 0;
        this.hora = null;
        this.cash = 0;
        this.card = 0;
        this.valores = null;
    }

    /**
     * @return the idValor
     */
    public int getIdValor() {
        return idValor;
    }

    /**
     * @param idValor the idValor to set
     */
    public void setIdValor(int idValor) {
        this.idValor = idValor;
    }

    /**
     * @return the hora
     */
    public Date getHora() {
        return hora;
    }

    /**
     * @param hora the hora to set
     */
    public void setHora(Date hora) {
        this.hora = hora;
    }

    /**
     * @return the cash
     */
    public double getCash() {
        return cash;
    }

    /**
     * @param cash the cash to set
     */
    public void setCash(double cash) {
        this.cash = cash;
    }

    /**
     * @return the card
     */
    public double getCard() {
        return card;
    }

    /**
     * @param card the card to set
     */
    public void setCard(double card) {
        this.card = card;
    }

    /**
     * @return the valores
     */
    public List<Caixa> getValores() {
        return valores;
    }

    /**
     * @param valores the valores to set
     */
    public void setValores(List<Caixa> valores) {
        this.valores = valores;
    }
    
    
    
}
