package mz.co.fer.DTO;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 * @author Fernando
 */
public class Vendas extends Id {

    private Date dataVenda;
    private String operador;
    private List<Produto> produtosVendidos;
    private double valorTotal;
    private String idRecibo;

    public Vendas (String operador, Date dataVenda, List<Produto> arrayList, double valorTotal, String idrecibo) {
        this.dataVenda = dataVenda;
        this.operador = operador;
        this.produtosVendidos = new ArrayList<>(arrayList);
        this.valorTotal = valorTotal;
        this.idRecibo = idrecibo;
        
    }
    public Vendas(){
        this.produtosVendidos = new ArrayList<>();
        
    }
    /**
     * @return the dataVenda
     */
    public Date getDataVenda() {
        return dataVenda;
    }

    /**
     * @param dataVenda the dataVenda to set
     */
    public void setDataVenda(Date dataVenda) {
        this.dataVenda = dataVenda;
    }

    /**
     * @return the operador
     */
    public String getOperador() {
        return operador;
    }

    /**
     * @param operador the operador to set
     */
    public void setOperador(String operador) {
        this.operador = operador;
    }

    /**
     * @return the produtosVendidos
     */
    public List<Produto> getProdutosVendidos() {
        return produtosVendidos;
    }

    /**
     * @param produtosVendidos the produtosVendidos to set
     */
    public void setProdutosVendidos(List<Produto> produtosVendidos) {
        this.produtosVendidos = produtosVendidos;
    }

    /**
     * @return the valorTotal
     */
    public double getValorTotal() {
        return valorTotal;
    }

    /**
     * @param valorTotal the valorTotal to set
     */
    public void setValorTotal(double valorTotal) {
        this.valorTotal = valorTotal;
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

}
