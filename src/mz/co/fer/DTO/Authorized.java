
package mz.co.fer.DTO;

import java.util.Date;

/**
 *
 * @author Deockilion
 */
public class Authorized extends Id{
    
    private String autorizedBy;
    private String operacao;
    private Date dataHora;
    
    public Authorized(String autorizedBy, String operacao){
       this.autorizedBy = autorizedBy;
       this.operacao = operacao;
    }
    public Authorized(){}

    /**
     * @return the autorizedBy
     */
    public String getAutorizedBy() {
        return autorizedBy;
    }

    /**
     * @param autorizedBy the autorizedBy to set
     */
    public void setAutorizedBy(String autorizedBy) {
        this.autorizedBy = autorizedBy;
    }

    /**
     * @return the operacao
     */
    public String getOperacao() {
        return operacao;
    }

    /**
     * @param operacao the operacao to set
     */
    public void setOperacao(String operacao) {
        this.operacao = operacao;
    }

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
