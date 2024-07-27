package mz.co.fer.View;

import mz.co.fer.DAO.UtilizadorDAO;
import mz.co.fer.DTO.Contabilidade;
import mz.co.fer.DTO.Fecho;
import mz.co.fer.DAO.FechoDAO;
import mz.co.fer.Relatorios.GerarPDF;
import mz.co.fer.DAO.ContabilidadeDAO;
import mz.co.fer.DTO.AuthService;
import mz.co.fer.DTO.Notas;
import com.itextpdf.text.DocumentException;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.print.DocFlavor;
import javax.print.DocPrintJob;
import javax.print.PrintException;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.SimpleDoc;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import mz.co.fer.DTO.Caixa;
import mz.co.fer.DTO.Utilizador;
import mz.co.fer.enums.Operacao;

/**
 * Form do contabilista, para efetuar o fecho.
 *
 * @author Fernando
 */
public class FrmContabilista extends javax.swing.JDialog {

    private String valor1000,
            valor500,
            valor200,
            valor100,
            valor50,
            valor20,
            cartoes;
    private double total;
    private String moedas;
    private double diferenca;
    private ContabilidadeDAO countDAO = new ContabilidadeDAO();
    // private double valorcash = 0;
    //private double valorcard = 0;

    public FrmContabilista(java.awt.Frame parent, boolean modal) {
        initComponents();
        jLUserLogado.setText(AuthService.getCurrentUser());
        jbtEmitirFecho.setEnabled(false);
        jbtDetalhesVenda.setEnabled(false);
        setSize(1000, 667);
        setLocationRelativeTo(null);
        readJTable();
        readJTabledois();
    }

    /**
     * lê a tabela com os dados referente as vendas a cartão.
     */
    public void readJTable() {
        DefaultTableModel modelo = (DefaultTableModel) jTdados.getModel();
        modelo.setNumRows(0);
        ContabilidadeDAO vendaDao = new ContabilidadeDAO();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        for (Contabilidade v : vendaDao.read()) {
            String dataHoraFormatada = dateFormat.format(v.getData());
            modelo.addRow(new Object[]{
                v.getId(),
                dataHoraFormatada,
                v.getIdRecibo(),
                v.getNrpos(),
                v.getExpdate(),
                v.getCard(),
                v.getValor()
            });
        }
    }

    private void readJTabledois() {
        DefaultTableModel modelo = (DefaultTableModel) jTfecho.getModel();
        modelo.setNumRows(0);
        FechoDAO fechoDao = new FechoDAO();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");

        for (Fecho fecho : fechoDao.read()) {
            String dataHoraFormatada = dateFormat.format(fecho.getData());
            modelo.addRow(new Object[]{
                fecho.getId(),
                dataHoraFormatada,
                fecho.getNome(),
                fecho.getValorTotal(),
                fecho.getValorFalta()
            });
        }
    }

