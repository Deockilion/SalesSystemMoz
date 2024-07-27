package mz.co.fer.DAO;

import mz.co.fer.DTO.Contabilidade;
import mz.co.fer.Conexao.Conecta;
import mz.co.fer.DTO.Caixa;
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
 * Classe com o CRUD das vendas
 *
 * @author Fernando
 */
public class ContabilidadeDAO {

    /**
     * Salva os dados do cartão da venda
     *
     * @param v recebe dados do cartão
     */
    public void salvarDadosCard(Contabilidade v) {
        Connection con = Conecta.getConnection();

        PreparedStatement stmt = null;
        try {
            stmt = con.prepareStatement("INSERT INTO  tab_dadosclientecard (data,idRecibo,nrpos,expdate,card,valor)VALUES(?,?,?,?,?,?)");
            Date dataHoraAtual = Date.from(Instant.now());
            java.time.Instant instant = dataHoraAtual.toInstant();
            java.sql.Timestamp sqlTimestamp = new java.sql.Timestamp(instant.toEpochMilli());
            
            stmt.setTimestamp(1, sqlTimestamp);
            stmt.setString(2, v.getIdRecibo());
            stmt.setInt(3, v.getNrpos());
            stmt.setInt(4, v.getExpdate());
            stmt.setInt(5, v.getCard());
            stmt.setDouble(6, v.getValor());

            stmt.executeUpdate();

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "erro ao salvar: " + ex.getMessage());
        } finally {
            Conecta.closeConnection(con, stmt);
        }
    }

    /**
     * Faz a leitura dos dados do cartão do cliente
     *
     * @return
     */
    public List<Contabilidade> read() {
        Connection con = Conecta.getConnection();

        PreparedStatement stmt = null;
        ResultSet rs = null;

        List<Contabilidade> vendas = new ArrayList<>();

        try {
            stmt = con.prepareStatement("SELECT * FROM tab_dadosclientecard");
            rs = stmt.executeQuery();

            while (rs.next()) {
                Contabilidade vendido = new Contabilidade();

                vendido.setId(rs.getInt("id_venda"));

                vendido.setData(rs.getTimestamp("data"));
                vendido.setIdRecibo(rs.getString("idRecibo"));
                vendido.setNrpos(rs.getInt("nrpos"));
                vendido.setExpdate(rs.getInt("expdate"));
                vendido.setCard(rs.getInt("card"));
                vendido.setValor(rs.getDouble("valor"));
                vendas.add(vendido);
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro: " + e.getMessage());
        } finally {
            Conecta.closeConnection(con, stmt, rs);
        }
        return vendas;
    }

    /**
     * Apagar do banco
     *
     * @param idrecibo pelo idrecibo
     */
    public void deletar(Contabilidade idrecibo) {
        Connection con = Conecta.getConnection();
        PreparedStatement stmt = null;

        try {
            stmt = con.prepareStatement("DELETE FROM tab_dadosclientecard WHERE id_venda = ?");
            stmt.setInt(1, idrecibo.getId());

            stmt.executeUpdate();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro ao tentar Eliminar: " + e.getMessage());
        } finally {
            Conecta.closeConnection(con, stmt);
        }
    }

    /**
     * Pesquisar uma venda cartão pelo ID do recibo
     *
     * @param id pelo ID
     * @return
     */
    public List<Contabilidade> readForId(int id) {
        Connection con = Conecta.getConnection();

        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Contabilidade> dadoscard = new ArrayList<>();

        try {
            stmt = con.prepareStatement("SELECT * FROM tab_dadosclientecard WHERE idrecibo like ?");

            stmt.setString(1, id + "%");

            rs = stmt.executeQuery();

            while (rs.next()) {

                Contabilidade dados = new Contabilidade();

                dados.setId(rs.getInt("id_venda"));
                dados.setData(rs.getTimestamp("data"));
                dados.setIdRecibo(rs.getString("idrecibo"));
                dados.setNrpos(rs.getInt("nrpos"));
                dados.setExpdate(rs.getInt("expdate"));
                dados.setCard(rs.getInt("card"));
                dados.setValor(rs.getDouble("valor"));
                dadoscard.add(dados);
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "erro " + ex.getMessage());
        } finally {
            Conecta.closeConnection(con, stmt, rs);
        }
        return dadoscard;

    }

    public void salvarValorDaVenda(Caixa user) {

        try (Connection con = Conecta.getConnection(); PreparedStatement stmt = con.prepareStatement("INSERT INTO tab_caixa (nome,data) VALUES (?,?)", Statement.RETURN_GENERATED_KEYS); PreparedStatement stmtValor = con.prepareStatement("INSERT INTO tab_extrato_caixa (id_venda,hora,cash,card) VALUES (?,?,?,?)")) {
            con.setAutoCommit(false);
            Date data = Date.from(Instant.now());
            java.sql.Time sqlTime = new java.sql.Time(data.getTime());

            java.time.Instant instant = data.toInstant();
            java.sql.Date sqlDate = new java.sql.Date(instant.toEpochMilli());

            stmt.setString(1, user.getNome());
            stmt.setDate(2, sqlDate);
            stmt.executeUpdate();

            ResultSet generatedKeys = stmt.getGeneratedKeys();
            int idVenda = -1;

            if (generatedKeys.next()) {
                idVenda = generatedKeys.getInt(1);

            } else {
                throw new SQLException("Falha ao obter o ID da venda, nenhum ID gerado.");
            }

            stmtValor.setInt(1, idVenda);
            stmtValor.setTime(2, sqlTime);
            stmtValor.setDouble(3, user.getCash());
            stmtValor.setDouble(4, user.getCard());
            stmtValor.executeUpdate();

            con.commit();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
             System.out.println(e.getMessage());
        }
    }

    public Caixa lerValoresVenda(String nome) {
        Connection con = Conecta.getConnection();
        PreparedStatement stmt = null;
        ResultSet rs = null;
        Caixa caixa = null;

        try {
            stmt = con.prepareStatement("SELECT * FROM tab_caixa WHERE nome = ? AND DATE(data) = CURRENT_DATE");
            stmt.setString(1, nome);
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                caixa = new Caixa();
                caixa.setId(rs.getInt("id"));
                caixa.setNome(rs.getString("nome"));
                caixa.setData(rs.getDate("data"));

                List<Caixa> valores = lerDadosPagamentos(caixa.getId());
                caixa.setValores(valores);

            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            JOptionPane.showMessageDialog(null, e.getMessage());
             
        } finally {
            Conecta.closeConnection(con, stmt, rs);
        }
        return caixa;
    }

    private List<Caixa> lerDadosPagamentos(int idValor) {
        Connection con = Conecta.getConnection();
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Caixa> valores = new ArrayList<>();
        try {
            stmt = con.prepareStatement("SELECT * FROM tab_extrato_caixa WHERE id_venda = ?");
            stmt.setInt(1, idValor);
            rs = stmt.executeQuery();
            while (rs.next()) {
                Caixa caixa = new Caixa();
                caixa.setIdValor(rs.getInt("id_venda"));
                caixa.setHora(rs.getTime("hora"));
                caixa.setCash(rs.getDouble("cash"));
                caixa.setCard(rs.getDouble("card"));
                valores.add(caixa);
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
             System.out.println(e.getMessage());
        } finally {
            Conecta.closeConnection(con, stmt, rs);
        }
        return valores;
    }

    public void saveCount(Caixa c) {
        Connection con = Conecta.getConnection();
        PreparedStatement stmt = null;
        Date data = Date.from(Instant.now());
        java.sql.Time sqlTime = new java.sql.Time(data.getTime());

        Caixa caixa = lerValoresVenda(c.getNome());

        try {
            stmt = con.prepareStatement("INSERT INTO tab_extrato_caixa (id_venda,hora,cash,card) VALUES (?,?,?,?)");
            stmt.setInt(1, caixa.getId());
            stmt.setTime(2, sqlTime);
            stmt.setDouble(3, c.getCash());
            stmt.setDouble(4, c.getCard());
            stmt.executeUpdate();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
            System.out.println(e.getMessage());
        } finally {
            Conecta.closeConnection(con, stmt);
        }
    }
     public boolean verificarCaixa(String nomeuser) {
        Connection con = Conecta.getConnection();
        PreparedStatement stmt = null;
        try {
            stmt = con.prepareStatement("SELECT COUNT(*) FROM tab_caixa WHERE nome = ? AND DATE(data) = CURRENT_DATE");
            stmt.setString(1, nomeuser);
            try (ResultSet resultSet = stmt.executeQuery()) {
                if (resultSet.next()) {
                    int quantidadeRegistros = resultSet.getInt(1);
                    return quantidadeRegistros > 0;
                }
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro FechoDAO" + e.getMessage());
        } finally {
            Conecta.closeConnection(con, stmt);
        }
        return false;

    }
    public Caixa lerValoresVendaAntigo(String nome, String data) {
        Connection con = Conecta.getConnection();
        PreparedStatement stmt = null;
        ResultSet rs = null;
        Caixa caixa = null;
        
        //LocalDate date = LocalDate.parse(data);
        try {
            stmt = con.prepareStatement("SELECT * FROM tab_caixa WHERE nome = ? AND DATE(data) = ?");
            stmt.setString(1, nome);
            stmt.setDate(2, java.sql.Date.valueOf(data));
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                caixa = new Caixa();
                caixa.setId(rs.getInt("id"));
                caixa.setNome(rs.getString("nome"));
                caixa.setData(rs.getDate("data"));

                List<Caixa> valores = lerDadosPagamentos(caixa.getId());
                caixa.setValores(valores);

            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            JOptionPane.showMessageDialog(null, e.getMessage());
             
        } finally {
            Conecta.closeConnection(con, stmt, rs);
        }
        return caixa;
    }
    public List<Caixa> lerCaixa() {
        Connection con = Conecta.getConnection();
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Caixa> datas = new ArrayList<>();
        
        
        try {
            stmt = con.prepareStatement("SELECT * FROM public.tab_caixa ORDER BY id DESC LIMIT 10");
           
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                Caixa caixa = new Caixa();
                caixa.setId(rs.getInt("id"));
                caixa.setNome(rs.getString("nome"));
                caixa.setData(rs.getDate("data"));
                datas.add(caixa);

                List<Caixa> valores = lerDadosPagamentos(caixa.getId());
                caixa.setValores(valores);

            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            JOptionPane.showMessageDialog(null, e.getMessage());
             
        } finally {
            Conecta.closeConnection(con, stmt, rs);
        }
        return datas;
    }


}
