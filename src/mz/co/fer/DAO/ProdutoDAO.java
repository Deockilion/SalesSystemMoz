package mz.co.fer.DAO;

import mz.co.fer.Conexao.Conecta;
import mz.co.fer.DTO.Produto;
import mz.co.fer.DTO.ProdutoPromocao;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import mz.co.fer.enums.Operacao;

/**
 * Classe com o CRUD dos Produtos
 *
 * @author Fernando
 */
public class ProdutoDAO {

    public void create(Produto p) {
        Connection con = Conecta.getConnection();
        PreparedStatement stmt = null;
        try {
            stmt = con.prepareStatement("INSERT INTO tab_produtos (codigo,descricao,stock_loja,stock_armazem,preco,preco_venda,type)VALUES(?,?,?,?,?,?,?)");
            stmt.setInt(1, p.getCodigo());
            stmt.setString(2, p.getDescricao());
            stmt.setDouble(3, 0); // valor padrão para stock_loja
            stmt.setDouble(4, p.getQtdArmazem());
            stmt.setDouble(5, p.getPreco());
            stmt.setDouble(6, p.getPrecoVenda());
            stmt.setString(7, p.getType());

            stmt.executeUpdate();

            JOptionPane.showMessageDialog(null, "Produto cadastrado com sucesso!");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "erro ao salvar: " + ex);
            System.out.println("erro ao salvar:" + ex.getMessage());
        } finally {
            Conecta.closeConnection(con, stmt);
        }

    }

    public List<Produto> read() {
        Connection con = Conecta.getConnection();
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Produto> produtos = new ArrayList<>();

        try {
            stmt = con.prepareStatement("SELECT * from tab_produtos order by descricao");
            rs = stmt.executeQuery();

            while (rs.next()) {
                Produto produto = new Produto();

                produto.setId(rs.getInt("id"));
                produto.setCodigo(rs.getInt("codigo"));
                produto.setDescricao(rs.getString("descricao"));
                produto.setQtdLoja(rs.getDouble("stock_loja"));
                produto.setQtdArmazem(rs.getDouble("stock_armazem"));
                produto.setPreco(rs.getDouble("preco"));
                produto.setPrecoVenda(rs.getDouble("preco_venda"));
                produto.setImagem(rs.getBytes("imagem"));
                produto.setType(rs.getString("type"));
                produtos.add(produto);
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "erro ao salvar: " + ex);
        } finally {
            Conecta.closeConnection(con, stmt, rs);
        }

        return produtos;
    }

    public List<Produto> readForCod(String cod) {
        Connection con = Conecta.getConnection();
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Produto> produtos = new ArrayList<>();

        try {
            stmt = con.prepareStatement("SELECT * FROM tab_produtos WHERE CAST(codigo AS TEXT) ilike ? OR descricao ilike ?");
            stmt.setString(1, cod + "%");
            stmt.setString(2, cod + "%");

            rs = stmt.executeQuery();
            while (rs.next()) {
                Produto produto = new Produto();

                produto.setCodigo(rs.getInt("codigo"));
                produto.setDescricao(rs.getString("descricao"));
                produto.setQtdLoja(rs.getDouble("stock_loja"));
                produto.setQtdArmazem(rs.getDouble("stock_armazem"));
                produto.setPreco(rs.getDouble("preco"));
                produto.setPrecoVenda(rs.getDouble("preco_venda"));
                produto.setImagem(rs.getBytes("imagem"));
                produto.setType(rs.getString("type"));
                produtos.add(produto);
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "ERRO: " + ex.getMessage());
            System.out.println(ex.getMessage());
        } finally {
            Conecta.closeConnection(con, stmt, rs);
        }

        return produtos;
    }

    public void update(Produto p, Operacao operacao) {
        Connection con = Conecta.getConnection();
        PreparedStatement stmt = null;

        try {
            if (null == operacao) {
                JOptionPane.showMessageDialog(null, "Ação inválida!");
            } else {
                switch (operacao) {
                    case CODIGO -> {
                        stmt = con.prepareStatement("UPDATE tab_produtos SET codigo = ? WHERE id = ?");
                        stmt.setInt(1, p.getCodigo());
                        stmt.setInt(2, p.getId());
                        stmt.executeUpdate();
                        //JOptionPane.showMessageDialog(null, "Código do produto atualizado com sucesso!");
                    }
                    case DESCRICAOP -> {
                        stmt = con.prepareStatement("UPDATE tab_produtos SET descricao = ? WHERE codigo = ?");
                        stmt.setString(1, p.getDescricao());
                        stmt.setInt(2, p.getCodigo());
                        stmt.executeUpdate();
                        JOptionPane.showMessageDialog(null, "Descrição do Produto atualizado com sucesso!");
                    }
                    case QUANTIDADE_LOJA -> {
                        stmt = con.prepareStatement("UPDATE tab_produtos SET stock_loja = ? WHERE codigo = ?");
                        stmt.setDouble(1, p.getQtdLoja());
                        stmt.setInt(2, p.getCodigo());
                        stmt.executeUpdate();
                        //JOptionPane.showMessageDialog(null, "Transferência concluída com sucesso!");
                    }
                    case QTD_ARMAZEM -> {
                        stmt = con.prepareStatement("UPDATE tab_produtos SET stock_armazem = ? WHERE codigo = ?");
                        stmt.setDouble(1, p.getQtdArmazem());
                        stmt.setInt(2, p.getCodigo());
                        stmt.executeUpdate();
                        //JOptionPane.showMessageDialog(null, "Quantidade do Produto actualizado com sucesso!");
                    }
                    case PRECO -> {
                        stmt = con.prepareStatement("UPDATE tab_produtos SET preco = ? WHERE codigo = ?");
                        stmt.setDouble(1, p.getPreco());
                        stmt.setInt(2, p.getCodigo());
                        stmt.executeUpdate();
                        JOptionPane.showMessageDialog(null, "Preço do Produto actualizado com sucesso!");
                    }
                    case PRECO_VENDA -> {
                        stmt = con.prepareStatement("UPDATE tab_produtos SET preco_venda = ? WHERE codigo = ?");
                        stmt.setDouble(1, p.getPrecoVenda());
                        stmt.setInt(2, p.getCodigo());
                        stmt.executeUpdate();

                    }
                    case IMAGEM -> {
                        stmt = con.prepareStatement("UPDATE tab_produtos SET imagem = ? WHERE codigo = ?");
                        stmt.setBytes(1, p.getImagem());
                        stmt.setInt(2, p.getCodigo());
                        stmt.executeUpdate();
                        JOptionPane.showMessageDialog(null, "Imagem do Produto actualizada com sucesso!");
                    }
                    case TYPE -> {
                        stmt = con.prepareStatement("UPDATE tab_produtos SET type = ? WHERE codigo = ?");
                        stmt.setString(1, p.getType());
                        stmt.setInt(2, p.getCodigo());
                        stmt.executeUpdate();
                        JOptionPane.showMessageDialog(null, "Alterou a categoria do Produto com sucesso!");
                    }
                    default ->
                        JOptionPane.showMessageDialog(null, "Ação inválida!");
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Erro ao atualizar: " + ex);
            System.out.println(ex.getMessage());
        } finally {
            Conecta.closeConnection(con, stmt);
        }

    }

    public void delete(Produto p) {
        Connection con = Conecta.getConnection();
        PreparedStatement stmt = null;
        try {
            stmt = con.prepareStatement("delete from tab_produtos where codigo = " + p.getId() + "");
            stmt.executeUpdate();
            JOptionPane.showMessageDialog(null, "Produto excluído com sucesso!");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Erro ao excluir: " + ex);
        } finally {
            Conecta.closeConnection(con, stmt);
        }

    }

    /**
     * Retorna o produto pelo código
     *
     * @param codigo
     * @return
     */
    public Produto retornarProduto(int codigo) {
        Connection con = Conecta.getConnection();
        Produto produto = new Produto();
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            stmt = con.prepareStatement("SELECT * FROM tab_produtos WHERE codigo = " + codigo + "");
            rs = stmt.executeQuery();

            if (rs.next()) {
                produto.setId(rs.getInt("id"));
                produto.setCodigo(rs.getInt("codigo"));
                produto.setDescricao(rs.getString("descricao"));
                produto.setQtdLoja(rs.getDouble("stock_loja"));
                produto.setQtdArmazem(rs.getDouble("stock_armazem"));
                produto.setPreco(rs.getDouble("preco"));
                produto.setPrecoVenda(rs.getDouble("preco_venda"));
                produto.setImagem(rs.getBytes("imagem"));
                produto.setType(rs.getString("type"));

            } else {
                JOptionPane.showMessageDialog(null, "Produto não encontrado!", "Atenção", JOptionPane.WARNING_MESSAGE);
                return null;
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Não foi possivel adicionar o produto:" + ex);
        } finally {
            Conecta.closeConnection(con, stmt);
        }
        return produto;
    }

    public void reduzirQuantidadeProdutos(List<Produto> produtos) {
        Connection con = Conecta.getConnection();
        PreparedStatement stmt = null;
        try {
            for (Produto produto : produtos) {
                stmt = con.prepareStatement("UPDATE tab_produtos SET stock_loja = stock_loja - ? WHERE codigo = ?");
                stmt.setDouble(1, produto.getQtdArmazem());
                stmt.setInt(2, produto.getCodigo());
                stmt.executeUpdate();
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e);
        } finally {
            Conecta.closeConnection(con, stmt);
        }
    }

    public Produto procurarProdutoPeloCodigo(String codigo) {
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        Produto produto = null;

        try {
            con = Conecta.getConnection();
            stmt = con.prepareStatement("SELECT * FROM tab_produtos WHERE CAST(codigo AS TEXT) ilike ? OR descricao ilike ?");

            stmt.setString(1, codigo + "%");
            stmt.setString(2, codigo + "%");

            rs = stmt.executeQuery();

            if (rs.next()) {
                produto = new Produto();
                produto.setId(rs.getInt("id"));
                produto.setCodigo(rs.getInt("codigo"));
                produto.setDescricao(rs.getString("descricao"));
                produto.setQtdLoja(rs.getDouble("stock_loja"));
                produto.setQtdArmazem(rs.getDouble("stock_armazem"));
                produto.setPreco(rs.getDouble("preco"));
                produto.setPrecoVenda(rs.getDouble("preco_venda"));
                produto.setType(rs.getString("type"));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "ProdutoDAO:" + e);
            System.out.println("erroDAo: " + e.getMessage());
        } finally {
            Conecta.closeConnection(con, stmt, rs);
        }

        return produto;
    }

    public void salvarPromocao(List<ProdutoPromocao> listaPromo) {
        Connection con = Conecta.getConnection();
        PreparedStatement stmt = null;
        try {

            stmt = con.prepareStatement("INSERT INTO tab_produtos_promocao (codigo_do_produto,preco_normal,preco_promocional,data_fim_promocao,data_inicio_promocao)VALUES(?,?,?,?,?)");
            for (ProdutoPromocao promo : listaPromo) {

                LocalDate dataInicio = promo.getDataInicioPromocao();
                LocalDate dataFim = promo.getDataFimPromocao();

                stmt.setInt(1, promo.getCodigoProd());
                stmt.setDouble(2, promo.getPrecoNormal());
                stmt.setDouble(3, promo.getPrecoPromocional());
                stmt.setDate(4, java.sql.Date.valueOf(dataFim));
                stmt.setDate(5, java.sql.Date.valueOf(dataInicio));

                stmt.executeUpdate();
            }

            JOptionPane.showMessageDialog(null, "Operação concluida com sucesso!");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "erro ao salvar: " + ex);
            System.out.println("erro ao salvar:" + ex.getMessage());
        } finally {
            Conecta.closeConnection(con, stmt);
        }

    }

    public List<ProdutoPromocao> readPromoProd() {
        Connection con = Conecta.getConnection();
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<ProdutoPromocao> prodPromo = new ArrayList<>();

        try {
            stmt = con.prepareStatement("SELECT * FROM tab_produtos_promocao");
            //stmt.setInt(1, cod);

            rs = stmt.executeQuery();
            while (rs.next()) {
                ProdutoPromocao promo = new ProdutoPromocao();
                promo.setCodigoProd(rs.getInt("codigo_do_produto"));
                promo.setPrecoNormal(rs.getDouble("preco_normal"));
                promo.setPrecoPromocional(rs.getDouble("preco_promocional"));
                promo.setDataInicioPromocao(rs.getDate("data_inicio_promocao").toLocalDate());
                promo.setDataFimPromocao(rs.getDate("data_fim_promocao").toLocalDate());
                prodPromo.add(promo);

            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "ERRO: " + ex.getMessage());
            System.out.println(ex.getMessage());
        } finally {
            Conecta.closeConnection(con, stmt, rs);
        }

        return prodPromo;
    }

    public void deletePromocaoProd(ProdutoPromocao p) {
        Connection con = Conecta.getConnection();
        PreparedStatement stmt = null;
        try {
            stmt = con.prepareStatement("delete from tab_produtos_promocao where codigo_do_produto = " + p.getCodigoProd() + "");
            stmt.executeUpdate();
            //JOptionPane.showMessageDialog(null, "Produto removido da promoção!");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Erro ao excluir: " + ex);
        } finally {
            Conecta.closeConnection(con, stmt);
        }

    }

    public boolean verificarSeEstaEmPromocao(int codProd) {
        Connection con = Conecta.getConnection();
        PreparedStatement stmt = null;
        try {
            stmt = con.prepareStatement("SELECT COUNT(*) FROM tab_produtos_promocao WHERE codigo_do_produto = ?");
            stmt.setInt(1, codProd);
            try (ResultSet resultSet = stmt.executeQuery()) {
                if (resultSet.next()) {
                    int quantidadeRegistros = resultSet.getInt(1);
                    return quantidadeRegistros > 0;
                }
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro" + e.getMessage());
            System.out.println(e.getMessage());
        } finally {
            Conecta.closeConnection(con, stmt);
        }
        return false;

    }

    public ProdutoPromocao procurarProdutoPromocao(int codigo) {
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        ProdutoPromocao promo = null;

        try {
            con = Conecta.getConnection();
            stmt = con.prepareStatement("SELECT * FROM tab_produtos_promocao WHERE codigo_do_produto = ?");

            stmt.setInt(1, codigo);

            rs = stmt.executeQuery();

            if (rs.next()) {
                promo = new ProdutoPromocao();
                promo.setCodigoProd(rs.getInt("codigo_do_produto"));
                promo.setPrecoNormal(rs.getDouble("preco_normal"));
                promo.setPrecoPromocional(rs.getDouble("preco_promocional"));
                promo.setDataInicioPromocao(rs.getDate("data_inicio_promocao").toLocalDate());
                promo.setDataFimPromocao(rs.getDate("data_fim_promocao").toLocalDate());
            } else {
                return null;
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "ProdutoDAO:" + e);
            System.out.println("erroDAo: " + e.getMessage());
        } finally {
            Conecta.closeConnection(con, stmt, rs);
        }

        return promo;
    }

}
