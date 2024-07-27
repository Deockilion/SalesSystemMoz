package mz.co.fer.View;

import mz.co.fer.DAO.ProdutoDAO;
import mz.co.fer.DAO.UtilizadorDAO;
import mz.co.fer.DTO.Vendas;
import mz.co.fer.DTO.Produto;
import mz.co.fer.DTO.Recibo;
import mz.co.fer.DAO.VendasDAO;
import mz.co.fer.DAO.ContabilidadeDAO;
import mz.co.fer.DAO.TrocaDAO;
import mz.co.fer.DTO.Caixa;
import mz.co.fer.DTO.ProdutoDevolvido;
import mz.co.fer.DTO.TrocaProd;
import mz.co.fer.Outros.Tables;
import mz.co.fer.Relatorios.ImpressoraRecibo;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import mz.co.fer.enums.Operacao;

/**
 *
 *
 * @author Fernando
 */
public class PagamentoDinheiro extends javax.swing.JDialog {

    private double total;

    private double valorPago;
    private double valorRecebido;
    private double trocos;
    private JLabel jLUserLogado;
    private JTable jTProdutos;
    private String supervisor;
    private List<ProdutoDevolvido> produtosDevolvidos = new ArrayList<>();

    public PagamentoDinheiro(java.awt.Frame parent, boolean modal, double total) {
        super(parent, modal);
        initComponents();
        //this.setSize(392, 343);
        setLocationRelativeTo(null);
        txtValorTotal.setText(total + "");
        this.jLUserLogado = new JLabel();
        this.jTProdutos = new JTable();

        if (txtValorPago.getText().isBlank() && txtFaltaPagar.getText().isBlank()) {
            txtValorTotal.selectAll();
            txtValorRecebido.setText(txtValorTotal.getSelectedText());
            txtValorRecebido.selectAll();
        }
    }

    public void dadosDoReturn(String supervisor, List<ProdutoDevolvido> produtosDevolvidos) {
        this.supervisor = supervisor;
        this.produtosDevolvidos = produtosDevolvidos;
    }

    public PagamentoDinheiro(double total) {
        txtValorTotal.setText(total + "");
    }

    public void mostrarNomeOperador(String nomeUtilizador) {
        jLUserLogado.setText(nomeUtilizador);
    }

    public void setTabelaProdutos(DefaultTableModel tabelaProdutos) {
        jTProdutos.setModel(tabelaProdutos);
    }

