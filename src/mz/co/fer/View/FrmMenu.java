package mz.co.fer.View;

import mz.co.fer.Outros.Server;

import mz.co.fer.DAO.UtilizadorDAO;
import mz.co.fer.DTO.Utilizador;
import mz.co.fer.DAO.FechoDAO;
import mz.co.fer.DTO.AuthService;
import javax.swing.JOptionPane;

/**
 *
 * @author Deockilion
 */
public class FrmMenu extends javax.swing.JFrame {

    private FrmCaixa telaVenda = null;
    private FrmGestaoDeEstoque gestaoDeEstoque = null;
    private FrmAdmin admin = null;

    public FrmMenu() {
        initComponents();
        jLUserLogado.setText(AuthService.getCurrentUser());
        this.setExtendedState(FrmMenu.MAXIMIZED_BOTH);
        menuItemServer();
    }

    private void menuItemServer() {
        String nome = jLUserLogado.getText();
        Utilizador utilizador = UtilizadorDAO.procurarUtilizadorPeloNome(nome);
        if (utilizador.isAcessoAdmin()) {
            jMenuServer.setEnabled(true);
        } else {
            jMenuServer.setEnabled(false);
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        painelAreaTrabalho = new javax.swing.JDesktopPane();
        jLUserLogado = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        menuAdmin = new javax.swing.JMenuItem();
        menuEstoque = new javax.swing.JMenuItem();
        menuContabilista = new javax.swing.JMenuItem();
        menuVendas = new javax.swing.JMenuItem();
        jMenuLogOut = new javax.swing.JMenuItem();
        menuSair = new javax.swing.JMenu();
        jMenuServer = new javax.swing.JMenuItem();
        jMenuItem5 = new javax.swing.JMenuItem();
        jMenuItem6 = new javax.swing.JMenuItem();
        jMenuSair = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Sistema de Vendas com Gestão de Estoque");
        setBackground(new java.awt.Color(0, 102, 102));
        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        setExtendedState(MAXIMIZED_BOTH);

        painelAreaTrabalho.setBackground(new java.awt.Color(240, 240, 240));
        painelAreaTrabalho.setMaximumSize(new java.awt.Dimension(61, 32767));

        jLUserLogado.setBackground(new java.awt.Color(255, 255, 0));
        jLUserLogado.setFont(new java.awt.Font("Times New Roman", 1, 12)); // NOI18N
        painelAreaTrabalho.add(jLUserLogado);
        jLUserLogado.setBounds(20, 650, 168, 23);

        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/menuPrincipal.jpg"))); // NOI18N
        jLabel2.setText("Created by");
        painelAreaTrabalho.add(jLabel2);
        jLabel2.setBounds(0, 0, 1370, 770);

        jMenuBar1.setBackground(new java.awt.Color(0, 0, 0));

        jMenu1.setText("Arquivo");

        menuAdmin.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F5, 0));
        menuAdmin.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/user-16.png"))); // NOI18N
        menuAdmin.setText("Admin");
        menuAdmin.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuAdminActionPerformed(evt);
            }
        });
        jMenu1.add(menuAdmin);

        menuEstoque.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F3, 0));
        menuEstoque.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/database-16.png"))); // NOI18N
        menuEstoque.setText("Gestão de Estoque");
        menuEstoque.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuEstoqueActionPerformed(evt);
            }
        });
        jMenu1.add(menuEstoque);

        menuContabilista.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F2, 0));
        menuContabilista.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/money-16.png"))); // NOI18N
        menuContabilista.setText("Area Contabilistica");
        menuContabilista.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuContabilistaActionPerformed(evt);
            }
        });
        jMenu1.add(menuContabilista);

        menuVendas.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F1, 0));
        menuVendas.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/shopping-cart-16.png"))); // NOI18N
        menuVendas.setText("Caixa");
        menuVendas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuVendasActionPerformed(evt);
            }
        });
        jMenu1.add(menuVendas);

        jMenuLogOut.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ENTER, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        jMenuLogOut.setText("Log out");
        jMenuLogOut.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuLogOutActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuLogOut);

        jMenuBar1.add(jMenu1);

        menuSair.setText("Sobre");
        menuSair.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuSairActionPerformed(evt);
            }
        });

        jMenuServer.setText("Server");
        jMenuServer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuServerActionPerformed(evt);
            }
        });
        menuSair.add(jMenuServer);

        jMenuItem5.setText("Check for updates");
        menuSair.add(jMenuItem5);

        jMenuItem6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/help.png"))); // NOI18N
        jMenuItem6.setText("About us");
        jMenuItem6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem6ActionPerformed(evt);
            }
        });
        menuSair.add(jMenuItem6);

        jMenuSair.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F4, 0));
        jMenuSair.setText("Exit");
        jMenuSair.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuSairActionPerformed(evt);
            }
        });
        menuSair.add(jMenuSair);

        jMenuBar1.add(menuSair);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(painelAreaTrabalho, javax.swing.GroupLayout.DEFAULT_SIZE, 762, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(painelAreaTrabalho, javax.swing.GroupLayout.DEFAULT_SIZE, 763, Short.MAX_VALUE)
        );

        painelAreaTrabalho.getAccessibleContext().setAccessibleName("");

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void menuAdminActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuAdminActionPerformed
        String nome = jLUserLogado.getText();
        if (admin == null || !admin.isVisible()) {
            Utilizador utilizador = UtilizadorDAO.procurarUtilizadorPeloNome(nome);
            if (utilizador.isAcessoAdmin()) {
                admin = new FrmAdmin();
                admin.setIconImage(this.getIconImage());
                admin.setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this, "Acesso não autorizado.", "Atenção", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            admin.requestFocus();
        }
    }//GEN-LAST:event_menuAdminActionPerformed

    private void menuSairActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuSairActionPerformed
        this.dispose();
    }//GEN-LAST:event_menuSairActionPerformed

    private void jMenuSairActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuSairActionPerformed
        this.dispose();
    }//GEN-LAST:event_jMenuSairActionPerformed

    private void menuVendasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuVendasActionPerformed
        String nome = jLUserLogado.getText();

        Utilizador utilizador = UtilizadorDAO.procurarUtilizadorPeloNome(nome);
        if (telaVenda == null || !telaVenda.isVisible()) {
            if (utilizador.isAcessoContabilista() || utilizador.isAcessoGestao() || utilizador.isAcessoAdmin()) {
                telaVenda = new FrmCaixa();
                telaVenda.setIconImage(this.getIconImage());
                telaVenda.getBtnFinalizar(false);
                telaVenda.setVisible(true);
            } else if (utilizador.isAcessoCaixa()) {
                FechoDAO fechoDAO = new FechoDAO();
                boolean fechoRealizado = fechoDAO.verificarFecho(nome);

                if (fechoRealizado) {
                    JOptionPane.showMessageDialog(this, "Utilizador não pode vender. Fecho do dia já realizado.", "Atenção", JOptionPane.WARNING_MESSAGE);
                } else {
                    telaVenda = new FrmCaixa();
                    telaVenda.setIconImage(this.getIconImage());
                    telaVenda.setVisible(true);
                    dispose();
                }

            } else {
                JOptionPane.showMessageDialog(this, "Acesso não autorizado.", "Atenção", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            telaVenda.requestFocus();
        }
    }//GEN-LAST:event_menuVendasActionPerformed

    private void menuContabilistaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuContabilistaActionPerformed
        String nome = jLUserLogado.getText();

        Utilizador utilizador = UtilizadorDAO.procurarUtilizadorPeloNome(nome);
        if (utilizador.isAcessoContabilista()) {
            FrmContabilista areaContabilista = new FrmContabilista(this, true);
            areaContabilista.setIconImage(this.getIconImage());
            areaContabilista.setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this, "Acesso não autorizado.", "Atenção", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_menuContabilistaActionPerformed

    private void menuEstoqueActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuEstoqueActionPerformed
        String nome = jLUserLogado.getText();
        if (gestaoDeEstoque == null || !gestaoDeEstoque.isVisible()) {
            Utilizador utilizador = UtilizadorDAO.procurarUtilizadorPeloNome(nome);
            if (utilizador.isAcessoGestao()) {
                gestaoDeEstoque = new FrmGestaoDeEstoque();
                gestaoDeEstoque.setIconImage(this.getIconImage());
                gestaoDeEstoque.setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this, "Acesso não autorizado.", "Atenção", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            gestaoDeEstoque.requestFocus();
        }
    }//GEN-LAST:event_menuEstoqueActionPerformed

    private void jMenuLogOutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuLogOutActionPerformed
        String nome = jLUserLogado.getText();
        Server.removeUsuarioOnline(nome);
        this.dispose();
        FrmLogin telaLogin = new FrmLogin();
        telaLogin.setVisible(true);
    }//GEN-LAST:event_jMenuLogOutActionPerformed

    private void jMenuServerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuServerActionPerformed
        Thread serverThread = new Thread(() -> {
            new Server().startServer();
        });
        // Iniciar a thread
        serverThread.start();
    }//GEN-LAST:event_jMenuServerActionPerformed

    private void jMenuItem6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem6ActionPerformed
        Sobre sobre = new Sobre(this, true);
        sobre.setVisible(true);
    }//GEN-LAST:event_jMenuItem6ActionPerformed

   
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLUserLogado;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem5;
    private javax.swing.JMenuItem jMenuItem6;
    private javax.swing.JMenuItem jMenuLogOut;
    private javax.swing.JMenuItem jMenuSair;
    private javax.swing.JMenuItem jMenuServer;
    private javax.swing.JMenuItem menuAdmin;
    private javax.swing.JMenuItem menuContabilista;
    private javax.swing.JMenuItem menuEstoque;
    private javax.swing.JMenu menuSair;
    private javax.swing.JMenuItem menuVendas;
    private javax.swing.JDesktopPane painelAreaTrabalho;
    // End of variables declaration//GEN-END:variables

}