    /**
     * faz a pesquisa da venda pelo ID do recibo
     *
     * @param id
     */
    private void pesquisarIdRecibo(int id) {
        DefaultTableModel modelo = (DefaultTableModel) jTdados.getModel();
        modelo.setNumRows(0);
        ContabilidadeDAO vDao = new ContabilidadeDAO();

        for (Contabilidade dcard : vDao.readForId(id)) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            String dataHoraFormatada = dateFormat.format(dcard.getData());
            modelo.addRow(new Object[]{
                dcard.getId(),
                dataHoraFormatada,
                dcard.getIdRecibo(),
                dcard.getNrpos(),
                dcard.getExpdate(),
                dcard.getCard(),
                dcard.getValor()
            });
        }
    }

    private void sentToPrinter(double card, Notas nota) {
        try {
            Date dataHoraAtual = Date.from(Instant.now());
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            StringBuilder content = new StringBuilder();
            content.append("-----------------------------------\n");
            content.append("           Fecho do Dia            \n");
            content.append("-----------------------------------\n");
            content.append("Data: ").append(dateFormat.format(dataHoraAtual)).append("\n");
            content.append("Nome: ").append(txtNomeCom.getText()).append("\n");
            content.append("-----------------------------------\n");
            content.append("CARTÃO: ").append(card).append("\n");
            content.append("Cartão: ").append(cartoes).append("\n");
            content.append("-----------------------------------\n");
            content.append("1000: ").append(nota.getV1000()).append("\n");
            content.append("500: ").append(nota.getV500()).append("\n");
            content.append("200: ").append(nota.getV200()).append("\n");
            content.append("100: ").append(nota.getV100()).append("\n");
            content.append("50: ").append(nota.getV50()).append("\n");
            content.append("20: ").append(nota.getV20()).append("\n");
            content.append("Moedas: ").append(nota.getMoedas()).append("\n");
            content.append("-----------------------------------\n");
            content.append("TOTAL: ").append(nota.getCard()).append("\n");
            content.append("Total: ").append(total).append("\n");
            content.append("-----------------------------------\n");
            content.append("TOTAL Vendido: ").append(txtValorTotalVenda.getText()).append("\n");
            content.append("Total Recebido: ").append(total).append("\n");
            content.append("Diferença: ").append(diferenca).append("\n");
            content.append("-----------------------------------\n");
            content.append("             ASSINATURA            \n");
            content.append("___________________________________\n");

            // Enviar para a impressora
            PrintService defaultPrintService = PrintServiceLookup.lookupDefaultPrintService();
            DocPrintJob job = defaultPrintService.createPrintJob();
            SimpleDoc doc = new SimpleDoc(content.toString().getBytes(), DocFlavor.BYTE_ARRAY.AUTOSENSE, null);
            job.print(doc, null);
        } catch (PrintException ex) {
            Logger.getLogger(FrmContabilista.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private int parseIntOrDefault(String texto) {
        return texto.trim().isEmpty() ? 0 : Integer.parseInt(texto);
    }

    private double parseDoubleOrDefault(String text) {
        return text.trim().isEmpty() ? 0 : Double.parseDouble(text);
    }

    private void limparCampos() {
        txtValorCard.setText("");
        txtUser.setText("");
        txtValorTotalVenda.setText("");
        txtNomeCom.setText("");
        txtValor1000.setText("");
        txtValor500.setText("");
        txtValor200.setText("");
        txtValor100.setText("");
        txtValor50.setText("");
        txtValor20.setText("");
        txtMoedas.setText("");
        jbtEmitirFecho.setEnabled(false);
        jbtDetalhesVenda.setEnabled(false);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        jLabel14 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTfecho = new javax.swing.JTable();
        jLUserLogado = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        txtUser = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        txtValorCard = new javax.swing.JTextField();
        txtValor1000 = new javax.swing.JTextField();
        txtValor500 = new javax.swing.JTextField();
        txtValor200 = new javax.swing.JTextField();
        txtValor100 = new javax.swing.JTextField();
        txtValor50 = new javax.swing.JTextField();
        txtValor20 = new javax.swing.JTextField();
        txtMoedas = new javax.swing.JTextField();
        jbtEmitirFecho = new javax.swing.JButton();
        jbtVerify = new javax.swing.JButton();
        txtNomeCom = new javax.swing.JTextField();
        txtValorTotalVenda = new javax.swing.JTextField();
        jbtDetalhesVenda = new javax.swing.JButton();
        jbtSetFecho = new javax.swing.JButton();
        jLabel10 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jbtdelete = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTdados = new javax.swing.JTable();
        txtPesquisar = new javax.swing.JTextField();
        jbtPesquisar = new javax.swing.JButton();
        jLabel11 = new javax.swing.JLabel();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenuItem1 = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Contabilidade");
        setModal(true);
        setResizable(false);

        jTabbedPane1.setFocusable(false);

        jPanel1.setBackground(new java.awt.Color(51, 255, 204));
        jPanel1.setLayout(null);

        jLabel14.setFont(new java.awt.Font("Times New Roman", 1, 14)); // NOI18N
        jLabel14.setText("Fecho do Dia");
        jPanel1.add(jLabel14);
        jLabel14.setBounds(23, 13, 79, 17);

        jTfecho.setFont(new java.awt.Font("Times New Roman", 0, 14)); // NOI18N
        jTfecho.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "Data", "Nome", "Valor Total Vendido", "Diferença"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTfecho.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(jTfecho);
        if (jTfecho.getColumnModel().getColumnCount() > 0) {
            jTfecho.getColumnModel().getColumn(0).setMinWidth(50);
            jTfecho.getColumnModel().getColumn(0).setMaxWidth(50);
        }

        jPanel1.add(jScrollPane1);
        jScrollPane1.setBounds(12, 44, 550, 312);

        jLUserLogado.setBackground(new java.awt.Color(0, 0, 0));
        jLUserLogado.setFont(new java.awt.Font("Times New Roman", 1, 12)); // NOI18N
        jLUserLogado.setForeground(new java.awt.Color(255, 255, 255));
        jPanel1.add(jLUserLogado);
        jLUserLogado.setBounds(20, 490, 155, 22);

        jPanel2.setBackground(new java.awt.Color(51, 255, 204));

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel1.setText("Username do Funcionário:");

        txtUser.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtUserActionPerformed(evt);
            }
        });

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel2.setText("1000");

        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel3.setText("500");

        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel4.setText("200");

        jLabel5.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel5.setText("100");

        jLabel6.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel6.setText("50");

        jLabel7.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel7.setText("20");

        jLabel8.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel8.setText("Cartões:");

        jLabel12.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel12.setText("Moedas");

        txtValorCard.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtValorCardActionPerformed(evt);
            }
        });

        txtValor1000.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtValor1000ActionPerformed(evt);
            }
        });

        txtValor500.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtValor500ActionPerformed(evt);
            }
        });

        txtValor200.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtValor200ActionPerformed(evt);
            }
        });

        txtValor100.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtValor100ActionPerformed(evt);
            }
        });

        txtValor50.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtValor50ActionPerformed(evt);
            }
        });

        txtValor20.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtValor20ActionPerformed(evt);
            }
        });

        txtMoedas.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtMoedasKeyPressed(evt);
            }
        });

        jbtEmitirFecho.setText("Emitir Fecho");
        jbtEmitirFecho.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtEmitirFechoActionPerformed(evt);
            }
        });

        jbtVerify.setText("Verificar");
        jbtVerify.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtVerifyActionPerformed(evt);
            }
        });

        txtNomeCom.setEditable(false);

        txtValorTotalVenda.setEditable(false);

        jbtDetalhesVenda.setText("Extrato");
        jbtDetalhesVenda.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtDetalhesVendaActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(txtNomeCom, javax.swing.GroupLayout.PREFERRED_SIZE, 162, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(txtValorTotalVenda))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel3)
                            .addComponent(jLabel4)
                            .addComponent(jLabel5)
                            .addComponent(jLabel6)
                            .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel8)
                                    .addComponent(jLabel2)
                                    .addComponent(jLabel12))
                                .addGap(10, 10, 10)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(txtValorCard, javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel2Layout.createSequentialGroup()
                                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(txtValor1000, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(txtValor500, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(txtValor200, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(txtValor100, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(txtValor50, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(txtValor20, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(txtMoedas, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGap(0, 33, Short.MAX_VALUE))))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jbtEmitirFecho)
                                .addGap(18, 18, 18)
                                .addComponent(jbtDetalhesVenda))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel1)
                                .addGap(18, 18, 18)
                                .addComponent(txtUser, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jbtVerify)))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtUser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jbtVerify)))
                .addGap(10, 10, 10)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtNomeCom, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtValorTotalVenda, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(txtValorCard, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(txtValor1000, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(txtValor500, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(16, 16, 16)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(txtValor200, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(txtValor100, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel6)
                    .addComponent(txtValor50, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtValor20, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(txtMoedas, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel12))
                .addGap(26, 26, 26)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jbtEmitirFecho)
                    .addComponent(jbtDetalhesVenda))
                .addContainerGap(41, Short.MAX_VALUE))
        );

        jPanel1.add(jPanel2);
        jPanel2.setBounds(580, 40, 340, 480);

        jbtSetFecho.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/edit-16.png"))); // NOI18N
        jbtSetFecho.setText("Alterar");
        jbtSetFecho.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtSetFechoActionPerformed(evt);
            }
        });
        jPanel1.add(jbtSetFecho);
        jbtSetFecho.setBounds(12, 369, 100, 25);

        jLabel10.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/contabilista.jpg"))); // NOI18N
        jLabel10.setText("Count");
        jPanel1.add(jLabel10);
        jLabel10.setBounds(-6, -3, 1010, 667);

        jTabbedPane1.addTab("Fecho", jPanel1);

        jPanel3.setLayout(null);

        jbtdelete.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/trash-16.png"))); // NOI18N
        jbtdelete.setText("Eliminar");
        jbtdelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtdeleteActionPerformed(evt);
            }
        });
        jPanel3.add(jbtdelete);
        jbtdelete.setBounds(10, 330, 100, 25);

        jTdados.setFont(new java.awt.Font("Times New Roman", 0, 14)); // NOI18N
        jTdados.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "Data", "ID do Recibo", "Nrª POS", "EXPdate", "Nrª do Cartão", "Valor Pago"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTdados.getTableHeader().setReorderingAllowed(false);
        jScrollPane2.setViewportView(jTdados);
        if (jTdados.getColumnModel().getColumnCount() > 0) {
            jTdados.getColumnModel().getColumn(0).setMinWidth(40);
            jTdados.getColumnModel().getColumn(0).setMaxWidth(40);
            jTdados.getColumnModel().getColumn(1).setMinWidth(150);
            jTdados.getColumnModel().getColumn(1).setMaxWidth(150);
        }

        jPanel3.add(jScrollPane2);
        jScrollPane2.setBounds(12, 56, 604, 270);

        txtPesquisar.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtPesquisarKeyPressed(evt);
            }
        });
        jPanel3.add(txtPesquisar);
        txtPesquisar.setBounds(12, 13, 223, 22);

        jbtPesquisar.setFont(new java.awt.Font("Times New Roman", 0, 12)); // NOI18N
        jbtPesquisar.setText("Pesquisar");
        jbtPesquisar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtPesquisarActionPerformed(evt);
            }
        });
        jPanel3.add(jbtPesquisar);
        jbtPesquisar.setBounds(247, 13, 79, 23);

        jLabel11.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/contabilista.jpg"))); // NOI18N
        jPanel3.add(jLabel11);
        jLabel11.setBounds(2, 0, 1360, 680);

        jTabbedPane1.addTab("Dados Cartão", jPanel3);

        jMenu1.setText("File");

        jMenuItem1.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F2, 0));
        jMenuItem1.setText("Extrato do Caixa");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem1);

        jMenuBar1.add(jMenu1);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 1021, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 677, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jbtdeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtdeleteActionPerformed
        int[] linhasSelecionadas = jTdados.getSelectedRows();

        if (linhasSelecionadas.length > 0) {
            ContabilidadeDAO vendasDao = new ContabilidadeDAO();

            for (int i = 0; i < linhasSelecionadas.length; i++) {
                Contabilidade dadosCard = new Contabilidade();
                dadosCard.setId((int) jTdados.getValueAt(linhasSelecionadas[i], 0));
                vendasDao.deletar(dadosCard);
            }
            readJTable();

        } else {
            JOptionPane.showMessageDialog(this, "Selecione pelo menos um item para excluir.");
        }
    }//GEN-LAST:event_jbtdeleteActionPerformed

    private void jbtPesquisarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtPesquisarActionPerformed
        pesquisarIdRecibo(Integer.parseInt(txtPesquisar.getText()));
    }//GEN-LAST:event_jbtPesquisarActionPerformed

    private void jbtSetFechoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtSetFechoActionPerformed
        if (jTfecho.getSelectedRow() != -1) {
            String nomeCaixa = (String) jTfecho.getValueAt(jTfecho.getSelectedRow(), 2);
            int id = (int) jTfecho.getValueAt(jTfecho.getSelectedRow(), 0);
            double totalVenda = (double) jTfecho.getValueAt(jTfecho.getSelectedRow(), 3);
            FechoDAO fechoDao = new FechoDAO();
            Notas nota = fechoDao.retornarDados(id);

            if (nota != null) {
                FrmUpdateFecho setFecho = new FrmUpdateFecho((Frame) getParent(), true);
                setFecho.setNome(nomeCaixa);
                setFecho.setTotal(totalVenda);
                setFecho.setValor1000(nota.getV1000());
                setFecho.setValor500(nota.getV500());
                setFecho.setValor200(nota.getV200());
                setFecho.setValor100(nota.getV100());
                setFecho.setValor50(nota.getV50());
                setFecho.setValor20(nota.getV20());
                setFecho.setCartoes(nota.getCard());
                setFecho.setMoeda(nota.getMoedas());
                setFecho.setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this, "A alteração do fecho selecionado não é possivel! Apenas para a data atual!", "ERRO", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Por favor, selecione um fecho na tabela com o dia atual!", "Atenção", JOptionPane.WARNING_MESSAGE);
        }
    }//GEN-LAST:event_jbtSetFechoActionPerformed

    private void jbtVerifyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtVerifyActionPerformed
        String nomeuser = txtUser.getText();
        UtilizadorDAO usuarioDao = new UtilizadorDAO();

        Utilizador util = usuarioDao.retornar(nomeuser);
        if (util == null) {
            JOptionPane.showMessageDialog(this, "Username não existe!", "Atenção", JOptionPane.WARNING_MESSAGE);
            txtUser.setText("");
        } else {
            double valorcash = 0;
            double valorcard = 0;
            Caixa c = countDAO.lerValoresVenda(util.getNome());
            if (c != null) {
                for (Caixa caixa : c.getValores()) {
                    valorcash += caixa.getCash();
                    valorcard += caixa.getCard();
                }
            }
            double totalvendido = valorcash + valorcard;
            FechoDAO fechoDAO = new FechoDAO();
            boolean fechoRealizado = fechoDAO.verificarFecho(util.getNome());
            if (fechoRealizado) {
                JOptionPane.showMessageDialog(this, "Fecho deste utilizador já realizado.", "Atenção", JOptionPane.WARNING_MESSAGE);
            } else if (totalvendido != 0) {
                txtNomeCom.setText(util.getNome());

                txtValorTotalVenda.setText(totalvendido + "");
                jbtEmitirFecho.setEnabled(true);
                jbtDetalhesVenda.setEnabled(true);
                txtValorCard.requestFocus();
            } else {
                JOptionPane.showMessageDialog(this, "O utilizador não efetuou nenhuma venda!");
            }
        }
    }//GEN-LAST:event_jbtVerifyActionPerformed

    private void txtMoedasKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtMoedasKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            ActionEvent evtt = null;
            jbtEmitirFechoActionPerformed(evtt);
        }
    }//GEN-LAST:event_txtMoedasKeyPressed

    private void txtValor20ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtValor20ActionPerformed
        txtMoedas.requestFocus();
    }//GEN-LAST:event_txtValor20ActionPerformed

    private void txtValor50ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtValor50ActionPerformed
        txtValor20.requestFocus();
    }//GEN-LAST:event_txtValor50ActionPerformed

    private void txtValor100ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtValor100ActionPerformed
        txtValor50.requestFocus();
    }//GEN-LAST:event_txtValor100ActionPerformed

    private void txtValor200ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtValor200ActionPerformed
        txtValor100.requestFocus();
    }//GEN-LAST:event_txtValor200ActionPerformed

    private void txtValor500ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtValor500ActionPerformed
        txtValor200.requestFocus();
    }//GEN-LAST:event_txtValor500ActionPerformed

    private void txtValor1000ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtValor1000ActionPerformed
        txtValor500.requestFocus();
    }//GEN-LAST:event_txtValor1000ActionPerformed

    private void txtValorCardActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtValorCardActionPerformed
        txtValor1000.requestFocus();
    }//GEN-LAST:event_txtValorCardActionPerformed

    private void txtUserActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtUserActionPerformed
        jbtVerifyActionPerformed(evt);
    }//GEN-LAST:event_txtUserActionPerformed

    private void jbtEmitirFechoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtEmitirFechoActionPerformed
        cartoes = txtValorCard.getText();
        valor1000 = txtValor1000.getText();
        valor500 = txtValor500.getText();
        valor200 = txtValor200.getText();
        valor100 = txtValor100.getText();
        valor50 = txtValor50.getText();
        valor20 = txtValor20.getText();
        moedas = txtMoedas.getText();
        try {
            double cards = parseDoubleOrDefault(cartoes);
            int v1000 = parseIntOrDefault(valor1000) * 1000;
            int v500 = parseIntOrDefault(valor500) * 500;
            int v200 = parseIntOrDefault(valor200) * 200;
            int v100 = parseIntOrDefault(valor100) * 100;
            int v50 = parseIntOrDefault(valor50) * 50;
            int v20 = parseIntOrDefault(valor20) * 20;
            float moeda = (float) parseDoubleOrDefault(moedas);

            total = (cards + v1000 + v500 + v200 + v100 + v50 + v20 + moeda);
            double totalCash = v1000 + v500 + v200 + v100 + v50 + v20 + moeda;
            String nomeCompleto = txtNomeCom.getText();
            double valorTotalVendido = Double.parseDouble(txtValorTotalVenda.getText());

            diferenca = total - valorTotalVendido;
            int resposta;
            if (diferenca < 0) {
                resposta = JOptionPane.showConfirmDialog(this, "Fecho apresenta falta de " + diferenca + "MT. Deseja continuar?", "Confirmação", JOptionPane.YES_NO_OPTION);

                if (resposta == JOptionPane.YES_OPTION) {
                    double valorcash = 0;
                    double valorcard = 0;
                    Caixa c = countDAO.lerValoresVenda(nomeCompleto);
                    if (c != null) {
                        for (Caixa caixa : c.getValores()) {
                            valorcash += caixa.getCash();
                            valorcard += caixa.getCard();
                        }
                    }

                    Notas nota = new Notas();
                    Fecho fecho = new Fecho();
                    FechoDAO fechoDao = new FechoDAO();

                    nota.setCard(cards);
                    nota.setV1000(v1000);
                    nota.setV500(v500);
                    nota.setV200(v200);
                    nota.setV100(v100);
                    nota.setV50(v50);
                    nota.setV20(v20);
                    nota.setMoedas(moeda);

                    fecho.setNome(nomeCompleto);
                    fecho.setValorTotal(valorTotalVendido);
                    fecho.setValorFalta(diferenca);
                    fechoDao.save(fecho, nota);

                    readJTabledois();
                    new GerarPDF().imprimirFecho(nomeCompleto, nota, valorcash, totalCash, valorTotalVendido, total, diferenca, valorcard);

                    //sentToPrinter(somaValorVendido.getValorCartao(), nota);
                    limparCampos();

                } else {
                    //break; // Retornar ao usuário para fazer alterações
                }
            } else if (diferenca > 0) {
                resposta = JOptionPane.showConfirmDialog(this, "Fecho apresenta excesso de " + diferenca + "MT. Deseja continuar?", "Confirmação", JOptionPane.YES_NO_OPTION);
                if (resposta == JOptionPane.YES_OPTION) {
                    double valorcash = 0;
                    double valorcard = 0;
                    Caixa c = countDAO.lerValoresVenda(nomeCompleto);
                    if (c != null) {
                        for (Caixa caixa : c.getValores()) {
                            valorcash += caixa.getCash();
                            valorcard += caixa.getCard();
                        }
                    }

                    Notas nota = new Notas();
                    Fecho fecho = new Fecho();
                    FechoDAO fechoDao = new FechoDAO();

                    nota.setCard(cards);
                    nota.setV1000(v1000);
                    nota.setV500(v500);
                    nota.setV200(v200);
                    nota.setV100(v100);
                    nota.setV50(v50);
                    nota.setV20(v20);
                    nota.setMoedas(moeda);

                    fecho.setNome(nomeCompleto);
                    fecho.setValorTotal(valorTotalVendido);
                    fecho.setValorFalta(diferenca);
                    fechoDao.save(fecho, nota);

                    readJTabledois();
                    new GerarPDF().imprimirFecho(nomeCompleto, nota, valorcash, totalCash, valorTotalVendido, total, diferenca, valorcard);

                    //sentToPrinter(somaValorVendido.getValorCartao(), nota);
                    limparCampos();
                } else {
                    //break; // Retornar ao usuário para fazer alterações
                }

            } else {
                JOptionPane.showMessageDialog(this, "Fecho finalizado com sucesso!" + diferenca + "MT");
                double valorcash = 0;
                double valorcard = 0;
                Caixa c = countDAO.lerValoresVenda(nomeCompleto);
                if (c != null) {
                    for (Caixa caixa : c.getValores()) {
                        valorcash += caixa.getCash();
                        valorcard += caixa.getCard();
                    }
                }

                Notas nota = new Notas();
                Fecho fecho = new Fecho();
                FechoDAO fechoDao = new FechoDAO();

                nota.setCard(cards);
                nota.setV1000(v1000);
                nota.setV500(v500);
                nota.setV200(v200);
                nota.setV100(v100);
                nota.setV50(v50);
                nota.setV20(v20);
                nota.setMoedas(moeda);

                fecho.setNome(nomeCompleto);
                fecho.setValorTotal(valorTotalVendido);
                fecho.setValorFalta(diferenca);
                fechoDao.save(fecho, nota);

                readJTabledois();
                new GerarPDF().imprimirFecho(nomeCompleto, nota, valorcash, totalCash, valorTotalVendido, total, diferenca, valorcard);

                //sentToPrinter(somaValorVendido.getValorCartao(), nota);
                limparCampos();
            }
        } catch (DocumentException | FileNotFoundException ex) {
            Logger.getLogger(FrmContabilista.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(FrmContabilista.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jbtEmitirFechoActionPerformed

    private void txtPesquisarKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtPesquisarKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            ActionEvent evtt = null;
            jbtPesquisarActionPerformed(evtt);
        }
    }//GEN-LAST:event_txtPesquisarKeyPressed

    private void jbtDetalhesVendaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtDetalhesVendaActionPerformed
        String nome = txtNomeCom.getText();
        double valorTo = Double.parseDouble(txtValorTotalVenda.getText());
        FrmExtratoCaixa extrato = new FrmExtratoCaixa((Frame) getParent(), true);
        extrato.nomeFun(nome, valorTo);
        extrato.setOperacao(Operacao.EXTRATOAtual);
        extrato.desbloquearBotoes(true, false);
        extrato.setVisible(true);
    }//GEN-LAST:event_jbtDetalhesVendaActionPerformed

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
        FrmExtratoCaixa extratoCaixa = new FrmExtratoCaixa((Frame) getParent(), true);
        extratoCaixa.setOperacao(Operacao.EXTRATOAntigo);
        extratoCaixa.desbloquearBotoes(false, true);
        extratoCaixa.setVisible(true);
    }//GEN-LAST:event_jMenuItem1ActionPerformed



    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLUserLogado;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTable jTdados;
    private javax.swing.JTable jTfecho;
    private javax.swing.JButton jbtDetalhesVenda;
    private javax.swing.JButton jbtEmitirFecho;
    private javax.swing.JButton jbtPesquisar;
    private javax.swing.JButton jbtSetFecho;
    private javax.swing.JButton jbtVerify;
    private javax.swing.JButton jbtdelete;
    private javax.swing.JTextField txtMoedas;
    private javax.swing.JTextField txtNomeCom;
    private javax.swing.JTextField txtPesquisar;
    private javax.swing.JTextField txtUser;
    private javax.swing.JTextField txtValor100;
    private javax.swing.JTextField txtValor1000;
    private javax.swing.JTextField txtValor20;
    private javax.swing.JTextField txtValor200;
    private javax.swing.JTextField txtValor50;
    private javax.swing.JTextField txtValor500;
    private javax.swing.JTextField txtValorCard;
    private javax.swing.JTextField txtValorTotalVenda;
    // End of variables declaration//GEN-END:variables
}
