
package mz.co.fer.DTO;

/**
 *
 * @author Deockilion
 */
public class EntradaDeStock extends Authorized{
        
    private float qtdAnterior;
    private float qtdAtual;
    private int codProd;

    public EntradaDeStock(String autorizedBy, String operacao, int codProd,float qtdAnterior, float qtdAtual) {
        super(autorizedBy, operacao);
        this.qtdAnterior = qtdAnterior;
        this.qtdAtual = qtdAtual;
        this.codProd =codProd;
    }

    public EntradaDeStock() {
        
    }

    /**
     * @return the qtdAnterior
     */
    public float getQtdAnterior() {
        return qtdAnterior;
    }

    /**
     * @param qtdAnterior the qtdAnterior to set
     */
    public void setQtdAnterior(float qtdAnterior) {
        this.qtdAnterior = qtdAnterior;
    }

    /**
     * @return the qtdAtual
     */
    public float getQtdAtual() {
        return qtdAtual;
    }

    /**
     * @param qtdAtual the qtdAtual to set
     */
    public void setQtdAtual(float qtdAtual) {
        this.qtdAtual = qtdAtual;
    }

    /**
     * @return the codProd
     */
    public int getCodProd() {
        return codProd;
    }

    /**
     * @param codProd the codProd to set
     */
    public void setCodProd(int codProd) {
        this.codProd = codProd;
    }
    
}
