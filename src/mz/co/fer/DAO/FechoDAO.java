package mz.co.fer.DAO;

import mz.co.fer.DTO.Fecho;
import mz.co.fer.Conexao.Conecta;
import mz.co.fer.DTO.Notas;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.JOptionPane;

/**
 * Classe com CRUD do Fecho
 *
 * @author Fernando
 */
public class FechoDAO {

    PreparedStatement stmt = null;

    /**
     * Salva os dados do fecho
     *
     * @param fecho recebe fecho
     * @param nota  recebe nota
     */
    public void save(Fecho fecho, Notas nota) {
        //Connection con;
        Date dataHoraAtual = Date.from(Instant.now());
        java.time.Instant instant = dataHoraAtual.toInstant();
        java.sql.Timestamp sqlTimestamp = new java.sql.Timestamp(instant.toEpochMilli());

        try (Connection con = Conecta.getConnection(); PreparedStatement stmtt = con.prepareStatement("INSERT INTO tab_fecho (nome, data, valor_total, valor_falta) VALUES (?,?,?,?)", Statement.RETURN_GENERATED_KEYS); PreparedStatement stmtNota = con.prepareStatement("INSERT INTO tab_notas (id_fecho, card, n1000, n500, n200, n100, n50, n20, moedas) VALUES (?,?,?,?,?,?,?,?,?)")) {
            con.setAutoCommit(false);
            stmtt.setString(1, fecho.getNome());
            stmtt.setTimestamp(2, sqlTimestamp);
            stmtt.setDouble(3, fecho.getValorTotal());
            stmtt.setDouble(4, fecho.getValorFalta());
            stmtt.executeUpdate();

            ResultSet generatedKeys = stmtt.getGeneratedKeys();
            int idfecho = -1;

            if (generatedKeys.next()) {
                idfecho = generatedKeys.getInt(1);

            } else {
                throw new SQLException("Falha ao obter o ID da venda, nenhum ID gerado.");
            }
            
            stmtNota.setInt(1, idfecho);
            stmtNota.setDouble(2, nota.getCard());
            stmtNota.setInt(3, nota.getV1000());
            stmtNota.setInt(4, nota.getV500());
            stmtNota.setInt(5, nota.getV200());
            stmtNota.setInt(6, nota.getV100());
            stmtNota.setInt(7, nota.getV50());
            stmtNota.setInt(8, nota.getV20());
            stmtNota.setFloat(9, nota.getMoedas());
            stmtNota.executeUpdate();

            con.commit();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro FechoDAO" + e.getMessage());
            System.out.println(e.getMessage());
        } finally {
            Conecta.closeConnection(null, stmt);
        }

    }

