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
import mz.co.fer.DTO.Logs;

/**
 *
 * @author Deockilion
 */
public class LogsDAO implements IDAO<Logs> {
    
    
    /**
     * Este método salva o nome completo e data & hora que o utilizador iniciou sessão ao sistema 
     * @param objeto
     */
    @Override
    public void save(Logs objeto) {
        java.sql.Connection con = Conecta.getConnection();
        PreparedStatement stmt = null;
        try {

            Date dataHoraAtual = Date.from(Instant.now());
            java.time.Instant instant = dataHoraAtual.toInstant();
            java.sql.Timestamp sqlTimestamp = new java.sql.Timestamp(instant.toEpochMilli());

            stmt = con.prepareStatement("INSERT INTO tab_log_de_login (nome_completo, data_hora_login) VALUES (?,?)");

            stmt.setString(1, objeto.getNome());
            stmt.setTimestamp(2, sqlTimestamp);
            stmt.executeUpdate();

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Erro ao salvar: " + ex);
        } finally {
            Conecta.closeConnection(con, stmt);
        }
    }

    @Override
    public void update(Logs objeto) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void Delete(Logs objeto) {
        Connection con = Conecta.getConnection();
        PreparedStatement stmt = null;
        try {
            stmt = con.prepareStatement("delete from tab_log_de_login where id = " + objeto.getId() + "");
            stmt.executeUpdate();
            //JOptionPane.showMessageDialog(null, "Produto removido da promoção!");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Erro ao excluir: " + ex);
        } finally {
            Conecta.closeConnection(con, stmt);
        }
    }

    @Override
    public List<Logs> read() {
        java.sql.Connection con = Conecta.getConnection();
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Logs> logs = new ArrayList<>();
        try {
            stmt = con.prepareStatement("SELECT * FROM tab_log_de_login ORDER BY id DESC");
            rs = stmt.executeQuery();
            while (rs.next()) {
                Logs log = new Logs();
                log.setId(rs.getInt("id"));
                log.setNome(rs.getString("nome_completo"));
                log.setDataHora(rs.getTimestamp("data_hora_login"));
                logs.add(log);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Erro: " + ex);
        } finally {
            Conecta.closeConnection(con, stmt);
        }
        return logs;
    }

    @Override
    public Logs retornar(String codigo) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}
