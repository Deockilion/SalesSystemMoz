package mz.co.fer.DAO;

import mz.co.fer.Conexao.Conecta;
import mz.co.fer.DTO.Utilizador;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

/**
 * Classe com o CRUD dos Utilizadores
 *
 * @author Fernando
 */
public class UtilizadorDAO implements IDAO<Utilizador>{

    @Override
    public void save(Utilizador user) {
        Connection con = Conecta.getConnection();
        PreparedStatement stmt = null;

        try {
            stmt = con.prepareStatement("INSERT INTO tab_users (nome_utilizador, username, senha, acesso_caixa, acesso_gestao, acesso_contabilista, acesso_admin) "
                    + "VALUES (?, ?, ?, ?, ?, ?,?)");

            stmt.setString(1, user.getNome());
            stmt.setString(2, user.getUsername());
            stmt.setString(3, user.getSenha());
            stmt.setBoolean(4, user.isAcessoCaixa());
            stmt.setBoolean(5, user.isAcessoGestao());
            stmt.setBoolean(6, user.isAcessoContabilista());
            stmt.setBoolean(7, user.isAcessoAdmin());

            stmt.executeUpdate();
            JOptionPane.showMessageDialog(null, "Utilizador cadastrado com sucesso.");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro ao cadastrar" + e.getMessage());
            System.out.println("Erro ao cadastrar" + e.getMessage());

        } finally {
            Conecta.closeConnection(con, stmt, null);
        }
    }

    @Override
    public List<Utilizador> read() {
        List<Utilizador> usuarios = new ArrayList<>();
        Connection con = Conecta.getConnection();
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            stmt = con.prepareStatement("SELECT * FROM tab_users order by nome_utilizador");
            rs = stmt.executeQuery();

            while (rs.next()) {
                Utilizador usuario = new Utilizador(
                        rs.getInt("id_user"),
                        rs.getString("nome_utilizador"),
                        rs.getString("username"),
                        rs.getString("senha"),
                        rs.getBoolean("acesso_caixa"),
                        rs.getBoolean("acesso_gestao"),
                        rs.getBoolean("acesso_contabilista"),
                        rs.getBoolean("acesso_admin")
                );
                
                usuarios.add(usuario);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro ao tentar ler: " + e.getMessage());
        } finally {
            Conecta.closeConnection(con, stmt, rs);
        }

        return usuarios;
    }

