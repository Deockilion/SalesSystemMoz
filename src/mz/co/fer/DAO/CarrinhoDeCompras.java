package mz.co.fer.DAO;

import mz.co.fer.Conexao.Conecta;
import mz.co.fer.DTO.Produto;
import java.util.List;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import javax.swing.JOptionPane;

/**
 * Classe com o CRUD para salvar vendas
 * @author Deockilion
 */
public class CarrinhoDeCompras {

    private int numeroDoCarrinho;

    /**
     * @return the numeroDoCarrinho
     */
    public int getNumeroDoCarrinho() {
        return numeroDoCarrinho;
    }

    /**
     * @param numeroDoCarrinho the numeroDoCarrinho to set
     */
    public void setNumeroDoCarrinho(int numeroDoCarrinho) {
        this.numeroDoCarrinho = numeroDoCarrinho;
    }

    public void salvarCarrinho(List<Produto> lista) {
        int proximoIDfatura = 0;
        
        Connection conn = Conecta.getConnection();
        PreparedStatement stmtt = null;
        ResultSet rs = null;

        try {

            stmtt = conn.prepareStatement("SELECT MAX(numero) AS numero FROM tab_carinho");
            rs = stmtt.executeQuery();
            if (rs.next()) {
                proximoIDfatura = rs.getInt("numero");
                proximoIDfatura++;
                setNumeroDoCarrinho(proximoIDfatura);
            } else {
                // Se não há registros, começar com o numero 20000
                proximoIDfatura = 20000;
                setNumeroDoCarrinho(proximoIDfatura);
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro: " + e.getMessage());
            System.out.println(e.getMessage());
        } finally {
            Conecta.closeConnection(conn, stmtt, rs);
        }

        try (Connection con = Conecta.getConnection(); PreparedStatement stmt = con.prepareStatement("INSERT INTO tab_carinho (numero)VALUES(?)", Statement.RETURN_GENERATED_KEYS); PreparedStatement stmtProduto = con.prepareStatement("INSERT INTO tab_produtos_salvos (id_carrinho, codigo, descricao, quantidade, preco, subtotal) VALUES (?, ?, ?, ?, ?, ?)")) {

            con.setAutoCommit(false);

            stmt.setInt(1, proximoIDfatura);
            stmt.executeUpdate();

            ResultSet generatedKeys = stmt.getGeneratedKeys();
            int idCarrinho = -1;

            if (generatedKeys.next()) {
                idCarrinho = generatedKeys.getInt(1);

            } else {
                throw new SQLException("Falha ao obter o ID da venda, nenhum ID gerado.");
            }

            for (Produto produto : lista) {
                stmtProduto.setInt(1, idCarrinho);
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
        }
    }

    public List<Produto> lerCarrinho(int id) {
        Connection con = Conecta.getConnection();
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Produto> produtos = new ArrayList<>();

        try {
            stmt = con.prepareStatement("SELECT * FROM tab_produtos_salvos ps JOIN tab_carinho tc ON ps.id_carrinho = tc.id where numero = ?");
            stmt.setInt(1, id);
            rs = stmt.executeQuery();

            while (rs.next()) {
                Produto produto = new Produto();
                produto.setId(rs.getInt("id_carrinho"));
                produto.setCodigo(rs.getInt("codigo"));
                produto.setDescricao(rs.getString("descricao"));
                produto.setQtdArmazem(rs.getInt("quantidade"));
                produto.setPreco(rs.getDouble("preco"));
                produto.setSubtotal(rs.getDouble("subtotal"));
                produtos.add(produto);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro: " + e);
        } finally {
            Conecta.closeConnection(con, stmt, rs);
        }

        return produtos;

    }

    public void deletarCarrinho(int number) {
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = Conecta.getConnection();
            conn.setAutoCommit(false); // Desativa o autocommit para poder executar a transação
            
            stmt = conn.prepareStatement("DELETE FROM tab_carinho WHERE numero = ?");
            stmt.setInt(1, number);
            stmt.executeUpdate();

            conn.commit(); // Confirma a transação
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro ao deletar carrinho e produtos salvos: " + e.getMessage());
            System.out.println("Erro ao deletar carrinho e produtos salvos: " + e.getMessage());
        } finally {
            Conecta.closeConnection(null, stmt); 
        }
    }

    public int verificarCarrinhoExiste(int numero) {
        Connection con = Conecta.getConnection();
        PreparedStatement stmt = null;
        int quantidadeRegistros = 0;
        try {
            stmt = con.prepareStatement("SELECT numero FROM tab_carinho WHERE numero = ?");
            stmt.setInt(1, numero);
            try (ResultSet resultSet = stmt.executeQuery()) {
                if (resultSet.next()) {
                    quantidadeRegistros = resultSet.getInt("numero");
                    //return quantidadeRegistros;
                }
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro FechoDAO" + e.getMessage());
        } finally {
            Conecta.closeConnection(con, stmt);
        }
        return quantidadeRegistros;
    }
}
