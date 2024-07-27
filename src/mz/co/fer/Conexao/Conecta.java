package mz.co.fer.Conexao;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/**
 *
 * @author Fernando
 */
public class Conecta {

//    private static final String DRIVER = "com.mysql.cj.jdbc.Driver";
//    private static final String URL = "jdbc:mysql://192.168.23.1:3306/dbvendas";
//    private static final String USER = "root";
//    private static final String PASS = "";
    private static final String DRIVER = "org.postgresql.Driver";
    private static final String URL = "jdbc:postgresql://192.168.23.1:5433/dbsales";
    private static final String USER = "postgres";
    private static final String PASS = "king123";

    public static Connection getConnection() {
        try {
//            String configFilePath = "src/config.properties";
//            Properties prop = new Properties();
//            
//            FileInputStream propsInput = new FileInputStream(configFilePath);
//            
//            prop.load(propsInput);
            Class.forName(DRIVER);
            return DriverManager.getConnection(URL, USER, PASS);
            //return DriverManager.getConnection(URL, prop);
        } catch (ClassNotFoundException | SQLException ex) {
            throw new RuntimeException("Erro na conexão: ", ex.getCause());
            //JOptionPane.showMessageDialog(null, "Erro na conexão: "+ ex.getMessage());
        } 
    }

    public static void closeConnection(Connection con) {
        try {
            if (con != null) {
                con.close();
            }
        } catch (SQLException ex) {
            Logger.getLogger(Conecta.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void closeConnection(Connection con, PreparedStatement stmt) {

        closeConnection(con);

        try {

            if (stmt != null) {
                stmt.close();
            }

        } catch (SQLException ex) {
            Logger.getLogger(Conecta.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void closeConnection(Connection con, PreparedStatement stmt, ResultSet rs) {

        closeConnection(con, stmt);

        try {

            if (rs != null) {
                rs.close();
            }

        } catch (SQLException ex) {
            Logger.getLogger(Conecta.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