    public static Utilizador procurarUtilizadorPeloNome(String nome) {
        Connection con = Conecta.getConnection();
        PreparedStatement stmt = null;
        ResultSet rs = null;
        Utilizador usuario = null;

        try {
            stmt = con.prepareStatement("SELECT * FROM tab_users WHERE nome_utilizador = ?");
            stmt.setString(1, nome);
            rs = stmt.executeQuery();

            if (rs.next()) {
                return new Utilizador(
                        rs.getInt("id_user"),
                        rs.getString("username"),
                        rs.getString("nome_utilizador"),
                        rs.getString("senha"),
                        rs.getBoolean("acesso_caixa"),
                        rs.getBoolean("acesso_gestao"),
                        rs.getBoolean("acesso_contabilista"),
                        rs.getBoolean("acesso_admin")
                );
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro ao tentar pesquisar: " + e);
        } finally {
            Conecta.closeConnection(con, stmt, rs);
        }

        return usuario;

    }

    @Override
    public void update(Utilizador user) {
        Connection con = Conecta.getConnection();
        PreparedStatement stmt = null;

        try {
            StringBuilder sql = new StringBuilder("UPDATE tab_users SET");

            if (user.getUsername() != null) {
                sql.append(" username = ?,");
            }

            if (user.getNome() != null) {
                sql.append(" nome_utilizador = ?,");
            }

            if (user.getSenha() != null) {
                sql.append(" senha = ?,");
            }

            sql.append(" acesso_caixa = ?, acesso_gestao = ?, acesso_contabilista = ?, acesso_admin = ? ");
            sql.append("WHERE id_user = ?");

            stmt = con.prepareStatement(sql.toString());

            int parameterIndex = 1;

            if (user.getUsername() != null) {
                stmt.setString(parameterIndex++, user.getUsername());
            }

            if (user.getNome() != null) {
                stmt.setString(parameterIndex++, user.getNome());
            }

            if (user.getSenha() != null) {
                stmt.setString(parameterIndex++, user.getSenha());
            }
            stmt.setBoolean(parameterIndex++, user.isAcessoCaixa());
            stmt.setBoolean(parameterIndex++, user.isAcessoGestao());
            stmt.setBoolean(parameterIndex++, user.isAcessoContabilista());
            stmt.setBoolean(parameterIndex++, user.isAcessoAdmin());
            stmt.setInt(parameterIndex, user.getId());

            stmt.executeUpdate();
            JOptionPane.showMessageDialog(null, "Dados do utilizador atualizado com sucesso.");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro ao atualizar: " + e);
            System.out.println(e.getMessage());
        } finally {
            Conecta.closeConnection(con, stmt);
        }
    }

    @Override
    public void Delete(Utilizador user) {
        Connection con = Conecta.getConnection();
        PreparedStatement stmt = null;

        try {
            stmt = con.prepareStatement("DELETE FROM tab_users WHERE username = ?");
            stmt.setString(1, user.getUsername());

            stmt.executeUpdate();
            JOptionPane.showMessageDialog(null, "Utilizador Excluido com sucesso.");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro ao tentar excluir: " + e);
        } finally {
            Conecta.closeConnection(con, stmt);
        }
    }

    public ResultSet autenticacaoDoUtilizador(Utilizador obUserDto) {
        Connection conn = Conecta.getConnection();

        try {
            PreparedStatement pstm = conn.prepareStatement("select * from tab_users where username = ? and senha = ? ");
            pstm.setString(1, obUserDto.getUsername());
            pstm.setString(2, obUserDto.getSenha());

            ResultSet rs = pstm.executeQuery();
            return rs;

        } catch (SQLException erro) {
            JOptionPane.showMessageDialog(null, "Login erro: " + erro.getMessage());
            return null;
        }finally{
            Conecta.closeConnection(conn);
        }

    }

    public String retornarNivelDeAcessoPeloNome(String nome) {
        Connection con = Conecta.getConnection();
        PreparedStatement stmt = null;
        ResultSet rs = null;
        String nivelDeAcesso = null;

        try {
            stmt = con.prepareStatement("SELECT * FROM tab_users WHERE nome_utilizador = ?");
            stmt.setString(1, nome);
            rs = stmt.executeQuery();

            if (rs.next()) {
                boolean acessoCaixa = rs.getBoolean("acesso_caixa");
                boolean acessoGestao = rs.getBoolean("acesso_gestao");
                boolean acessoContabilista = rs.getBoolean("acesso_contabilista");
                boolean acessoAdmin = rs.getBoolean("acesso_admin");

                if (acessoAdmin) {
                    nivelDeAcesso = "Admin";
                } else if (acessoContabilista) {
                    nivelDeAcesso = "Contabilista";
                } else if (acessoGestao) {
                    nivelDeAcesso = "Gestão";
                } else if (acessoCaixa) {
                    nivelDeAcesso = "Caixa";
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error: " + e);
        } finally {
            Conecta.closeConnection(con, stmt, rs);
        }

        return nivelDeAcesso;
    }

    public Utilizador retornarUserPeloID(int id) {
        Connection con = Conecta.getConnection();
        PreparedStatement stmt = null;
        ResultSet rs = null;
        Utilizador usuario = null;

        try {
            stmt = con.prepareStatement("SELECT * FROM tab_users WHERE id_user = ?");
            stmt.setInt(1, id);
            rs = stmt.executeQuery();

            if (rs.next()) {
                usuario = new Utilizador();
                usuario.setId(rs.getInt("id_user"));
                usuario.setNome(rs.getString("nome_utilizador"));
                usuario.setSenha(rs.getString("senha"));
                usuario.setAcessoCaixa(rs.getBoolean("acesso_caixa"));
                usuario.setAcessoGestao(rs.getBoolean("acesso_gestao"));
                usuario.setAcessoContabilista(rs.getBoolean("acesso_contabilista"));
                usuario.setAcessoAdmin(rs.getBoolean("acesso_admin"));

            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error" + e);
        } finally {
            Conecta.closeConnection(con, stmt, rs);
        }

        return usuario;

    }

    public String procurarNomeCompleto(String username) {
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        String nomeCompleto = null;
        try {
            con = Conecta.getConnection();
            stmt = con.prepareStatement("SELECT nome_utilizador FROM tab_users WHERE username = ?");
            stmt.setString(1, username);
            rs = stmt.executeQuery();

            if (rs.next()) {
                nomeCompleto = rs.getString("nome_utilizador");
            }
        } catch (SQLException e) {
             JOptionPane.showMessageDialog(null, "Erro: " + e);
        } finally {
            Conecta.closeConnection(con, stmt, rs);
        }

        return nomeCompleto;
    }

    @Override
    public Utilizador retornar(String username) {
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        Utilizador usuario = null;

        try {
            con = Conecta.getConnection();
            stmt = con.prepareStatement("SELECT * FROM tab_users WHERE username = ?");
            stmt.setString(1, username);
            rs = stmt.executeQuery();

            if (rs.next()) {
                usuario = new Utilizador();
                usuario.setId(rs.getInt("id_user"));
                usuario.setNome(rs.getString("nome_utilizador"));
                usuario.setUsername(rs.getString("username"));
                //usuario.setSenha(rs.getString("senha"));
                usuario.setAcessoCaixa(rs.getBoolean("acesso_caixa"));
                usuario.setAcessoGestao(rs.getBoolean("acesso_gestao"));
                usuario.setAcessoContabilista(rs.getBoolean("acesso_contabilista"));
                usuario.setAcessoAdmin(rs.getBoolean("acesso_admin"));
            }
        } catch (SQLException e) {
           JOptionPane.showMessageDialog(null, "Erro: " + e);
        } finally {
            Conecta.closeConnection(con, stmt, rs);
        }

        return usuario;
    }

    public String procurarUsername(String nomeCompleto) {
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        String username = null;
        try {
            con = Conecta.getConnection();
            stmt = con.prepareStatement("SELECT username FROM tab_users WHERE nome_utilizador = ?");
            stmt.setString(1, nomeCompleto);
            rs = stmt.executeQuery();

            if (rs.next()) {
                username = rs.getString("username");
            }
        } catch (SQLException e) {
          JOptionPane.showMessageDialog(null, "Erro: " + e.getMessage());
            System.out.println(e.getMessage());
        } finally {
            Conecta.closeConnection(con, stmt, rs);
        }

        return username;
    }

}
