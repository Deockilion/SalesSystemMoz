package mz.co.fer.View;

import mz.co.fer.DAO.UtilizadorDAO;
import mz.co.fer.DTO.AuthService;
import mz.co.fer.DTO.Utilizador;
import mz.co.fer.Relatorios.GerarPDF;
import com.itextpdf.text.DocumentException;
import java.io.FileNotFoundException;
import java.io.IOException;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import mz.co.fer.Outros.Server;

/**
 * Tela do FrmAdmin
 *
 * @author Deockilion
 */
public class FrmAdmin extends javax.swing.JFrame {

    /**
     * Creates new form Administrador
     */
    public FrmAdmin() {
        initComponents();
        jLUserLogado.setText(AuthService.getCurrentUser());
        this.setExtendedState(MAXIMIZED_BOTH);
        readJTable();
    }

    /**
     * Faz a leitura da tabela
     */
    private void readJTable() {

        DefaultTableModel modelo = (DefaultTableModel) jTUsers.getModel();
        modelo.setNumRows(0);
        UtilizadorDAO utilizadorDao = new UtilizadorDAO();
        List<Utilizador> utilizadores = utilizadorDao.read();

        utilizadores.stream().forEach((user) -> {
            modelo.addRow(new Object[]{
                user.getId(),
                user.getUsername(),
                user.getNome(),
                user.isAcessoCaixa(),
                user.isAcessoGestao(),
                user.isAcessoContabilista(),
                user.isAcessoAdmin()
            });
        });
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
//                @SuppressWarnings("unchecked");
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        txtNome = new javax.swing.JTextField();
        txtPass = new javax.swing.JPasswordField();
        jbtCadastrar = new javax.swing.JButton();
        jCboxCaixa = new javax.swing.JCheckBox();
        jCboxStock = new javax.swing.JCheckBox();
        jCbAmin = new javax.swing.JCheckBox();
        jCbContabilista = new javax.swing.JCheckBox();
        jLUserLogado = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        txtUserName = new javax.swing.JTextField();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTUsers = new javax.swing.JTable();
        jbtEliminar = new javax.swing.JButton();
        jbtMudarNome = new javax.swing.JButton();
        jbtsetsenha = new javax.swing.JButton();
        jbtAusername = new javax.swing.JButton();
        jbtPrint = new javax.swing.JButton();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenuItem1 = new javax.swing.JMenuItem();
        jMenuItem2 = new javax.swing.JMenuItem();
        jMenuItem3 = new javax.swing.JMenuItem();
        jMenuItem4 = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Administrador");

        jPanel1.setBackground(new java.awt.Color(102, 102, 255));

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel1.setText("Cadastrar Novo Utilizador");

        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel3.setText("Nome Completo:");

        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel4.setText("Senha:");

        jLabel5.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel5.setText("Acesso:");

        txtNome.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtNomeActionPerformed(evt);
            }
        });

        jbtCadastrar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/user_add.png"))); // NOI18N
        jbtCadastrar.setText("Cadastrar");
        jbtCadastrar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtCadastrarActionPerformed(evt);
            }
        });

        jCboxCaixa.setBackground(new java.awt.Color(102, 102, 255));
        jCboxCaixa.setFont(new java.awt.Font("Times New Roman", 0, 12)); // NOI18N
        jCboxCaixa.setText("Caixa");

        jCboxStock.setBackground(new java.awt.Color(102, 102, 255));
        jCboxStock.setFont(new java.awt.Font("Times New Roman", 0, 12)); // NOI18N
        jCboxStock.setText("Stock");

        jCbAmin.setBackground(new java.awt.Color(102, 102, 255));
        jCbAmin.setFont(new java.awt.Font("Times New Roman", 0, 12)); // NOI18N
        jCbAmin.setText("Admin");

        jCbContabilista.setBackground(new java.awt.Color(102, 102, 255));
        jCbContabilista.setFont(new java.awt.Font("Times New Roman", 0, 12)); // NOI18N
        jCbContabilista.setText("Contabilista");

        jLUserLogado.setFont(new java.awt.Font("Times New Roman", 1, 12)); // NOI18N

        jLabel7.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel7.setText("Username:");

        txtUserName.setFont(new java.awt.Font("Times New Roman", 0, 12)); // NOI18N
        txtUserName.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtUserNameActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel4)
                            .addComponent(jLabel7)
                            .addComponent(jLabel3))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtNome)
                            .addComponent(txtUserName)
                            .addComponent(txtPass)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(6, 6, 6)
                                .addComponent(jLabel1))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(5, 5, 5)
                                .addComponent(jLabel5)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jCboxStock)
                                    .addComponent(jCboxCaixa)
                                    .addComponent(jCbContabilista)
                                    .addComponent(jCbAmin)))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(6, 6, 6)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLUserLogado, javax.swing.GroupLayout.PREFERRED_SIZE, 169, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jbtCadastrar))))
                        .addGap(0, 88, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(txtNome, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtUserName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtPass, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(jCboxCaixa))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jCboxStock)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jCbContabilista)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jCbAmin)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jbtCadastrar)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 102, Short.MAX_VALUE)
                .addComponent(jLUserLogado, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(23, 23, 23))
        );

        jPanel2.setBackground(new java.awt.Color(102, 102, 255));
        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Lista de Utilizadores Cadastrados", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 14))); // NOI18N

        jTUsers.setFont(new java.awt.Font("Times New Roman", 0, 14)); // NOI18N
        jTUsers.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "Username", "Nome", "Caixa", "Stock", "Contabilidade", "Administrador"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTUsers.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(jTUsers);

        jbtEliminar.setFont(new java.awt.Font("Times New Roman", 0, 12)); // NOI18N
        jbtEliminar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/remove-16.png"))); // NOI18N
        jbtEliminar.setText("Eliminar");
        jbtEliminar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtEliminarActionPerformed(evt);
            }
        });

        jbtMudarNome.setFont(new java.awt.Font("Times New Roman", 0, 12)); // NOI18N
        jbtMudarNome.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/user_edit.png"))); // NOI18N
        jbtMudarNome.setText("Alterar Nome");
        jbtMudarNome.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtMudarNomeActionPerformed(evt);
            }
        });

        jbtsetsenha.setFont(new java.awt.Font("Times New Roman", 0, 12)); // NOI18N
        jbtsetsenha.setText("Redefinir senha");
        jbtsetsenha.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtsetsenhaActionPerformed(evt);
            }
        });

        jbtAusername.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/user_edit.png"))); // NOI18N
        jbtAusername.setText("Alterar Username");
        jbtAusername.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtAusernameActionPerformed(evt);
            }
        });

        jbtPrint.setText("Imprimir");
        jbtPrint.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtPrintActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jbtMudarNome, javax.swing.GroupLayout.PREFERRED_SIZE, 141, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jbtAusername))
                    .addComponent(jbtPrint, javax.swing.GroupLayout.Alignment.LEADING))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jbtsetsenha)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jbtEliminar)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(27, 27, 27)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 198, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jbtMudarNome)
                    .addComponent(jbtAusername)
                    .addComponent(jbtsetsenha)
                    .addComponent(jbtEliminar))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jbtPrint)
                .addGap(95, 95, 95))
        );

        jMenu1.setText("File");

        jMenuItem1.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F2, 0));
        jMenuItem1.setText("Relatórios");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem1);

        jMenuItem2.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F3, 0));
        jMenuItem2.setText("Movimentos");
        jMenuItem2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem2ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem2);

        jMenuItem3.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_P, java.awt.event.InputEvent.ALT_DOWN_MASK | java.awt.event.InputEvent.CTRL_DOWN_MASK));
        jMenuItem3.setText("Imprimir");
        jMenuItem3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem3ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem3);

        jMenuItem4.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F4, 0));
        jMenuItem4.setText("Sair");
        jMenu1.add(jMenuItem4);

        jMenuBar1.add(jMenu1);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jbtCadastrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtCadastrarActionPerformed
        String nome = txtNome.getText();
        String username = txtUserName.getText();
        boolean acessoAdminSelecionado = jCbAmin.isSelected();
        boolean acessoGestaoSelecionado = jCboxStock.isSelected();
        boolean acessoCaixaSelecionado = jCboxCaixa.isSelected();
        boolean acessoContabilistaSelecionado = jCbContabilista.isSelected();

        if (nome.isEmpty() || username.isEmpty()) {
            JOptionPane.showMessageDialog(this, "O nome e a senha são obrigatórios!", "Atenção", JOptionPane.INFORMATION_MESSAGE);
        } else {
            UtilizadorDAO dao = new UtilizadorDAO();
            Utilizador novoUser = new Utilizador(0, null, null, null, acessoCaixaSelecionado, acessoGestaoSelecionado, acessoContabilistaSelecionado, acessoAdminSelecionado);
            novoUser.setNome(txtNome.getText());
            novoUser.setUsername(txtUserName.getText());
            novoUser.setSenha(String.valueOf(txtPass.getPassword()).trim().isEmpty() ? "12345" : String.valueOf(txtPass.getPassword()));

            Utilizador usuarioExistente = dao.retornar(username);
            if (usuarioExistente != null) {
                JOptionPane.showMessageDialog(this, "O username já existe.", "Atenção", JOptionPane.ERROR_MESSAGE);
                txtUserName.setText("");
            } else {
                dao.save(novoUser);

                readJTable();
                txtNome.setText("");
                txtUserName.setText("");
                txtPass.setText("");
                jCboxCaixa.setSelected(false);
                jCboxStock.setSelected(false);
                jCbContabilista.setSelected(false);
                jCbAmin.setSelected(false);
            }
        }
    }//GEN-LAST:event_jbtCadastrarActionPerformed

    private void jbtEliminarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtEliminarActionPerformed
        
        //String nomeCompletoLogado = jLUserLogado.getText();
        //String currentUser = AuthService.getCurrentUser();
        if (jTUsers.getSelectedRow() != -1) {
            String name = (String) jTUsers.getValueAt(jTUsers.getSelectedRow(), 2);
            Utilizador user = new Utilizador();
            UtilizadorDAO userDao = new UtilizadorDAO();
            
            if (Server.getUsuariosOnline().contains(name)) {
                JOptionPane.showMessageDialog(this, "Utilizador ONLINE: Não é possível eliminar este utilizador!");
                System.out.println(Server.getUsuariosOnline());
            } else {

                user.setUsername(jTUsers.getValueAt(jTUsers.getSelectedRow(), 1).toString());
                userDao.Delete(user);
                readJTable();
            }
        } else {
            JOptionPane.showMessageDialog(null, "Selecione um utilizador para eliminar.");
        }
    }//GEN-LAST:event_jbtEliminarActionPerformed

    private void jbtMudarNomeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtMudarNomeActionPerformed
        if (jTUsers.getSelectedRow() != -1) {
            int idUserSelecionado = (int) jTUsers.getValueAt(jTUsers.getSelectedRow(), 0);
            String novoNome = JOptionPane.showInputDialog(this, "Introduza o novo nome de utilizador:");

            if (novoNome != null && !novoNome.isEmpty()) {
                UtilizadorDAO userDao = new UtilizadorDAO();
                Utilizador utilizador = userDao.retornarUserPeloID(idUserSelecionado);

                if (utilizador != null) {
                    utilizador.setNome(novoNome);
                    userDao.update(utilizador);
                    readJTable();
                } else {
                    JOptionPane.showMessageDialog(null, "Utilizador não encontrado. Selecione um utilizador válido.");
                }
            }
        } else {
            JOptionPane.showMessageDialog(null, "Selecione um utilizador para alterar o nome.");
        }
    }//GEN-LAST:event_jbtMudarNomeActionPerformed

    private void jbtsetsenhaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtsetsenhaActionPerformed
        if (jTUsers.getSelectedRow() != -1) {
            int idUserSelecionado = (int) jTUsers.getValueAt(jTUsers.getSelectedRow(), 0);
            String novoSenha = JOptionPane.showInputDialog(null, "Introduza a nova senha de utilizador:");
            if (novoSenha != null && !novoSenha.isEmpty()) {
                UtilizadorDAO userDao = new UtilizadorDAO();
                Utilizador utilizador = userDao.retornarUserPeloID(idUserSelecionado);

                if (utilizador != null) {
                    utilizador.setSenha(novoSenha);
                    userDao.update(utilizador);
                    readJTable();
                } else {
                    JOptionPane.showMessageDialog(this, "Utilizador não encontrado. Selecione um utilizador válido.");
                }
            }

        } else {
            JOptionPane.showMessageDialog(this, "Selecione um utilizador para alterar a Senha.", "Atenção", JOptionPane.INFORMATION_MESSAGE);
        }
    }//GEN-LAST:event_jbtsetsenhaActionPerformed

    private void jbtAusernameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtAusernameActionPerformed
        if (jTUsers.getSelectedRow() != -1) {
            int idUserSelecionado = (int) jTUsers.getValueAt(jTUsers.getSelectedRow(), 0);
            String novoNome = JOptionPane.showInputDialog(null, "Digite o novo username:");

            if (novoNome != null && !novoNome.isEmpty()) {
                UtilizadorDAO userDao = new UtilizadorDAO();
                Utilizador utilizador = userDao.retornarUserPeloID(idUserSelecionado);

                if (utilizador != null) {
                    utilizador.setUsername(novoNome);
                    userDao.update(utilizador);
                    readJTable();
                } else {
                    JOptionPane.showMessageDialog(null, "Utilizador não encontrado. Selecione um usuário válido.");
                }
            }
        } else {
            JOptionPane.showMessageDialog(null, "Selecione um utilizador para alterar o username.");
        }

    }//GEN-LAST:event_jbtAusernameActionPerformed

    private void txtNomeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtNomeActionPerformed
        txtUserName.requestFocus();
    }//GEN-LAST:event_txtNomeActionPerformed

    private void txtUserNameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtUserNameActionPerformed
        txtPass.requestFocus();
    }//GEN-LAST:event_txtUserNameActionPerformed

    private void jbtPrintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtPrintActionPerformed
        DefaultTableModel modelo = (DefaultTableModel) jTUsers.getModel();
        try {
            new GerarPDF().imprimirListaUsers(modelo);
        } catch (DocumentException | FileNotFoundException ex) {
            Logger.getLogger(FrmGestaoDeEstoque.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(FrmAdmin.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jbtPrintActionPerformed

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
        FrmRelatorio viewRelatorio = new FrmRelatorio(this, true);
        viewRelatorio.setIconImage(this.getIconImage());
        viewRelatorio.setVisible(true);
    }//GEN-LAST:event_jMenuItem1ActionPerformed

    private void jMenuItem2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem2ActionPerformed
        FrmMovimentos movimentos = new FrmMovimentos(this, true);
        movimentos.setIconImage(this.getIconImage());
        movimentos.setVisible(true);
    }//GEN-LAST:event_jMenuItem2ActionPerformed

    private void jMenuItem3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem3ActionPerformed
        jbtPrintActionPerformed(evt);
    }//GEN-LAST:event_jMenuItem3ActionPerformed

 

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox jCbAmin;
    private javax.swing.JCheckBox jCbContabilista;
    private javax.swing.JCheckBox jCboxCaixa;
    private javax.swing.JCheckBox jCboxStock;
    private javax.swing.JLabel jLUserLogado;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JMenuItem jMenuItem3;
    private javax.swing.JMenuItem jMenuItem4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTUsers;
    private javax.swing.JButton jbtAusername;
    private javax.swing.JButton jbtCadastrar;
    private javax.swing.JButton jbtEliminar;
    private javax.swing.JButton jbtMudarNome;
    private javax.swing.JButton jbtPrint;
    private javax.swing.JButton jbtsetsenha;
    private javax.swing.JTextField txtNome;
    private javax.swing.JPasswordField txtPass;
    private javax.swing.JTextField txtUserName;
    // End of variables declaration//GEN-END:variables

}
