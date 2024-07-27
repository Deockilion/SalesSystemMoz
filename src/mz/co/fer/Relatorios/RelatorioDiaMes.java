package mz.co.fer.Relatorios;

import mz.co.fer.Conexao.Conecta;
import java.time.LocalDate;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Connection;
import javax.swing.JOptionPane;

/**
 *
 * @author Deockilion
 */
public class RelatorioDiaMes {

    /**
     * Este método retorna a quantidade vendida do dia atual
     *
     * @return quantidade
     */
    public static double obterQuantidadeDia() {
        double quantidade = 0;
        String sqlPostgre = "SELECT SUM(CASE WHEN pv.quantidade > 0 THEN 1 ELSE 0 END) \n"
                + "FROM tab_produtos_vendidos pv \n"
                + "JOIN tab_vendas v ON pv.id_venda = v.id_venda \n"
                + "WHERE DATE(v.data_venda) = ?";
//        String sqlMySql = "SELECT SUM(pv.quantidade > 0) FROM tab_produtos_vendidos pv "
//                + "JOIN tab_vendas v ON pv.id_venda = v.id_venda "
//                + "WHERE DATE(v.data_venda) = ?";
        try (Connection conexao = Conecta.getConnection(); PreparedStatement stmt = conexao.prepareStatement(
                sqlPostgre)) {

            LocalDate dataAtual = LocalDate.now();
            java.sql.Date dataSql = java.sql.Date.valueOf(dataAtual);

            stmt.setDate(1, dataSql);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    quantidade = rs.getDouble(1);
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "ERROR", JOptionPane.ERROR_MESSAGE);
        }

        return quantidade;
    }

    /**
     * Este metodo retorna o valor total vendido do dia atual
     *
     * @return totalVendido
     */
    public static double obterValorTotal() {
        double totalVendido = 0;

        try (Connection conexao = Conecta.getConnection(); PreparedStatement stmt = conexao.prepareStatement(
                "SELECT SUM(valor_total) FROM tab_vendas "
                + "WHERE DATE(data_venda) = ?")) {

            LocalDate dataAtual = LocalDate.now();
            java.sql.Date dataSql = java.sql.Date.valueOf(dataAtual);

            stmt.setDate(1, dataSql);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    totalVendido = rs.getDouble(1);
                }
            }
        } catch (SQLException e) {
             JOptionPane.showMessageDialog(null, e.getMessage(), "ERROR", JOptionPane.ERROR_MESSAGE);
        }

        return totalVendido;
    }

    /**
     * Retorna a quantidade total vendida do mês atual e ano atual
     *
     * @return quantidade
     */
    public static double obterQuantidadeMes() {
        double quantidade = 0;

//        String sql = "SELECT SUM(pv.quantidade >= 0) FROM tab_produtos_vendidos pv "
//                + "JOIN tab_vendas v ON pv.id_venda = v.id_venda "
//                + "WHERE MONTH(v.data_venda) = ? AND YEAR(v.data_venda) = ?";

        try (Connection conexao = Conecta.getConnection(); PreparedStatement stmt = conexao.prepareStatement(
                "SELECT SUM(CASE WHEN pv.quantidade >= 0 THEN 1 ELSE 0 END) \n"
                + "FROM tab_produtos_vendidos pv \n"
                + "JOIN tab_vendas v ON pv.id_venda = v.id_venda \n"
                + "WHERE EXTRACT(MONTH FROM v.data_venda) = ? AND EXTRACT(YEAR FROM v.data_venda) = ?")) {

            LocalDate dataAtual = LocalDate.now();
            int mesAtual = dataAtual.getMonthValue();
            int anoAtual = dataAtual.getYear();

            stmt.setInt(1, mesAtual);
            stmt.setInt(2, anoAtual);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    quantidade = rs.getDouble(1);
                }
            }
        } catch (SQLException e) {
             JOptionPane.showMessageDialog(null, e.getMessage(), "ERROR", JOptionPane.ERROR_MESSAGE);
        }

        return quantidade;
    }

    /**
     * Retorna o valor total vendido do mês e ano atual
     *
     * @return total
     */
    public static double obterValorVendidoMes() {
        double total = 0;
//        String sql = "SELECT SUM(pv.subtotal) FROM tab_produtos_vendidos pv "
//                + "JOIN tab_vendas v ON pv.id_venda = v.id_venda "
//                + "WHERE MONTH(v.data_venda) = ? AND YEAR(v.data_venda) = ?";
        try (Connection conexao = Conecta.getConnection(); PreparedStatement stmt = conexao.prepareStatement(
                "SELECT SUM(valor_total) FROM tab_vendas WHERE EXTRACT(MONTH FROM data_venda) = ? "
                        + "AND EXTRACT(YEAR FROM data_venda) = ?")) {

            LocalDate dataAtual = LocalDate.now();
            int mesAtual = dataAtual.getMonthValue();
            int anoAtual = dataAtual.getYear();

            stmt.setInt(1, mesAtual);
            stmt.setInt(2, anoAtual);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    total = rs.getDouble(1);
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
        }

        return total;
    }

    /**
     * Retorna a quantidade vendida do mês e ano informado pelo utilizador
     *
     * @param mes
     * @param ano
     * @return quantidade
     */
    public static double retornarQuantidadeMes(int mes, int ano) {
        Connection con = Conecta.getConnection();
        PreparedStatement stmt = null;
        ResultSet rs = null;
        double quantidade = 0;

        try {
            String sql = "SELECT SUM(CASE WHEN pv.quantidade >= 0 THEN 1 ELSE 0 END) FROM tab_produtos_vendidos pv "
                    + "JOIN tab_vendas v ON pv.id_venda = v.id_venda "
                    + "WHERE EXTRACT(MONTH FROM data_venda) = ? AND EXTRACT(YEAR FROM data_venda) = ?";

            stmt = con.prepareStatement(sql);
            stmt.setInt(1, mes);
            stmt.setInt(2, ano);
            rs = stmt.executeQuery();

            if (rs.next()) {
                quantidade = rs.getDouble(1);
            } else {
                JOptionPane.showMessageDialog(null, "Nenhuma venda encontrada!");
            }

        } catch (SQLException e) {
             JOptionPane.showMessageDialog(null, e.getMessage(), "ERROR", JOptionPane.ERROR_MESSAGE);
        } finally {
            Conecta.closeConnection(con, stmt, rs);
        }

        return quantidade;
    }

    /**
     * retorna o valor total vendida no mês e ano informado pelo utilizador
     *
     * @param mes
     * @param ano
     * @return totalVendido
     */
    public static double retornarValorMes(int mes, int ano) {
        Connection con = Conecta.getConnection();
        PreparedStatement stmt = null;
        ResultSet rs = null;
        double totalVendido = 0;

        try {
            String sql = "SELECT SUM(valor_total) FROM tab_vendas "
                    + "WHERE EXTRACT(MONTH FROM data_venda) = ? AND EXTRACT(YEAR FROM data_venda) = ?";

            stmt = con.prepareStatement(sql);
            stmt.setInt(1, mes);
            stmt.setInt(2, ano);
            rs = stmt.executeQuery();

            if (rs.next()) {
                totalVendido = rs.getDouble(1);
            } else {
                JOptionPane.showMessageDialog(null, "Nenhuma venda encontrada!");
            }

        } catch (SQLException e) {
             JOptionPane.showMessageDialog(null, e.getMessage(), "ERROR", JOptionPane.ERROR_MESSAGE);
        } finally {
            Conecta.closeConnection(con, stmt, rs);
        }

        return totalVendido;
    }

}
