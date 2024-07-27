package mz.co.fer.View;

import mz.co.fer.Conexao.Conecta;
import mz.co.fer.DAO.ProdutoDAO;
import mz.co.fer.DAO.TrocaDAO;
import mz.co.fer.DAO.VendasDAO;
import mz.co.fer.DTO.Produto;
import mz.co.fer.DTO.TrocaProd;
import mz.co.fer.DTO.Vendas;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Deockilion
 */
public class FrmReturn extends javax.swing.JDialog {

    private String supervisor;

    /**
     * Classe para devoluçao ou troca de produtos
     *
     * @param parent
     * @param modal
     */
    public FrmReturn(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        setLocationRelativeTo(null);
        txtIDrecibo.requestFocus();
    }

    public void nomeSupervisor(String nomeCompleto) {
        this.supervisor = nomeCompleto;
    }

    /**
     * Mandar os produtos a serem devolvidos para a tela do caixa
     */
    private void sendDataBackToFrame() {
        if (jTreturn.getSelectedRow() != -1) {
            double getQtd = (double) jTreturn.getValueAt(jTreturn.getSelectedRow(), 2);
            try {
                int qtdV = Integer.parseInt(JOptionPane.showInputDialog(this, "Introduza a quantidade"));
                if (qtdV > getQtd || qtdV <= 0) {
                    JOptionPane.showMessageDialog(this, "A quantidade não pode ser maior que a existente ou negativa!", "Atenção", JOptionPane.INFORMATION_MESSAGE);
                } else {

                    int cod = (int) jTreturn.getValueAt(jTreturn.getSelectedRow(), 0);
                    String descricao = (String) jTreturn.getValueAt(jTreturn.getSelectedRow(), 1);
                    double qtdAserDevolvida = -(qtdV);
                    double preco = (double) jTreturn.getValueAt(jTreturn.getSelectedRow(), 3);
                    double subTotal = -(qtdV * preco);

                    ProdutoDAO prodDao = new ProdutoDAO();
                    Produto prod = prodDao.retornarProduto(cod);
                    //Verificar se o produto ainda esta no estoque ou banco de dados
                    if (prod != null) {
                        if (prod.getType().equals("UNIDADE")) {
                            Produto produto = new Produto(cod, descricao, qtdAserDevolvida, preco, subTotal);
                            ((FrmCaixa) getParent()).updateTableData(produto, txtIDrecibo.getText());
                            ((FrmCaixa) getParent()).dadosDoReturn(supervisor, txtIDrecibo.getText());
                        }else{
                            qtdAserDevolvida = - (double) jTreturn.getValueAt(jTreturn.getSelectedRow(), 2);
                            subTotal = - (double) jTreturn.getValueAt(jTreturn.getSelectedRow(), 4);
                            Produto produto = new Produto(cod, descricao, qtdAserDevolvida, preco, subTotal);
                            ((FrmCaixa) getParent()).updateTableData(produto, txtIDrecibo.getText());
                            ((FrmCaixa) getParent()).dadosDoReturn(supervisor, txtIDrecibo.getText());
                        }
                    } else {
                        JOptionPane.showMessageDialog(this, "Infelizmnete este produto já não se encontra cadastrado no estoque!"
                                + "\n Não é possivel prosseguir com a troca ou devolução!", "Atenção", JOptionPane.INFORMATION_MESSAGE);
                    }

                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Valor introduzido inválido. Insira um número válido", "Informação", JOptionPane.WARNING_MESSAGE);
            }

        } else {
            JOptionPane.showMessageDialog(this, "Por favor, selecione um produto!");
        }
    }

    /**
     * Este metodo soma as quantidade devolvida anteriormente no mesmo id do
     * recibo
     *
     * @param id
     * @return quantidade
     */
    private double somarQTDdevolvidas(String id) {
        Connection con = Conecta.getConnection();
        double quantidade = 0.0;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            String sql = "SELECT pd.quantidade FROM tab_produtos_devolvidos pd "
                    + "JOIN tab_return r ON pd.id_venda = r.id "
                    + "WHERE r.idDaTroca = ?";

            stmt = con.prepareStatement(sql);
            stmt.setString(1, id);
            rs = stmt.executeQuery();
            if (rs.next()) {
                quantidade = rs.getDouble(1);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "ERROR", JOptionPane.ERROR_MESSAGE);
        } finally {
            Conecta.closeConnection(con, stmt, rs);
        }
        return quantidade;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTreturn = new javax.swing.JTable();
        txtIDrecibo = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        txtData = new javax.swing.JTextField();
        txtOperador = new javax.swing.JTextField();
        txtTotal = new javax.swing.JTextField();
        jbtAdd = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Return");
        setModal(true);
        setResizable(false);

        jPanel1.setBackground(new java.awt.Color(0, 102, 102));

        jTreturn.setFont(new java.awt.Font("Times New Roman", 0, 12)); // NOI18N
        jTreturn.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Código", "Descrição", "Quantidade", "Preço", "SubTotal"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTreturn.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(jTreturn);

        txtIDrecibo.setToolTipText("Introduza o código ID do recibo.");
        txtIDrecibo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtIDreciboActionPerformed(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel1.setText("ID do Recibo:");

        txtData.setEditable(false);

        txtOperador.setEditable(false);

        txtTotal.setEditable(false);
        txtTotal.setBackground(java.awt.Color.yellow);

        jbtAdd.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/novo-foco.png"))); // NOI18N
        jbtAdd.setText("Add");
        jbtAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtAddActionPerformed(evt);
            }
        });

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel2.setText("Devolução/Troca de Produtos");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 513, Short.MAX_VALUE)
            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel1Layout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                                .addComponent(txtTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 141, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jbtAdd))
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanel1Layout.createSequentialGroup()
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                                    .addComponent(jLabel1)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(txtIDrecibo))
                                .addComponent(txtData, javax.swing.GroupLayout.PREFERRED_SIZE, 149, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGap(18, 18, 18)
                            .addComponent(txtOperador, javax.swing.GroupLayout.PREFERRED_SIZE, 152, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanel1Layout.createSequentialGroup()
                            .addGap(129, 129, 129)
                            .addComponent(jLabel2)))
                    .addContainerGap(49, Short.MAX_VALUE)))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 470, Short.MAX_VALUE)
            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel1Layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(jLabel2)
                    .addGap(18, 18, 18)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtIDrecibo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel1))
                    .addGap(11, 11, 11)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtData, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txtOperador, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 270, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtTotal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jbtAdd))
                    .addContainerGap(49, Short.MAX_VALUE)))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void txtIDreciboActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtIDreciboActionPerformed
        String id = txtIDrecibo.getText();
        TrocaDAO trocaDao = new TrocaDAO();
        VendasDAO prodvenddao = new VendasDAO();

        double qtdDevolvida = 0.0;
        boolean foiDevolvido = false;

        Vendas venda = prodvenddao.lerVendaPorIdRecibo(id);
        TrocaProd troca = trocaDao.retornar(id);
        int rowIndex = -1;
        if (venda != null) {

            if (troca != null) {
                if (venda.getIdRecibo().equals(troca.getIdDoReciboDaTroca())) {

                    String[] columnNames = {"Codigo", "Descricao", "Quantidade", "Preco", "Subtotal"};
                    DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);

                    for (Produto produto : troca.getProdutosVendidos()) {
                        Object[] rowData = {produto.getCodigo(), produto.getDescricao(), produto.getQtdArmazem(), produto.getPreco(), produto.getSubtotal()};
                        tableModel.addRow(rowData);

                    }
                    int rowCount = tableModel.getRowCount();
                    
                    for (int i = 0; i < rowCount; i++) {
                        double qtd = (double) tableModel.getValueAt(i, 2);
                        if (qtd < 0) {
                            rowIndex = i;
                            qtdDevolvida = (double) tableModel.getValueAt(i, 2);
                            foiDevolvido = true;

                        }

                    }

                }
            }
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            txtData.setText(dateFormat.format(venda.getDataVenda()));
            txtOperador.setText(venda.getOperador() + "");
            txtTotal.setText(venda.getValorTotal() + "");
            DefaultTableModel model = (DefaultTableModel) jTreturn.getModel();
            model.setNumRows(0);

            for (Produto p : venda.getProdutosVendidos()) {
                model.addRow(new Object[]{
                    p.getCodigo(),
                    p.getDescricao(),
                    p.getQtdArmazem(),
                    p.getPreco(),
                    p.getSubtotal()
                });

            }
            if (foiDevolvido) {
                
                double qtdInicial = (double) model.getValueAt(rowIndex, 2);
                double novaQtd = qtdInicial + somarQTDdevolvidas(id);
                model.setValueAt(novaQtd, rowIndex, 2);
            }

        } else {
            JOptionPane.showMessageDialog(this, "O ID do recibo informado não existe!", "Atenção", JOptionPane.INFORMATION_MESSAGE);
        }

    }//GEN-LAST:event_txtIDreciboActionPerformed

    private void jbtAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtAddActionPerformed

        sendDataBackToFrame();
    }//GEN-LAST:event_jbtAddActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTreturn;
    private javax.swing.JButton jbtAdd;
    private javax.swing.JTextField txtData;
    private javax.swing.JTextField txtIDrecibo;
    private javax.swing.JTextField txtOperador;
    private javax.swing.JTextField txtTotal;
    // End of variables declaration//GEN-END:variables
}
