
package mz.co.fer.DTO;

/**
 * classe contendo dados do cliente
 * @author Deockilion
 */
public class CustomerVD extends Id {
    
    private String empresa;
    private int nuit;
    private String endereco;


    /**
     * @return the empresa
     */
    public String getEmpresa() {
        return empresa;
    }

    /**
     * @param empresa the empresa to set
     */
    public void setEmpresa(String empresa) {
        this.empresa = empresa;
    }

    /**
     * @return the nuit
     */
    public int getNuit() {
        return nuit;
    }

    /**
     * @param nuit the nuit to set
     */
    public void setNuit(int nuit) {
        this.nuit = nuit;
    }

    /**
     * @return the endereco
     */
    public String getEndereco() {
        return endereco;
    }

    /**
     * @param endereco the endereco to set
     */
    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }
    
}
