package mz.co.fer.View;

import mz.co.fer.Conexao.Conecta;
import mz.co.fer.DAO.ProdutoDAO;
import mz.co.fer.DAO.UtilizadorDAO;
import mz.co.fer.DTO.Contabilidade;
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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Connection;
import mz.co.fer.Outros.IDgerator;

/**
 *
 *
 * @author Fernando
 */
public class PagamentoCartao extends javax.swing.JDialog {

    private JLabel jLUserLogado;
    private JTable jTProdutos;
    private String supervisor;
    private List<ProdutoDevolvido> produtosDevolvidos = new ArrayList<>();

    public PagamentoCartao(java.awt.Frame parent, boolean modal, double total) {
        super(parent, modal);
        initComponents();
        //this.setSize(431, 233);
        setLocationRelativeTo(null);
        txtValorTotal.setText(total + "");
        this.jTProdutos = new JTable();
        this.jLUserLogado = new JLabel();

        if (txtValorPago.getText().isBlank() && txtFaltaPagar.getText().isBlank()) {
            txtValorTotal.selectAll();
            txtValorRecebido.setText(txtValorTotal.getSelectedText());
            txtValorRecebido.selectAll();
        }

    }

    public void mostrarNomeOperador(String nomeUtilizador) {
        jLUserLogado.setText(nomeUtilizador);
    }

    public void dadosDoReturn(String supervisor, List<ProdutoDevolvido> produtosDevolvidos) {
        this.supervisor = supervisor;
        this.produtosDevolvidos = produtosDevolvidos;
    }

    public void setValorPago(double valorPago) {
        txtValorPago.setText(valorPago + "");
    }

    public void setTabelaProdutos(DefaultTableModel tabelaProdutos) {
        jTProdutos.setModel(tabelaProdutos);
    }

    public void setFaltaPagar(double faltaPagar) {
        txtFaltaPagar.setText(faltaPagar + "");
    }

