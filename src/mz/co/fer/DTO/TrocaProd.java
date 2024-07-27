
package mz.co.fer.DTO;

/**
 *
 * @author Deockilion
 */
public class TrocaProd extends Vendas{
    
    private String idDoReciboDaTroca;
    private String autorizedBy;
    private Produto produtoAdevolver;

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
     * @return the produtosDevolvidos
     */

    /**
     * @param produtoAdevolver
     * 
     */
    public void setProdutoAdevolver(Produto produtoAdevolver) {
        this.produtoAdevolver = produtoAdevolver;
    }

    /**
     * @param idDoReciboDaTroca the idDoReciboDaTroca to set
     */
    public void setIdDoReciboDaTroca(String idDoReciboDaTroca) {
        this.idDoReciboDaTroca = idDoReciboDaTroca;
    }

    /**
     * @return the idDoReciboDa
     */
    public String getIdDoReciboDaTroca() {
        return idDoReciboDaTroca;
    }

    /**
     * @return the produtoAdevolver
     */
    public Produto getProdutoAdevolver() {
        return produtoAdevolver;
    }   
}
