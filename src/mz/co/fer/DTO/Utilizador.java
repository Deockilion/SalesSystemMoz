package mz.co.fer.DTO;

/**
 *
 * @author Fernando
 */
public class Utilizador extends Id {
    
    private String nome;
    private String username;
    protected String senha;
    private boolean acessoCaixa;
    private boolean acessoGestao;
    private boolean acessoContabilista;
    private boolean acessoAdmin;
    private Double totalVendas;
    private double totalVendasCard;
    

    public Utilizador(int id_usuario, String nome, String username, String senha, boolean acessoCaixa, boolean acessoGestao, boolean acessoContabilista, boolean acessoAdmin) {
        super(id_usuario);
        this.nome = nome;
        this.username = username;
        this.senha = senha;
        this.acessoCaixa = acessoCaixa;
        this.acessoGestao = acessoGestao;
        this.acessoContabilista = acessoContabilista;
        this.acessoAdmin = acessoAdmin;
        
    }
    public Utilizador(String nome, Double totalVendas, Double totalVendasCard) {
        super(0);
        this.nome = nome;
        this.totalVendas = totalVendas;
        this.totalVendasCard = totalVendasCard;
    }

    public Utilizador() {
        
    }
    public Utilizador(String nome) {
        this.nome = nome;
    }

    
    public boolean isAcessoAdmin() {
        return acessoAdmin;
    }

    public void setAcessoAdmin(boolean acessoAdmin) {
        this.acessoAdmin = acessoAdmin;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public boolean isAcessoCaixa() {
        return acessoCaixa;
    }

    public void setAcessoCaixa(boolean acessoCaixa) {
        this.acessoCaixa = acessoCaixa;
    }

    public boolean isAcessoGestao() {
        return acessoGestao;
    }

    public void setAcessoGestao(boolean acessoGestao) {
        this.acessoGestao = acessoGestao;
    }

    public boolean isAcessoContabilista() {
        return acessoContabilista;
    }

    public void setAcessoContabilista(boolean acessoContabilista) {
        this.acessoContabilista = acessoContabilista;
    }

    public boolean isAdministrador() {
        return acessoCaixa && acessoGestao && acessoContabilista && acessoAdmin;
    }

    public void setTotalVendas(Double totalVendas) {
        this.totalVendas = totalVendas;
    }

    /**
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * @param username the username to set
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * @return the totalVendasCard
     */
    public double getTotalVendasCard() {
        return totalVendasCard;
    }

    /**
     * @param totalVendasCard the totalVendasCard to set
     */
    public void setTotalVendasCard(double totalVendasCard) {
        this.totalVendasCard = totalVendasCard;
    }

    /**
     * @return the totalVendas
     */
    public Double getTotalVendas() {
        return totalVendas;
    }

   
}


