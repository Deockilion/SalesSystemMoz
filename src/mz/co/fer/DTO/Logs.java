
package mz.co.fer.DTO;

import java.util.Date;

/**
 *
 * @author Deockilion
 */
public class Logs  extends Utilizador{
    private Date dataHora;
    
    public Logs(String nome){
       super(nome); 
    }
    public Logs(){}

    /**
     * @return the dataHora
     */
    public Date getDataHora() {
        return dataHora;
    }

    /**
     * @param dataHora the dataHora to set
     */
    public void setDataHora(Date dataHora) {
        this.dataHora = dataHora;
    }
    
}
