package mz.co.fer.DAO;

import mz.co.fer.Conexao.Conecta;
import mz.co.fer.DTO.Produto;
import mz.co.fer.DTO.TrocaProd;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.JOptionPane;

/**
 *
 * @author Deockilion
 */
public class TrocaDAO implements IDAO<TrocaProd>{

    /**
     * Metodo para salvar os dados da devolução dos produtos
     *
     * @param tp
     */
    @Override
    public void save(TrocaProd tp) {

        try (Connection con = Conecta.getConnection(); PreparedStatement stmt = con.prepareStatement("INSERT INTO tab_return (idDaTroca,dataHora,autorizadoPor,operador) VALUES (?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS); PreparedStatement stmtProduto = con.prepareStatement("INSERT INTO tab_produtos_devolvidos (id_venda, codigo, descricao, quantidade, preco, subtotal) VALUES (?, ?, ?, ?, ?,?)")) {

            con.setAutoCommit(false);
            Date dataHoraAtual = Date.from(Instant.now());
            java.time.Instant instant = dataHoraAtual.toInstant();
            java.sql.Timestamp sqlTimestamp = new java.sql.Timestamp(instant.toEpochMilli());

            stmt.setString(1, tp.getIdDoReciboDaTroca());
            stmt.setTimestamp(2, sqlTimestamp);
            stmt.setString(3, tp.getAutorizedBy());
            stmt.setString(4, tp.getOperador());
            stmt.executeUpdate();

            ResultSet generatedKeys = stmt.getGeneratedKeys();
            int idVenda = -1;

            if (generatedKeys.next()) {
                idVenda = generatedKeys.getInt(1);

            } else {
                throw new SQLException("Falha ao obter o ID da venda, nenhum ID gerado.");
            }

            Produto produto = tp.getProdutoAdevolver();
                stmtProduto.setInt(1, idVenda);
                stmtProduto.setInt(2, produto.getCodigo());
                stmtProduto.setString(3, produto.getDescricao());
                stmtProduto.setDouble(4, produto.getQtdArmazem());
                stmtProduto.setDouble(5, produto.getPreco());
                stmtProduto.setDouble(6, produto.getSubtotal());
                stmtProduto.executeUpdate();
            
            con.commit();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
           
        }
    }

    @Override
    public TrocaProd retornar(String id) {
        Connection con = Conecta.getConnection();
        PreparedStatement stmt = null;
        ResultSet rs = null;
        TrocaProd troca = null;

        try {
            stmt = con.prepareStatement("SELECT * FROM tab_return WHERE idDaTroca = ?");
            stmt.setString(1, id);
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                troca = new TrocaProd();
                troca.setId(rs.getInt("id"));
                troca.setIdDoReciboDaTroca(rs.getString("idDaTroca"));
                troca.setDataVenda(rs.getTimestamp("dataHora"));
                troca.setAutorizedBy(rs.getString("autorizadoPor"));
                troca.setOperador(rs.getString("operador"));
                
                List<Produto> produtos = lerProdutosPorIdVenda(troca.getId());
                troca.setProdutosVendidos(produtos);
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error: " + e);
        } finally {
            Conecta.closeConnection(con, stmt, rs);
        }
        return troca;
    }

    private List<Produto> lerProdutosPorIdVenda(int idVenda) {
        Connection con = Conecta.getConnection();
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Produto> produtos = new ArrayList<>();
        try {
            stmt = con.prepareStatement("SELECT * FROM tab_produtos_devolvidos WHERE id_venda = ?");
            stmt.setInt(1, idVenda);
            rs = stmt.executeQuery();

            while (rs.next()) {
                Produto produto = new Produto();
                //produto.setId(rs.getInt("id_venda"));
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

    @Override
    public void update(TrocaProd objeto) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void Delete(TrocaProd objeto) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public List<TrocaProd> read() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

}
