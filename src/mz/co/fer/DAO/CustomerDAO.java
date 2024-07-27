package mz.co.fer.DAO;

import mz.co.fer.Conexao.Conecta;
import mz.co.fer.DTO.CustomerVD;
import java.util.List;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import javax.swing.JOptionPane;

/**
 *
 * @author Deockilion
 */
public class CustomerDAO implements IDAO<CustomerVD>{
    PreparedStatement stmt = null;

    @Override
    public void save(CustomerVD c) {
         Connection con = Conecta.getConnection();
        try {
            stmt = con.prepareStatement("INSERT INTO tab_customer (empresa, nuit, endereco) VALUES (?,?,?)");
            
            stmt.setString(1, c.getEmpresa());
            stmt.setInt(2, c.getNuit());
            stmt.setString(3, c.getEndereco());
            stmt.executeUpdate();
            JOptionPane.showMessageDialog(null, "Dados da empresa salvos com sucesso!");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro FechoDAO" + e.getMessage());
        } finally {
            Conecta.closeConnection(con, stmt);
        }
    }

    @Override
    public void update(CustomerVD c) {
        Connection con = Conecta.getConnection();       
        try {          
            stmt = con.prepareStatement("UPDATE tab_customer SET empresa = ? where id = ?");
            stmt.setString(1, c.getEmpresa());
            stmt.setInt(2, c.getId());
            stmt.executeUpdate();
            
            JOptionPane.showMessageDialog(null, "Atualizado com sucesso!");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro fechoDAO: " + e.getMessage());
        } finally {
            Conecta.closeConnection(con, stmt);
        }
        
    }

    @Override
    public void Delete(CustomerVD c) {
         Connection con = Conecta.getConnection();
        try {
            stmt = con.prepareStatement("delete from tab_customer where empresa = ?");
            stmt.setString(1, c.getEmpresa());
            stmt.executeUpdate();
            //JOptionPane.showMessageDialog(null, "Excluído com sucesso!");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Erro ao excluir: " + ex);
        } finally {
            Conecta.closeConnection(con, stmt);
        }
    }

    @Override
    public List<CustomerVD> read() {
        Connection con = Conecta.getConnection();
        ResultSet rs = null;
        List<CustomerVD> vd = new ArrayList<>();
        try {
            stmt = con.prepareStatement("SELECT * FROM tab_customer");
            rs = stmt.executeQuery();
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                
                CustomerVD customervd = new CustomerVD();
                
                customervd.setId(rs.getInt("id"));
                customervd.setEmpresa(rs.getString("empresa"));
                customervd.setNuit(rs.getInt("nuit"));
                customervd.setEndereco(rs.getString("endereco"));
                
                vd.add(customervd);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Erro FechoDAO" + ex.getMessage());
        } finally {
            Conecta.closeConnection(con, stmt, rs);
        }
        
        return vd;
    }

    @Override
    public CustomerVD retornar(String empresa) {
        Connection con = Conecta.getConnection();
        CustomerVD custm = new CustomerVD();
        ResultSet rs = null;
        try {
            stmt = con.prepareStatement("SELECT * FROM tab_customer WHERE CAST(nuit AS TEXT) ilike ? OR empresa ilike ?");
            stmt.setString(1, empresa + "%");
            stmt.setString(2, empresa + "%");
            rs = stmt.executeQuery();
            
            if (rs.next()) {               
                custm.setId(rs.getInt("id"));
                custm.setEmpresa(rs.getString("empresa"));
                custm.setNuit(rs.getInt("nuit"));
                custm.setEndereco(rs.getString("endereco"));
                
            } else {
            //JOptionPane.showMessageDialog(null, "não existe!","Atenção", JOptionPane.WARNING_MESSAGE);
            return null;
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Não foi possivel:" + ex);
            System.out.println(ex.getMessage());
        } finally {
            Conecta.closeConnection(con, stmt);
        }
        return custm;
        
    }
   
    
   
}