    /**
     * Calcula o valor a pagar
     */
    private void calcular() throws SQLException {
        try {
            double valorTotal = Double.parseDouble(txtValorTotal.getText());
            double valorrecebido = Double.parseDouble(txtValorRecebido.getText());

            if (valorrecebido < valorTotal) {
                txtValorPago.setText(valorrecebido + "");
                txtValorRecebido.setText("");

                PagamentoDinheiro vendaDinheiro = new PagamentoDinheiro((Frame) getParent(), true, valorTotal);
                vendaDinheiro.setValorPago(valorrecebido);
                double reman = valorTotal - valorrecebido;
                vendaDinheiro.setFaltaPagar(reman);
                vendaDinheiro.setTabelaProdutos((DefaultTableModel) jTProdutos.getModel());
                vendaDinheiro.dadosDoReturn(supervisor, produtosDevolvidos);
                vendaDinheiro.mostrarNomeOperador(jLUserLogado.getText());
                vendaDinheiro.setVisible(true);
                dispose();

            } else if (valorTotal == valorrecebido) {
                double valor = valorrecebido - valorTotal;

                //Reduzir a quantidade quantidade a ser vendida no estoque
                ProdutoDAO prodDao = new ProdutoDAO();
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
                }

                ContabilidadeDAO vendaDao = new ContabilidadeDAO();
                String userLo = jLUserLogado.getText();
                UtilizadorDAO userDao = new UtilizadorDAO();
                String username = userDao.procurarUsername(userLo);
                boolean count = vendaDao.verificarCaixa(userLo);

                //salvar cada valor vendido para o contabilista
                String nomeCompleto = userDao.procurarNomeCompleto(username);
                if (count) {
                    Caixa caixa = new Caixa(nomeCompleto, 0, valorTotal);
                    vendaDao.saveCount(caixa);
                } else {
                    Caixa caixa = new Caixa(nomeCompleto, 0, valorTotal);
                    vendaDao.salvarValorDaVenda(caixa);
                }

                DefaultTableModel model = (DefaultTableModel) jTProdutos.getModel();

                List<Produto> produtosNaTabel = Tables.getProdutosFromTable((DefaultTableModel) jTProdutos.getModel());

                //cadastrar dados da venda
                VendasDAO vendaDa = new VendasDAO();
                Vendas venda = new Vendas();
                venda.setOperador(jLUserLogado.getText());
                venda.setIdRecibo(PagamentoCartao.gerarProximoID());
                venda.setProdutosVendidos(produtosNaTabel);
                venda.setValorTotal(valorTotal);
                vendaDa.inserirVendaNoBancoDeDados(venda);

                //Salva os dados no cartão do cliente
                String pos = txtPOS.getText();
                String validade = txtValidade.getText();
                String card = txtnrCard.getText();
                Contabilidade dadosCard = new Contabilidade();
                ContabilidadeDAO contabilidadeDao = new ContabilidadeDAO();

                dadosCard.setIdRecibo(PagamentoCartao.gerarProximoID());
                dadosCard.setNrpos(pos.trim().isEmpty() ? 0 : Integer.parseInt(pos));
                dadosCard.setExpdate(validade.trim().isEmpty() ? 0 : Integer.parseInt(validade));
                dadosCard.setCard(card.trim().isEmpty() ? 0 : Integer.parseInt(card));
                dadosCard.setValor(Double.parseDouble(txtValorTotal.getText()));
                contabilidadeDao.salvarDadosCard(dadosCard);

                //Imprimir o recibo na impressora
                Recibo recibo = new Recibo();
                recibo.setDataVenda(new Date());
                recibo.setOperador(jLUserLogado.getText());
                recibo.setProdutosVendidos(produtosNaTabela);
                recibo.setValorTotal(Double.parseDouble(txtValorTotal.getText()));
                recibo.setIdRecibo(gerarProximoID());
                //ImpressoraRecibo.imprimirRecibo(recibo); //imprime na impressora
                //imprime o recibo no console da IDE
                imprimirRecibo();

                //Limpar a tabela de produtos da janela do Caixa/Vendas
                ((FrmCaixa) getParent()).limparTabela(jTProdutos);
                ((FrmCaixa) getParent()).trocos(valor);
                ((FrmCaixa) getParent()).setarJLabels();

            } else {
                JOptionPane.showMessageDialog(this, "Introduza um valor menor ou igual.", "Erro", JOptionPane.ERROR_MESSAGE);

                txtValorRecebido.setText("");

            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Valor inválido.", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * imprime o recibo da venda no console
     */
    private void imprimirRecibo() {
        try (PrintWriter pw = new PrintWriter(System.out)) {
            pw.println("----------------------------------------------------------------");
            pw.println("                       FERNANDO SUPERSPAR                       ");
            pw.println("Av. Ahmed Sekou Touré nº 610 Mputo");
            pw.println("NUIT: 700057585                    CONTACTO: 21305054, 823011400");
            pw.println("----------------------------------------------------------------");
            pw.println("Recibo de Venda");
            Date date = new Date();
            DateFormat format = DateFormat.getDateTimeInstance();
            pw.println("Data: " + format.format(date));
            pw.println("----------------------------------------------------------------");
            pw.println("|  Código  |    Descrição    |  Quantidade  |    Preço    |  SubTotal  |");
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
                pw.println("Venda Cartão");
                pw.println("Total da compra: " + txtValorTotal.getText());
                pw.println("Valor pago: " + txtValorRecebido.getText());
            } else {
                pw.println("Total da compra: " + txtValorTotal.getText());
                pw.println("Valor pago a dinheiro: " + txtValorPago.getText());
                pw.println("Valor pago a cartão: " + txtValorRecebido.getText());
            }
            pw.println("----------------------------------------------------------------");
            String user = jLUserLogado.getText();
            pw.println("Operador: " + user);
            pw.println("ID do Recibo: " + gerarProximoID());
            pw.println("----------------------------------------------------------------");
            pw.println("                    Obrigado pela preferência!                  ");
            pw.println("----------------------------------------------------------------");
        }
    }

    /**
     * gera um id para o recibo do cliente
     *
     * @return
     */
    public static String gerarProximoID() {
        Connection con = Conecta.getConnection();
        PreparedStatement stmt = null;
        ResultSet rs = null;
        String proximoID = null;

        try {
            stmt = con.prepareStatement("SELECT MAX(id_recibo) AS id_recibo FROM tab_vendas");
            rs = stmt.executeQuery();

            if (rs.next()) {
                proximoID = rs.getString("id_recibo");
                if (proximoID == null) {
                    // Se não há registros, começar com 01A001
                    proximoID = "01A001";
                } else {

                    String prefixo = proximoID.substring(0, 2);
                    char letra = proximoID.charAt(2);
                    String sufixo = proximoID.substring(3, 6);
                    proximoID = IDgerator.gerarIdRecibo(prefixo, letra, sufixo);

                }

            }
        } catch (SQLException e) {
            System.out.println("erro" + e);
        } finally {
            Conecta.closeConnection(con, stmt, rs);
        }

        return proximoID;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        txtValidade = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        txtValorRecebido = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        txtValorTotal = new javax.swing.JTextField();
        jbtFinalize = new javax.swing.JButton();
        jbtCancel = new javax.swing.JButton();
        jLabel6 = new javax.swing.JLabel();
        txtValorPago = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        txtFaltaPagar = new javax.swing.JTextField();
        txtPOS = new javax.swing.JFormattedTextField();
        txtnrCard = new javax.swing.JFormattedTextField();
        jLabel8 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Pagamento a Cartão");
        setModal(true);
        setResizable(false);

        jLabel1.setFont(new java.awt.Font("Times New Roman", 1, 12)); // NOI18N
        jLabel1.setText("POS");

        jLabel2.setFont(new java.awt.Font("Times New Roman", 1, 12)); // NOI18N
        jLabel2.setText("Validade");

        jLabel3.setFont(new java.awt.Font("Times New Roman", 1, 12)); // NOI18N
        jLabel3.setText("Nr card");

        txtValidade.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        txtValidade.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtValidadeActionPerformed(evt);
            }
        });

        jLabel4.setFont(new java.awt.Font("Times New Roman", 1, 12)); // NOI18N
        jLabel4.setText("Valor Recebido");

        txtValorRecebido.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        txtValorRecebido.setToolTipText("Só pode introduzir um valor menor ou igual ao valor Total");
        txtValorRecebido.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtValorRecebidoKeyPressed(evt);
            }
        });

        jLabel5.setFont(new java.awt.Font("Times New Roman", 1, 12)); // NOI18N
        jLabel5.setText("Valor Total");

        txtValorTotal.setEditable(false);
        txtValorTotal.setBackground(new java.awt.Color(255, 255, 51));
        txtValorTotal.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N

        jbtFinalize.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/finalizar-foco.png"))); // NOI18N
        jbtFinalize.setText("Finalizar");
        jbtFinalize.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtFinalizeActionPerformed(evt);
            }
        });

        jbtCancel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/cancelar.png"))); // NOI18N
        jbtCancel.setText("Cancelar");
        jbtCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtCancelActionPerformed(evt);
            }
        });

        jLabel6.setFont(new java.awt.Font("Times New Roman", 1, 12)); // NOI18N
        jLabel6.setText("Valor Pago");

        txtValorPago.setEditable(false);
        txtValorPago.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N

        jLabel7.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel7.setText("Diferença");

        txtFaltaPagar.setEditable(false);
        txtFaltaPagar.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N

        try {
            txtPOS.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.MaskFormatter("###")));
        } catch (java.text.ParseException ex) {
            ex.printStackTrace();
        }
        txtPOS.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        txtPOS.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtPOSActionPerformed(evt);
            }
        });

        try {
            txtnrCard.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.MaskFormatter("####")));
        } catch (java.text.ParseException ex) {
            ex.printStackTrace();
        }
        txtnrCard.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        txtnrCard.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtnrCardActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtValorRecebido, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(75, 75, 75)
                        .addComponent(jbtFinalize)
                        .addGap(32, 32, 32)
                        .addComponent(jbtCancel))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(12, 12, 12)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(19, 19, 19)
                                .addComponent(txtPOS, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(47, 47, 47)
                                .addComponent(jLabel5)
                                .addGap(19, 19, 19)
                                .addComponent(txtValorTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(9, 9, 9)
                                .addComponent(txtValidade, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(50, 50, 50)
                                .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(16, 16, 16)
                                .addComponent(txtValorPago, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(9, 9, 9)
                                .addComponent(txtnrCard, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(50, 50, 50)
                                .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(6, 6, 6)
                                .addComponent(txtFaltaPagar, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtPOS, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtValorTotal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(5, 5, 5)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addComponent(jLabel5))))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtValidade, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtValorPago, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(5, 5, 5)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2)
                            .addComponent(jLabel6))))
                .addGap(23, 23, 23)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(5, 5, 5)
                        .addComponent(jLabel3))
                    .addComponent(txtnrCard, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(3, 3, 3)
                        .addComponent(jLabel7))
                    .addComponent(txtFaltaPagar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(13, 13, 13)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(txtValorRecebido, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(7, 7, 7)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(2, 2, 2)
                        .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jbtFinalize)
                    .addComponent(jbtCancel)))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jbtCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtCancelActionPerformed
        dispose();
    }//GEN-LAST:event_jbtCancelActionPerformed

    private void jbtFinalizeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtFinalizeActionPerformed
        String valorEmFalta = txtFaltaPagar.getText();
        String valorPago = txtValorPago.getText();
        String valorrecebido = txtValorRecebido.getText();

        if (valorEmFalta.isEmpty() && valorPago.isEmpty()) {
            try {
                try {
                    double valortotal = Double.parseDouble(txtValorTotal.getText());
                    double valorrecebid = Double.parseDouble(txtValorRecebido.getText());
                    if (valortotal > 0 && valorrecebid < 0) {
                        JOptionPane.showMessageDialog(this, "Não pode introduzir um valor negativo!", "Atenção", JOptionPane.WARNING_MESSAGE);
                        txtValorRecebido.setText("");

                    } else if (valortotal < 0 && valorrecebid < 0) {
                        JOptionPane.showMessageDialog(this, "Finalize com outro método de pagamento!", "Atenção", JOptionPane.WARNING_MESSAGE);
                        dispose();
                    } else {
                        calcular();
                        dispose();
                    }

                } catch (SQLException ex) {
                    Logger.getLogger(PagamentoCartao.class.getName()).log(Level.SEVERE, null, ex);
                }

            } catch (NumberFormatException numberFormatException) {
                JOptionPane.showMessageDialog(this, "Valor inválido. Insira um número válido!");
                txtValorRecebido.setText("");
            }

        } else {
            // Calcula o troco
            try {
                double valorRR = Double.parseDouble(valorrecebido);
                double valorFF = Double.parseDouble(valorEmFalta);
                if (valorRR == valorFF) {
                    double diferenca = Double.parseDouble(valorrecebido) - Double.parseDouble(valorEmFalta);

                    UtilizadorDAO userDao = new UtilizadorDAO();
                    ContabilidadeDAO contabilidadeDao = new ContabilidadeDAO();
                    String userLo = jLUserLogado.getText();
                    String username = userDao.procurarUsername(userLo);

                    boolean count = contabilidadeDao.verificarCaixa(userLo);
                    //salvar cada valor vendido para o contabilista
                    String nomeCompleto = userDao.procurarNomeCompleto(username);
                    if (count) {
                        Caixa caixa = new Caixa(nomeCompleto, Double.parseDouble(valorPago), Double.parseDouble(valorEmFalta));
                        contabilidadeDao.saveCount(caixa);
                    } else {
                        Caixa caixa = new Caixa(nomeCompleto, Double.parseDouble(valorPago), Double.parseDouble(valorEmFalta));
                        contabilidadeDao.salvarValorDaVenda(caixa);
                    }

                    Contabilidade dados = new Contabilidade();

                    dados.setIdRecibo(gerarProximoID());
                    dados.setNrpos(Integer.parseInt(txtPOS.getText()));
                    dados.setExpdate(Integer.parseInt(txtValidade.getText()));
                    dados.setCard(Integer.parseInt(txtnrCard.getText()));
                    dados.setValor(Double.parseDouble(txtFaltaPagar.getText()));

                    contabilidadeDao.salvarDadosCard(dados);

                    ProdutoDAO prodDao = new ProdutoDAO();
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

                    }
                    VendasDAO vendaDa = new VendasDAO();
                    Vendas venda = new Vendas();
                    venda.setOperador(jLUserLogado.getText());
                    venda.setIdRecibo(PagamentoCartao.gerarProximoID());
                    venda.setProdutosVendidos(produtosNaTabela);
                    venda.setValorTotal(Double.parseDouble(txtValorTotal.getText()));

                    vendaDa.inserirVendaNoBancoDeDados(venda);
                    Recibo recibo = new Recibo();

                    recibo.setDataVenda(new Date());
                    recibo.setOperador(jLUserLogado.getText());
                    recibo.setProdutosVendidos(produtosNaTabela);
                    recibo.setValorTotal(Double.parseDouble(txtValorTotal.getText()));
                    recibo.setIdRecibo(gerarProximoID());
                    //ImpressoraRecibo.imprimirRecibo(recibo);

                    imprimirRecibo();

                    ((FrmCaixa) getParent()).limparTabela(jTProdutos);
                    ((FrmCaixa) getParent()).trocos(diferenca);
                    ((FrmCaixa) getParent()).setarJLabels();
                    dispose();

                } else {
                    JOptionPane.showMessageDialog(this, "Introduza um valor menor ou igual.", "Erro", JOptionPane.ERROR_MESSAGE);

                    txtValorRecebido.setText("");
                }

            } catch (NumberFormatException numberFormatException) {
                JOptionPane.showMessageDialog(this, "Valor inválido. Insira um número válido!");
                txtValorRecebido.setText("");
            }
        }


    }//GEN-LAST:event_jbtFinalizeActionPerformed

    private void txtPOSActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtPOSActionPerformed
        if (txtPOS != null) {
            txtValidade.requestFocus();
        }
    }//GEN-LAST:event_txtPOSActionPerformed

    private void txtValidadeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtValidadeActionPerformed
        if (txtValidade != null) {
            txtnrCard.requestFocus();
        }
    }//GEN-LAST:event_txtValidadeActionPerformed

    private void txtnrCardActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtnrCardActionPerformed
        if (txtnrCard != null) {
            txtValorRecebido.requestFocus();
        }
    }//GEN-LAST:event_txtnrCardActionPerformed

    private void txtValorRecebidoKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtValorRecebidoKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            ActionEvent evtt = null;
            jbtFinalizeActionPerformed(evtt);
        }
    }//GEN-LAST:event_txtValorRecebidoKeyPressed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JButton jbtCancel;
    private javax.swing.JButton jbtFinalize;
    private javax.swing.JTextField txtFaltaPagar;
    private javax.swing.JFormattedTextField txtPOS;
    private javax.swing.JTextField txtValidade;
    private javax.swing.JTextField txtValorPago;
    private javax.swing.JTextField txtValorRecebido;
    private javax.swing.JTextField txtValorTotal;
    private javax.swing.JFormattedTextField txtnrCard;
    // End of variables declaration//GEN-END:variables
}
