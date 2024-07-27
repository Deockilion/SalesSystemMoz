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
import mz.co.fer.DTO.Authorized;

/**
 *
 * @author Deockilion
 */
public class AuthorizedDAO implements IDAO<Authorized> {

    @Override
    public void save(Authorized objeto) {
        Connection con = Conecta.getConnection();
        PreparedStatement stmt = null;
        try {

            stmt = con.prepareStatement("INSERT INTO tab_autorizacao (autorizedBy, operacao, datahora)VALUES(?,?,?)");

            Date dataHoraAtual = Date.from(Instant.now());
            
            java.time.Instant instant = dataHoraAtual.toInstant();
            java.sql.Timestamp sqlTimestamp = new java.sql.Timestamp(instant.toEpochMilli());

            stmt.setString(1, objeto.getAutorizedBy());
            stmt.setString(2, objeto.getOperacao());
            stmt.setTimestamp(3, sqlTimestamp);
            stmt.executeUpdate();
            
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "erro ao salvar: " + ex);
            System.out.println("erro ao salvar:" + ex.getMessage());
        } finally {
            Conecta.closeConnection(con, stmt);
        }

    }

    @Override
    public void update(Authorized objeto) {
        
    }

    @Override
    public void Delete(Authorized objeto) {
        Connection con = Conecta.getConnection();
        PreparedStatement stmt = null;
        try {
            stmt = con.prepareStatement("delete from tab_autorizacao where id = " + objeto.getId() + "");
            stmt.executeUpdate();
            //JOptionPane.showMessageDialog(null, "Produto removido da promoção!");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Erro ao excluir: " + ex);
        } finally {
            Conecta.closeConnection(con, stmt);
        }
    }

    @Override
    public List<Authorized> read() {
        Connection con = Conecta.getConnection();
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Authorized> obj = new ArrayList<>();
        try {
            stmt = con.prepareStatement("SELECT * FROM tab_autorizacao ORDER BY id DESC");
            rs = stmt.executeQuery();
            while (rs.next()) {
                Authorized ao = new Authorized();
                ao.setId(rs.getInt("id"));
                ao.setAutorizedBy(rs.getString("autorizedby"));
                ao.setOperacao(rs.getString("operacao"));
                ao.setDataHora(rs.getTimestamp("datahora"));
                obj.add(ao);

            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "ERRO: " + ex.getMessage());
        } finally {
            Conecta.closeConnection(con, stmt, rs);
        }

        
        return obj;

    }

    @Override
    public Authorized retornar(String codigo) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

}
