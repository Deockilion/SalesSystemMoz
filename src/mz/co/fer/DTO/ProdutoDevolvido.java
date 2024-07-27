
package mz.co.fer.DTO;

/**
 *
 * @author Deockilion
 */
public class ProdutoDevolvido {
    
    private Produto produto;
    private String idDoReciboDaTroca;

    public ProdutoDevolvido(Produto produto, String idDoReciboDaTroca) {
        this.produto = produto;
        this.idDoReciboDaTroca = idDoReciboDaTroca;
    }

    /**
     * @return the produto
     */
    public Produto getProduto() {
        return produto;
    }

    /**
     * @param produto the produto to set
     */
    public void setProduto(Produto produto) {
        this.produto = produto;
    }

    /**
     * @return the idDoReciboDaTroca
     */
    public String getIdDoReciboDaTroca() {
        return idDoReciboDaTroca;
    }

    /**
     * @param idDoReciboDaTroca the idDoReciboDaTroca to set
     */
    public void setIdDoReciboDaTroca(String idDoReciboDaTroca) {
        this.idDoReciboDaTroca = idDoReciboDaTroca;
    }
    
}
