package mz.co.fer.DTO;

import java.time.LocalDate;
import java.util.List;

public class ProdutoPromocao {

    private int codigoProd;
    private double precoNormal;
    private double precoPromocional;
    private LocalDate dataFimPromocao;
    private LocalDate dataInicioPromocao;
    private List<ProdutoPromocao> prodPromo;

    // Construtor
    public ProdutoPromocao(int codigoProd, double precoNormal, double precoPromocional, LocalDate dataInicioPromocao, LocalDate dataFimPromocao) {
        this.codigoProd = codigoProd;
        this.precoNormal = precoNormal;
        this.precoPromocional = precoPromocional;
        this.dataInicioPromocao = dataInicioPromocao;
        this.dataFimPromocao = dataFimPromocao;
    }

    public ProdutoPromocao() {
        this.codigoProd = 0;
        this.precoPromocional = 0;
        this.precoNormal = 0;
        this.dataInicioPromocao = null;
        this.dataFimPromocao = null;
        this.prodPromo = null;
    }

    // Getters e setters
    public int getCodigoProd() {
        return codigoProd;
    }

    public double getPrecoNormal() {
        return precoNormal;
    }

    public double getPrecoPromocional() {
        return precoPromocional;
    }

    /**
     * @return the dataFimPromocao
     */
    public LocalDate getDataFimPromocao() {
        return dataFimPromocao;
    }

    /**
     * @param dataFimPromocao the dataFimPromocao to set
     */
    public void setDataFimPromocao(LocalDate dataFimPromocao) {
        this.dataFimPromocao = dataFimPromocao;
    }

    /**
     * @return the dataInicioPromocao
     */
    public LocalDate getDataInicioPromocao() {
        return dataInicioPromocao;
    }

    /**
     * @param dataInicioPromocao the dataInicioPromocao to set
     */
    public void setDataInicioPromocao(LocalDate dataInicioPromocao) {
        this.dataInicioPromocao = dataInicioPromocao;
    }

    /**
     * @param codigoProd the codigoProd to set
     */
    public void setCodigoProd(int codigoProd) {
        this.codigoProd = codigoProd;
    }

    /**
     * @param precoNormal the precoNormal to set
     */
    public void setPrecoNormal(double precoNormal) {
        this.precoNormal = precoNormal;
    }

    /**
     * @param precoPromocional the precoPromocional to set
     */
    public void setPrecoPromocional(double precoPromocional) {
        this.precoPromocional = precoPromocional;
    }

    /**
     * @param prodPromo the prodPromo to set
     */
    public void setProdPromo(List<ProdutoPromocao> prodPromo) {
        this.prodPromo = prodPromo;
    }

}
