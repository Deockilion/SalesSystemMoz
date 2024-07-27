package mz.co.fer.DAO;

import mz.co.fer.Conexao.Conecta;
import mz.co.fer.DTO.Produto;
import mz.co.fer.DTO.Vendas;
import java.sql.Connection;
import javax.swing.JOptionPane;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.sql.Statement;
import java.time.Instant;
import java.util.Date;

/**
 *
 * @author Fernando
 */
public class VendasDAO {

    /**
     * Salva a venda e incluí o arraylist de produtos vendidos
     *
     * @param venda
     */
    public void inserirVendaNoBancoDeDados(Vendas venda) {

        try (Connection con = Conecta.getConnection(); PreparedStatement stmt = con.prepareStatement("INSERT INTO tab_vendas (id_recibo, data_venda, operador, valor_total) VALUES (?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS); PreparedStatement stmtProduto = con.prepareStatement("INSERT INTO tab_produtos_vendidos (id_venda, codigo, descricao, quantidade, preco, subtotal) VALUES (?, ?, ?, ?, ?, ?)")) {

            con.setAutoCommit(false);
            Date dataHoraAtual = Date.from(Instant.now());
            java.time.Instant instant = dataHoraAtual.toInstant();
            java.sql.Timestamp sqlTimestamp = new java.sql.Timestamp(instant.toEpochMilli());

            stmt.setString(1, venda.getIdRecibo());
            stmt.setTimestamp(2, sqlTimestamp);
            stmt.setString(3, venda.getOperador());
            stmt.setDouble(4, venda.getValorTotal());
            stmt.executeUpdate();

            ResultSet generatedKeys = stmt.getGeneratedKeys();
            int idVenda = -1;

            if (generatedKeys.next()) {
                idVenda = generatedKeys.getInt(1);

            } else {
                throw new SQLException("Falha ao obter o ID da venda, nenhum ID gerado.");
            }

            for (Produto produto : venda.getProdutosVendidos()) {
                stmtProduto.setInt(1, idVenda);
                stmtProduto.setInt(2, produto.getCodigo());
                stmtProduto.setString(3, produto.getDescricao());
                stmtProduto.setDouble(4, produto.getQtdArmazem());
                stmtProduto.setDouble(5, produto.getPreco());
                stmtProduto.setDouble(6, produto.getSubtotal());
                stmtProduto.executeUpdate();
            }
            con.commit();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro ao cadastrar venda: " + e);
            System.out.println("Salvar: "+e.getMessage());

        }
    }

    public Vendas lerVendaPorIdRecibo(String idRecibo) {
        Connection con = Conecta.getConnection();
        PreparedStatement stmt = null;
        ResultSet rs = null;
        Vendas venda = null;

        try {
            stmt = con.prepareStatement("SELECT * FROM tab_vendas WHERE id_recibo = ?");
            stmt.setString(1, idRecibo);
            rs = stmt.executeQuery();

            if (rs.next()) {
                venda = new Vendas();
                venda.setId(rs.getInt("id_venda"));
                venda.setIdRecibo(rs.getString("id_recibo"));
                venda.setDataVenda(rs.getTimestamp("data_venda"));
                venda.setOperador(rs.getString("operador"));
                venda.setValorTotal(rs.getDouble("valor_total"));

                // Ler os produtos associados à venda
                List<Produto> produtos = lerProdutosPorIdVenda(venda.getId());
                venda.setProdutosVendidos(produtos);
            }
        } catch (SQLException e) {            
            JOptionPane.showMessageDialog(null, "Error: " + e);
            System.out.println(e.getMessage());
        } finally {
            Conecta.closeConnection(con, stmt, rs);
        }

        return venda;
    }

    private List<Produto> lerProdutosPorIdVenda(int idVenda) {
        Connection con = Conecta.getConnection();
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Produto> produtos = new ArrayList<>();

        try {
            stmt = con.prepareStatement("SELECT * FROM tab_produtos_vendidos WHERE id_venda = ?");
            stmt.setInt(1, idVenda);
            rs = stmt.executeQuery();

            while (rs.next()) {
                Produto produto = new Produto();
                produto.setId(rs.getInt("id_venda"));
                produto.setCodigo(rs.getInt("codigo"));
                produto.setDescricao(rs.getString("descricao"));
                produto.setQtdArmazem(rs.getDouble("quantidade"));
                produto.setPreco(rs.getDouble("preco"));
                produto.setSubtotal(rs.getDouble("subtotal"));
                produtos.add(produto);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro: " + e);
            System.out.println(e.getMessage());
        } finally {
            Conecta.closeConnection(con, stmt, rs);
        }

        return produtos;
    }

    public Vendas obterUltimaVenda() {
        Connection con = Conecta.getConnection();
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            stmt = con.prepareStatement("SELECT * FROM tab_vendas ORDER BY data_venda DESC LIMIT 1");
            rs = stmt.executeQuery();
            if (rs.next()) {
                Vendas ultimaVenda = new Vendas();
                ultimaVenda.setId(rs.getInt("id_venda"));
                ultimaVenda.setIdRecibo(rs.getString("id_recibo"));
                ultimaVenda.setOperador(rs.getString("operador"));
                ultimaVenda.setDataVenda(rs.getTimestamp("data_venda"));
                ultimaVenda.setValorTotal(rs.getDouble("valor_total"));

                List<Produto> produtosVendidos = obterProdutosVendidos(ultimaVenda.getId());
                ultimaVenda.setProdutosVendidos(produtosVendidos);

                return ultimaVenda;
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro : " + e);
        } finally {
            Conecta.closeConnection(con, stmt, rs);
        }
        return null;

    }

    private List<Produto> obterProdutosVendidos(int idVenda) {
        List<Produto> produtos = new ArrayList<>();
        Connection con = Conecta.getConnection();
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            stmt = con.prepareStatement("SELECT * FROM tab_produtos_vendidos WHERE id_venda = ?");
            stmt.setInt(1, idVenda);
            rs = stmt.executeQuery();

            while (rs.next()) {
                Produto produto = new Produto();
                //produto.setId(rs.getInt("id_venda"));
                //produto.setIdVenda(rs.getInt("id_venda"));
                produto.setCodigo(rs.getInt("codigo"));
                produto.setDescricao(rs.getString("descricao"));
                produto.setQtdArmazem(rs.getDouble("quantidade"));
                produto.setPreco(rs.getDouble("preco"));
                produto.setSubtotal(rs.getDouble("subtotal"));

                produtos.add(produto);
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Err: " + e);
        } finally {
            Conecta.closeConnection(con, stmt, rs);
        }

        return produtos;
    }

}
