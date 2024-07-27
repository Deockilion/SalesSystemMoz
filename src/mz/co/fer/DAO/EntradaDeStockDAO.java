
package mz.co.fer.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.JOptionPane;
import mz.co.fer.Conexao.Conecta;
import mz.co.fer.DTO.EntradaDeStock;

/**
 *
 * @author Deockilion
 */
public class EntradaDeStockDAO implements IDAO<EntradaDeStock> {

    @Override
    public void save(EntradaDeStock objeto) {
        Connection con = Conecta.getConnection();
        PreparedStatement stmt = null;
        try {

            stmt = con.prepareStatement("INSERT INTO tab_entrada_stock (datahora, autorizedby, operacao, produto, qtdanterior, qtdatual)VALUES(?,?,?,?,?,?)");

            Date dataHoraAtual = Date.from(Instant.now());
            
            java.time.Instant instant = dataHoraAtual.toInstant();
            java.sql.Timestamp sqlTimestamp = new java.sql.Timestamp(instant.toEpochMilli());

            stmt.setTimestamp(1, sqlTimestamp);
            stmt.setString(2, objeto.getAutorizedBy());
            stmt.setString(3, objeto.getOperacao());
            stmt.setInt(4, objeto.getCodProd());
            stmt.setFloat(5, objeto.getQtdAnterior());
            stmt.setFloat(6, objeto.getQtdAtual());
            
            stmt.executeUpdate();
            
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "erro ao salvar: " + ex);
            System.out.println("erro ao salvar:" + ex.getMessage());
        } finally {
            Conecta.closeConnection(con, stmt);
        }

    }

    @Override
    public void update(EntradaDeStock objeto) {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public void Delete(EntradaDeStock objeto) {
        Connection con = Conecta.getConnection();
        PreparedStatement stmt = null;
        try {
            stmt = con.prepareStatement("delete from tab_entrada_stock where id = " + objeto.getId() + "");
            stmt.executeUpdate();
            //JOptionPane.showMessageDialog(null, "Produto removido da promoção!");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Erro ao excluir: " + ex);
        } finally {
            Conecta.closeConnection(con, stmt);
        }
    }

    @Override
    public List<EntradaDeStock> read() {
         Connection con = Conecta.getConnection();
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<EntradaDeStock> obj = new ArrayList<>();
        try {
            stmt = con.prepareStatement("SELECT * FROM tab_entrada_stock ORDER BY id DESC");
            rs = stmt.executeQuery();
            while (rs.next()) {
                EntradaDeStock ao = new EntradaDeStock();
                ao.setId(rs.getInt("id"));
                ao.setAutorizedBy(rs.getString("autorizedby"));
                ao.setOperacao(rs.getString("operacao"));
                ao.setDataHora(rs.getTimestamp("datahora"));
                ao.setCodProd(rs.getInt("produto"));
                ao.setQtdAnterior(rs.getFloat("qtdAnterior"));
                 ao.setQtdAtual(rs.getFloat("qtdAtual"));
                obj.add(ao);

            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "ERRO: " + ex.getMessage());
            System.out.println(ex.getMessage());
        } finally {
            Conecta.closeConnection(con, stmt, rs);
        }

        
        return obj;
    }

    @Override
    public EntradaDeStock retornar(String codigo) {
        throw new UnsupportedOperationException("Not supported yet."); 
    }
    
}
