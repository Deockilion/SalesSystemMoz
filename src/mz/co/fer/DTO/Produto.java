package mz.co.fer.DTO;

/**
 * Classe para objetos do tipo Produto, onde serão contidos, valores e métodos
 * para o mesmo.
 *
 * @author Fernando
 */
public class Produto extends Id {

    private int codigo;
    private String descricao;
    private double qtdArmazem;
    private double preco;
    private double qtdLoja;
    private double precoVenda;
    private double subtotal;
    private String type;
    private byte[] imagem;

    public Produto() {
        this.codigo = 0;
        this.descricao = "";
        this.qtdLoja = 0;
        this.qtdArmazem = 0;
        this.preco = 0;
        this.precoVenda = 0;
        this.subtotal = 0;
        this.imagem = null;
    }

    /**
     *
     * @param id_codigo
     * @param descricao
     * @param quantidade
     * @param preco
     * @param subtotal
     */
    public Produto(int id_codigo, String descricao, double quantidade, double preco, double subtotal) {

        this.codigo = id_codigo;
        this.descricao = descricao;
        this.qtdArmazem = quantidade;
        this.preco = preco;
        this.subtotal = subtotal;
    }

    /**
     * Método para retorno do id do produto
     *
     * @return id do produto
     */
    public int getCodigo() {
        return codigo;
    }

    /**
     * Método para inserir/modificar o id do produto
     *
     * @param codigo
     */
    public void setCodigo(int codigo) {
        this.codigo = codigo;
    }

    /**
     * Método para retorno da Descrição do produto
     *
     * @return
     */
    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    /**
     * Método para retorno da quantidade do produto no armazem
     *
     * @return qtdArmazem
     */
    public double getQtdArmazem() {
        return qtdArmazem;
    }

    public void setQtdArmazem(double quantidade) {
        this.qtdArmazem = quantidade;
    }

    /**
     * Método para retorno do preço do produto
     *
     * @return preco
     */
    public double getPreco() {
        return preco;
    }

    /**
     *
     * @param preco the preco to set
     *
     */
    public void setPreco(double preco) {
        this.preco = preco;
    }

    /**
     * Método para retorno da quantidade do produto na Loja
     *
     * @return the qtdLoja
     */
    public double getQtdLoja() {
        return qtdLoja;
    }

    /**
     * @param qtdLoja the qtdLoja to set
     */
    public void setQtdLoja(double qtdLoja) {
        this.qtdLoja = qtdLoja;
    }

    /**
     * Método para retorno do preço de venda
     *
     * @return the precoVenda
     */
    public double getPrecoVenda() {
        return precoVenda;
    }

    /**
     * @param precoVenda the precoVenda to set
     */
    public void setPrecoVenda(double precoVenda) {
        this.precoVenda = precoVenda;
    }

    /**
     * Método para retorno do subtotal
     *
     * @return the subtotal
     */
    public double getSubtotal() {
        return subtotal;
    }

    /**
     * @param subtotal the subtotal to set
     */
    public void setSubtotal(double subtotal) {
        this.subtotal = subtotal;
    }

    /**
     * @return the imagem
     */
    public byte[] getImagem() {
        return imagem;
    }

    /**
     * @param imagem the imagem to set
     */
    public void setImagem(byte[] imagem) {
        this.imagem = imagem;
    }

    public String toStringP() {
        return this.codigo + "-" + this.descricao;
    }

    /**
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(String type) {
        this.type = type;
    }

}