    /**
     * Faz a leitura dos dados do Fecho
     *
     * @return fecho
     */
    public List<Fecho> read() {
        Connection con = Conecta.getConnection();
        ResultSet rs = null;
        List<Fecho> fecha = new ArrayList<>();
        try {
            stmt = con.prepareStatement("SELECT * FROM tab_fecho ORDER BY id DESC");
            rs = stmt.executeQuery();

            while (rs.next()) {

                Fecho fechododia = new Fecho();

                fechododia.setId(rs.getInt("id"));
                fechododia.setData(rs.getTimestamp("data"));
                fechododia.setNome(rs.getString("nome"));

                fechododia.setValorTotal(rs.getDouble("valor_total"));
                fechododia.setValorFalta(rs.getDouble("valor_falta"));
                fecha.add(fechododia);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Erro FechoDAO" + ex.getMessage());
            System.out.println(ex.getMessage());
        } finally {
            Conecta.closeConnection(con, stmt, rs);
        }
        return fecha;
    }

    public Notas retornarDados(int id) {
        Connection con = Conecta.getConnection();
        ResultSet rs = null;
        Notas nota = null;
        try {
            String sql = "SELECT * FROM tab_fecho pf JOIN tab_notas tn ON pf.id = tn.id_fecho where DATE(data) = CURRENT_DATE AND pf.id = ?";
            stmt = con.prepareStatement(sql);
            stmt.setInt(1, id);
            rs = stmt.executeQuery();
            if(rs.next()){
                nota = new Notas();
                nota.setId(rs.getInt("id_fecho"));
                nota.setCard(rs.getDouble("card"));
                nota.setV1000(rs.getInt("n1000"));
                nota.setV500(rs.getInt("n500"));
                nota.setV200(rs.getInt("n200"));
                nota.setV100(rs.getInt("n100"));
                nota.setV50(rs.getInt("n50"));
                nota.setV20(rs.getInt("n20"));
                nota.setMoedas(rs.getFloat("moedas"));
            }                 
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro FechoDAO" + e.getMessage());
            System.out.println(e.getMessage());
        } finally {
            Conecta.closeConnection(con, stmt, rs);
        }
        return nota;
    }
    public void updateNotas(Notas n) {
        Connection con = Conecta.getConnection();
        try {
            stmt = con.prepareStatement("UPDATE tab_notas SET card = ?, n1000 = ?, n500 = ?, n200 = ?,"
                    + "n100 = ?, n50 = ?, n20 = ?, moedas = ? where id_fecho = ?");
            stmt.setDouble(1, n.getCard());           
            stmt.setInt(2, n.getV1000());
            stmt.setInt(3, n.getV500());
            stmt.setInt(4, n.getV200());
            stmt.setInt(5, n.getV100());
            stmt.setInt(6, n.getV50());
            stmt.setInt(7, n.getV20());
            stmt.setFloat(8, n.getMoedas());
            stmt.setInt(9, n.getId());
            stmt.executeUpdate();

            JOptionPane.showMessageDialog(null, "Atualizado com sucesso!");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro fechoDAO: " + e.getMessage());
        } finally {
            Conecta.closeConnection(con, stmt);
        }

    }

    public void updateFecho(Fecho f) {
        Connection con = Conecta.getConnection();
        try {
            stmt = con.prepareStatement("UPDATE tab_fecho SET valor_falta = ? where id = ?");
            stmt.setDouble(1, f.getValorFalta());
            stmt.setInt(2, f.getId());
            stmt.executeUpdate();

            JOptionPane.showMessageDialog(null, "Atualizado com sucesso!");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro fechoDAO: " + e.getMessage());
            System.out.println(e.getMessage());
        } finally {
            Conecta.closeConnection(con, stmt);
        }

    }

    /**
     * Este metodo verifica se o utilizador ja efetuou o fecho do dia de hoje
     *
     * @param nomeuser recebe o nome do utilizador
     * @return
     */
    public boolean verificarFecho(String nomeuser) {
        Connection con = Conecta.getConnection();
        try {
            stmt = con.prepareStatement("SELECT COUNT(*) FROM tab_fecho WHERE nome = ? AND DATE(data) = CURRENT_DATE");
            stmt.setString(1, nomeuser);
            try (ResultSet resultSet = stmt.executeQuery()) {
                if (resultSet.next()) {
                    int quantidadeRegistros = resultSet.getInt(1);
                    return quantidadeRegistros > 0;
                }
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro FechoDAO" + e.getMessage());
            System.out.println(e.getMessage());
        } finally {
            Conecta.closeConnection(con, stmt);
        }
        return false;

    }

    public Fecho retornarFecho(String nome) {
        Connection con = Conecta.getConnection();
        ResultSet rs = null;
        Fecho fecho = new Fecho();
        try {
            stmt = con.prepareStatement("SELECT id, nome, data, valor_total, valor_falta FROM tab_fecho where nome = ?");
            stmt.setString(1, nome);
            rs = stmt.executeQuery();

            if (rs.next()) {
                fecho.setId(rs.getInt("id"));
                fecho.setNome(rs.getString("nome"));
                fecho.setData(rs.getDate("data"));
                fecho.setValorTotal(rs.getDouble("valor_total"));
                fecho.setValorFalta(rs.getDouble("valor_falta"));
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro: " + e.getMessage());
            System.out.println(e.getMessage());
        } finally {
            Conecta.closeConnection(con, stmt);
        }
        return fecho;
    }
}
