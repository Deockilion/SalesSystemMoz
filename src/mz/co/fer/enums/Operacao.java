
package mz.co.fer.enums;

/**
 *
 * @author Deockilion
 */
public enum Operacao {
    
    
    TROCA(1, "Troca"),
    
    DESCONTO(2, "Desconto"),
    
    REPRINT(3, "Reprint"),
    REPRINTID(4, "Reprint pelo ID"),
    
    
    CODIGO(5, "codigo"),
    
    DESCRICAOP(6, "Descricao"),
    
    QUANTIDADE_LOJA(7, "Quantidade LOJA"),
    
    QTD_ARMAZEM(8, "QTD Armazem"),
    
    PRECO(9, "Preco de Compra"),
    
    PRECO_VENDA(10, "Preco de Venda"),
    TYPE(11, "Tipo de produto"),
    
    IMAGEM(12, "Imagem"),
    
    CHANGEPRICE(13, "Alterar Preço"),
    ADDQTD(14, "Nova Quantidade"),
    TRANSFERIR(15, "Transferir"),
    PROMOCAO(16, "Promoção"),
    
    CALCULAR(17, "Calcular Venda"),
    
    ABRIRCONTA(18, "Abrir Conta"),
    SALVAR(20, "Salvar Conta"),
    
    VD(21, "Venda Dinheiro"),
    QUOTE(22, "Cotação"),
    EXTRATOAtual(23, "Extrato das vendas"),
    EXTRATOAntigo(24, "Extrato das vendas anterior");
    
    
    private final int ID;
    private final String DESCRICAO;
    
    private Operacao(int id, String descricao){
        this.ID = id;
        this.DESCRICAO = descricao;
    }

    /**
     * @return the ID
     */
    public int getID() {
        return ID;
    }

    /**
     * @return the DESCRICAO
     */
    @Override
    public String toString() {
        return this.DESCRICAO;
    }
    
}
