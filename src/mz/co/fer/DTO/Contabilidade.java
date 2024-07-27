
package mz.co.fer.DTO;

import java.util.Date;

/**
 * Classe contendo dados do pagamento á cartão
 * @author Fernando
 */
public class Contabilidade extends Id {
 
    private Date data;
    private String idRecibo;
    private int nrpos;
    private int expdate;
    private int card;
    private double valor;


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
     * @return the idRecibo
     */
    public String getIdRecibo() {
        return idRecibo;
    }

    /**
     * @param idRecibo the idRecibo to set
     */
    public void setIdRecibo(String idRecibo) {
        this.idRecibo = idRecibo;
    }

    /**
     * @return the nrpos
     */
    public int getNrpos() {
        return nrpos;
    }

    /**
     * @param nrpos the nrpos to set
     */
    public void setNrpos(int nrpos) {
        this.nrpos = nrpos;
    }

    /**
     * @return the expdate
     */
    public int getExpdate() {
        return expdate;
    }

    /**
     * @param expdate the expdate to set
     */
    public void setExpdate(int expdate) {
        this.expdate = expdate;
    }

    /**
     * @return the card
     */
    public int getCard() {
        return card;
    }

    /**
     * @param card the card to set
     */
    public void setCard(int card) {
        this.card = card;
    }

    /**
     * @return the valor
     */
    public double getValor() {
        return valor;
    }

    /**
     * @param valor the valor to set
     */
    public void setValor(double valor) {
        this.valor = valor;
    }   
}