    /**
     * calcula os trocos do cliente
     * @throws java.sql.SQLException
     */
    public void calcularTroco() throws SQLException {
        try {
            double valorTotal = Double.parseDouble(txtValorTotal.getText());
            double valorRecebidoo = Double.parseDouble(txtValorRecebido.getText());

            double troco = valorRecebidoo - valorTotal;
            jLTrocos.setText(String.format("%.2f", troco));

            if (troco >= 0) {
                //imprime o recibo no console da IDE
                imprimirRecibo();

                ProdutoDAO prodDao = new ProdutoDAO();
                //reduzir a quantidade vendida no estoque
                List<Produto> produtosNaTabela = Tables.getProdutosFromTable((DefaultTableModel) jTProdutos.getModel());
                prodDao.reduzirQuantidadeProdutos(produtosNaTabela);

                if (produtosDevolvidos != null) {
                    TrocaDAO trocaDao = new TrocaDAO();
                    TrocaProd troca = new TrocaProd();

                    for (ProdutoDevolvido produtoDevolvido : produtosDevolvidos) {
                        troca.setIdDoReciboDaTroca(produtoDevolvido.getIdDoReciboDaTroca());
                        troca.setAutorizedBy(supervisor);
                        troca.setOperador(jLUserLogado.getText());
                        troca.setProdutoAdevolver(produtoDevolvido.getProduto());
                        trocaDao.save(troca);
                    }
                    produtosDevolvidos.clear();
                }

                //Cadastrar dados da venda            
                VendasDAO vendaDao = new VendasDAO();
                Vendas venda = new Vendas();

                venda.setOperador(jLUserLogado.getText());
                venda.setIdRecibo(PagamentoCartao.gerarProximoID());
                venda.setProdutosVendidos(produtosNaTabela);
                venda.setValorTotal(valorTotal);
                vendaDao.inserirVendaNoBancoDeDados(venda);

                ContabilidadeDAO vendDao = new ContabilidadeDAO();

                String userLo = jLUserLogado.getText();
                UtilizadorDAO userDao = new UtilizadorDAO();
                String username = userDao.procurarUsername(userLo);
                String nomeCompleto = userDao.procurarNomeCompleto(username);
                boolean count = vendDao.verificarCaixa(userLo);
                
                //salvar cada valor vendido para contabilista
                if (count) {
                    Caixa caixa = new Caixa(nomeCompleto, valorTotal, 0);
                    vendDao.saveCount(caixa);
                } else {
                    Caixa caixa = new Caixa(nomeCompleto, valorTotal, 0);
                    vendDao.salvarValorDaVenda(caixa);
                }

                //Imprimir o recibo na impressora
//                Recibo recibo = new Recibo();
//                recibo.setDataVenda(new Date());
//                recibo.setOperador(jLUserLogado.getText());
//                recibo.setProdutosVendidos(produtosNaTabela);
//                recibo.setValorTotal(valorTotal);
//                recibo.setIdRecibo(PagamentoCartao.gerarProximoID());
//                ImpressoraRecibo.imprimirRecibo(recibo); //imprime na impressora

                //Limpar a tabela da janela TelaVendas e passar os trocos e setar alguns componentes desta mesma janela
                ((FrmCaixa) getParent()).limparTabela(jTProdutos);
                ((FrmCaixa) getParent()).trocos(troco);
                ((FrmCaixa) getParent()).setarJLabels();

            } else {

                txtValorPago.setText(valorRecebidoo + "");
                double valorPagoLocal = Double.parseDouble(txtValorPago.getText());

                PagamentoCartao vendaCartao = new PagamentoCartao((Frame) getParent(), true, valorTotal);
                vendaCartao.setValorPago(valorPagoLocal);
                double reman = valorTotal - valorPagoLocal;
                vendaCartao.setFaltaPagar(reman);
                vendaCartao.setTabelaProdutos((DefaultTableModel) jTProdutos.getModel());
                vendaCartao.dadosDoReturn(supervisor, produtosDevolvidos);
                vendaCartao.mostrarNomeOperador(jLUserLogado.getText());
                vendaCartao.setVisible(true);
                dispose();

            }

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Valor inválido.");
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

        txtValorRecebido = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        txtValorPago = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        txtValorTotal = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        txtFaltaPagar = new javax.swing.JTextField();
        jPanel1 = new javax.swing.JPanel();
        jLTrocos = new javax.swing.JLabel();
        jbtFinalize = new javax.swing.JButton();
        jbCancelar = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Pagamento a Cash");
        setModal(true);
        setResizable(false);

        txtValorRecebido.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        txtValorRecebido.setToolTipText("Só pode introduzir apenas números!");
        txtValorRecebido.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtValorRecebidoKeyPressed(evt);
            }
        });

        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel4.setText("Diferença");

        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel3.setText("Valor Recebido");

        txtValorPago.setEditable(false);
        txtValorPago.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        txtValorPago.setFocusable(false);

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel1.setText("Valor Total");

        txtValorTotal.setEditable(false);
        txtValorTotal.setBackground(new java.awt.Color(255, 255, 0));
        txtValorTotal.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        txtValorTotal.setFocusable(false);
        txtValorTotal.setRequestFocusEnabled(false);

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel2.setText("Valor pago");

        txtFaltaPagar.setEditable(false);
        txtFaltaPagar.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        txtFaltaPagar.setFocusable(false);

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Trocos"));

        jLTrocos.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(48, 48, 48)
                .addComponent(jLTrocos, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(59, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLTrocos, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(21, Short.MAX_VALUE))
        );

        jbtFinalize.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/finalizar-foco.png"))); // NOI18N
        jbtFinalize.setText("Finalizar");
        jbtFinalize.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtFinalizeActionPerformed(evt);
            }
        });

        jbCancelar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/cancelar.png"))); // NOI18N
        jbCancelar.setText("Cancelar");
        jbCancelar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbCancelarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(38, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(jLabel1)
                            .addGap(42, 42, 42)
                            .addComponent(txtValorTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 185, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(jLabel3)
                            .addGap(18, 18, 18)
                            .addComponent(txtValorRecebido, javax.swing.GroupLayout.PREFERRED_SIZE, 185, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(layout.createSequentialGroup()
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jLabel2)
                                .addComponent(jLabel4))
                            .addGap(43, 43, 43)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(txtFaltaPagar, javax.swing.GroupLayout.PREFERRED_SIZE, 185, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(txtValorPago, javax.swing.GroupLayout.PREFERRED_SIZE, 185, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(jbtFinalize)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jbCancelar))))
                .addGap(73, 73, 73))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(27, 27, 27)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(3, 3, 3)
                        .addComponent(jLabel1))
                    .addComponent(txtValorTotal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(3, 3, 3)
                        .addComponent(jLabel2))
                    .addComponent(txtValorPago, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(12, 12, 12)
                        .addComponent(jLabel4))
                    .addGroup(layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtFaltaPagar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(15, 15, 15)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(3, 3, 3)
                        .addComponent(jLabel3))
                    .addComponent(txtValorRecebido, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(29, 29, 29)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jbtFinalize)
                    .addComponent(jbCancelar))
                .addContainerGap(26, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jbCancelarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbCancelarActionPerformed
        dispose();
    }//GEN-LAST:event_jbCancelarActionPerformed

    private void jbtFinalizeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtFinalizeActionPerformed
        String valorEmFalta = txtFaltaPagar.getText();
        String valorPago = txtValorPago.getText();
        String valorrecebido = txtValorRecebido.getText();

        if (valorEmFalta.isEmpty() && valorPago.isEmpty()) {
            try {
                try {
                    double valorrecebid = Double.parseDouble(txtValorRecebido.getText());
                    double valortotal = Double.parseDouble(txtValorTotal.getText());
                    if (valortotal > 0 && valorrecebid < 0) {
                        JOptionPane.showMessageDialog(this, "Não pode introduzir um valor negativo!", "Atenção", JOptionPane.WARNING_MESSAGE);
                        txtValorRecebido.setText("");

                    } else if (valortotal < 0 && valorrecebid < 0) {
                        FrmAuthentic au = new FrmAuthentic((Frame) getParent(), true);
                        au.setOperacao(Operacao.CALCULAR);
                        au.setVisible(true);                        
                        dispose();

                    } else {
                        calcularTroco();
                        dispose();
                    }

                } catch (NumberFormatException numberFormatException) {
                    JOptionPane.showMessageDialog(this, "Valor inválido. Insira um número válido!");
                    txtValorRecebido.setText("");
                }

            } catch (SQLException ex) {
                Logger.getLogger(PagamentoDinheiro.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            try {
                double troco = Double.parseDouble(valorrecebido) - Double.parseDouble(valorEmFalta);

                jLTrocos.setText(troco + "MT");
                ContabilidadeDAO dadosDao = new ContabilidadeDAO();
                UtilizadorDAO userDao = new UtilizadorDAO();
                String userLo = jLUserLogado.getText();
                String username = userDao.procurarUsername(userLo);
                boolean count = dadosDao.verificarCaixa(userLo);

                
                //salvar cada valor vendido para contabilista
                String nomeCompleto = userDao.procurarNomeCompleto(username);
                if (count) {
                    Caixa caixa = new Caixa(nomeCompleto, Double.parseDouble(valorEmFalta), Double.parseDouble(valorPago));
                    dadosDao.saveCount(caixa);
                } else {
                    Caixa caixa = new Caixa(nomeCompleto, Double.parseDouble(valorEmFalta), Double.parseDouble(valorPago));
                    dadosDao.salvarValorDaVenda(caixa);
                }

                ProdutoDAO prodDao = new ProdutoDAO();

                List<Produto> produtosNaTabel = Tables.getProdutosFromTable((DefaultTableModel) jTProdutos.getModel());

                prodDao.reduzirQuantidadeProdutos(produtosNaTabel);

                double valorTotal = Double.parseDouble(txtValorTotal.getText());
                VendasDAO vendaDao = new VendasDAO();
                Vendas venda = new Vendas();
                if (produtosDevolvidos != null) {
                    TrocaDAO trocaDao = new TrocaDAO();
                    TrocaProd troca = new TrocaProd();

                    for (ProdutoDevolvido produtoDevolvido : produtosDevolvidos) {
                        troca.setIdDoReciboDaTroca(produtoDevolvido.getIdDoReciboDaTroca());
                        troca.setAutorizedBy(supervisor);
                        troca.setOperador(jLUserLogado.getText());
                        troca.setProdutoAdevolver(produtoDevolvido.getProduto());
                        trocaDao.save(troca);
                    }
                    produtosDevolvidos.clear();
                }
                venda.setOperador(jLUserLogado.getText());
                venda.setIdRecibo(PagamentoCartao.gerarProximoID());
                venda.setProdutosVendidos(produtosNaTabel);
                venda.setValorTotal(valorTotal);
                vendaDao.inserirVendaNoBancoDeDados(venda);

//                Recibo recibo = new Recibo();
//                recibo.setDataVenda(new Date());
//                recibo.setOperador(jLUserLogado.getText());
//                recibo.setProdutosVendidos(produtosNaTabel);
//                recibo.setValorTotal(valorTotal);
//                recibo.setIdRecibo(PagamentoCartao.gerarProximoID());
                //ImpressoraRecibo.imprimirRecibo(recibo); //imprime na impressora

                imprimirRecibo();
                
                ((FrmCaixa) getParent()).limparTabela(jTProdutos);
                ((FrmCaixa) getParent()).trocos(troco);
                ((FrmCaixa) getParent()).setarJLabels();
                dispose();

            } catch (NumberFormatException numberFormatException) {
                JOptionPane.showMessageDialog(this, "Valor inválido. Insira um número válido!");
                txtValorRecebido.setText("");
            }
        }
    }//GEN-LAST:event_jbtFinalizeActionPerformed

    private void txtValorRecebidoKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtValorRecebidoKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            ActionEvent evtt = null;
            jbtFinalizeActionPerformed(evtt);
        }
    }//GEN-LAST:event_txtValorRecebidoKeyPressed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLTrocos;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JButton jbCancelar;
    private javax.swing.JButton jbtFinalize;
    private javax.swing.JTextField txtFaltaPagar;
    private javax.swing.JTextField txtValorPago;
    private javax.swing.JTextField txtValorRecebido;
    private javax.swing.JTextField txtValorTotal;
    // End of variables declaration//GEN-END:variables

    public void setValorPago(double valorPago) {
        txtValorPago.setText(valorPago + "");
    }

    public void setValorRecebido(double valorRecebido) {
        this.valorRecebido = valorRecebido;
    }

    public void setTrocos(float trocos) {
        this.trocos = trocos;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    /**
     * imprime o recibo da venda para o cliente no console
     */
    private void imprimirRecibo() {
        try (PrintWriter pw = new PrintWriter(System.out)) {
            pw.println("----------------------------------------------------------------");
            pw.println("                       FERNANDO SUPERSPAR                       ");
            pw.println("Av. Ahmed Sekou Touré nº 610 Maputo");
            pw.println("NUIT: 700057585                     CONTACTO:843450746/877226931");
            pw.println("----------------------------------------------------------------");
            pw.println("Recibo de Venda");
            Date date = new Date();
            DateFormat format = DateFormat.getDateTimeInstance();
            pw.println("Data: " + format.format(date));
            pw.println("----------------------------------------------------------------");
            pw.println("|  Código |      Descrição      |  Quantidade  |    Preço   |  SubTotal  |");
            DefaultTableModel modelo = (DefaultTableModel) jTProdutos.getModel();
            for (int i = 0; i < modelo.getRowCount(); i++) {
                // Imprime uma linha da tabela
                pw.format("%10s | %20s | %10s | %10s | %10s |",
                        modelo.getValueAt(i, 0),
                        modelo.getValueAt(i, 1),
                        modelo.getValueAt(i, 2),
                        modelo.getValueAt(i, 3),
                        modelo.getValueAt(i, 4));
                pw.println();
            }
            pw.println("----------------------------------------------------------------");
            String valorpago = txtValorPago.getText();
            if (valorpago.isEmpty()) {
                pw.println("Venda Dinheiro");
                pw.println("Total da compra: " + txtValorTotal.getText());
                pw.println("Valor pago: " + txtValorRecebido.getText());
                pw.println("Trocos: " + jLTrocos.getText());

            } else {
                pw.println("Total da compra: " + txtValorTotal.getText());
                pw.println("Valor pago a cartão: " + txtValorPago.getText());
                pw.println("Valor pago a dinheiro: " + txtValorRecebido.getText());
                pw.println("Trocos: " + jLTrocos.getText());
            }
            pw.println("----------------------------------------------------------------");
            String user = jLUserLogado.getText();
            pw.println("Operador: " + user);
            pw.println("ID do Recibo: " + PagamentoCartao.gerarProximoID());
            pw.println("----------------------------------------------------------------");
            pw.println("                 Obrigado pela preferência!                     ");
            pw.println("----------------------------------------------------------------");
        }
    }

    /**
     * @param faltaPagar the faltaPagar to set
     */
    public void setFaltaPagar(double faltaPagar) {
        txtFaltaPagar.setText(faltaPagar + "");
    }

}
