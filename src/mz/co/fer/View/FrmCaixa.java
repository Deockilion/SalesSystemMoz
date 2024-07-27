package mz.co.fer.View;

import mz.co.fer.DAO.CarrinhoDeCompras;
import mz.co.fer.DAO.ProdutoDAO;
import mz.co.fer.DAO.UtilizadorDAO;
import mz.co.fer.DTO.Produto;
import mz.co.fer.Relatorios.ImpressoraRecibo;
import mz.co.fer.DTO.AuthService;
import mz.co.fer.DTO.ProdutoDevolvido;
import mz.co.fer.DTO.Utilizador;
import mz.co.fer.Outros.Tables;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import mz.co.fer.DAO.AuthorizedDAO;
import mz.co.fer.DTO.Authorized;
import mz.co.fer.Outros.Server;
import mz.co.fer.enums.Operacao;

/**
 * Form para venda de produtos, devolução, desconto, impressão de recibos e
 * faturas
 *
 * @author Fernando
 */
public class FrmCaixa extends javax.swing.JFrame {

    private String supervisor;
    private String idDoReciboDaTroca;
    private DefaultTableModel model;
    private List<ProdutoDevolvido> produtosDevolvidos;
    private ProdutoDAO produtoDAO = new ProdutoDAO();
    private Produto produto;
    private AuthorizedDAO auDAO = new AuthorizedDAO();
    private Authorized au;

    public FrmCaixa() {
        initComponents();
        jLUserLogado.setText(AuthService.getCurrentUser());
        jLTotal.setText(0.00 + "");
        txtItensCount.setText("");
        txtCodProd.requestFocus();
        //this.setAlwaysOnTop(true);
        this.setExtendedState(FrmCaixa.MAXIMIZED_BOTH);
        this.model = (DefaultTableModel) jTProdutos.getModel();
        //cliente();
        FrmGestaoDeEstoque.verificarPromo();
        this.produtosDevolvidos = new ArrayList<>();
    }

    public void dadosDoReturn(String supervisor, String idDoReciboDatroca) {
        this.supervisor = supervisor;
        this.idDoReciboDaTroca = idDoReciboDatroca;
    }

    public void setValorTotal(double novoValorTotal) {
        jLTotal.setText(novoValorTotal + "");
    }

    private void cliente() {
        try {
            InetAddress inetAddress = InetAddress.getLocalHost();
            String ip = inetAddress.getHostAddress();
            try (Socket socket = new Socket(ip, 7000)) {
                System.out.println("Conectado ao servidor!");
                // Envia o nome do usuário para o servidor
                PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
                writer.println(jLUserLogado.getText());
                // Lê a mensagem do servidor
                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String mensagem = reader.readLine();

                System.out.println("Mensagem do servidor: " + mensagem);

                // Fecha a conexão com o servidor
                socket.close();
            }
        } catch (IOException e) {
            System.out.println("Erro ao conectar ao servidor!" + e.getMessage());
        }
    }

    /**
     * Este método recebe o produto referente a troca/devolução
     *
     * @param produto
     * @param idDoReciboDaTroca
     */
    public void updateTableData(Produto produto, String idDoReciboDaTroca) {
        int rowCount = model.getRowCount();
        boolean produtoExistente = false;

        for (int i = 0; i < rowCount; i++) {
            int codigoNaTabela = (int) model.getValueAt(i, 0);
            if (codigoNaTabela == produto.getCodigo()) {
                produtoExistente = true;
                break;
            }
        }
        if (produtoExistente) {
            JOptionPane.showMessageDialog(this, "Produto já foi adicionado!");
        } else {
            if (produto != null) {
                model.addRow(new Object[]{
                    produto.getCodigo(),
                    produto.getDescricao(),
                    produto.getQtdArmazem(),
                    produto.getPreco(),
                    produto.getSubtotal()
                });
                produtosDevolvidos.add(new ProdutoDevolvido(produto, idDoReciboDaTroca));
                jLTotal.setText(arredondarValor(somaValor()) + "");
                countRow();
            }
        }
    }

    public void descontar(double porcentagemDeReducao) {
        double valorTotal = Double.parseDouble(jLTotal.getText());

        if ((valorTotal >= 5000 && porcentagemDeReducao <= 15) || (valorTotal >= 10000 && porcentagemDeReducao <= 25)) {

            if (jTProdutos.getSelectedRow() != -1) {

                double valor = (double) jTProdutos.getValueAt(jTProdutos.getSelectedRow(), 3);
                double valorReduzido = valor * (1 - (porcentagemDeReducao / 100)); // Convertendo para double
                model.setValueAt(valorReduzido, jTProdutos.getSelectedRow(), 3);
                double recalcular = (double) jTProdutos.getValueAt(jTProdutos.getSelectedRow(), 2) * (double) jTProdutos.getValueAt(jTProdutos.getSelectedRow(), 3);
                model.setValueAt(recalcular, jTProdutos.getSelectedRow(), 4);
               
                jLTotal.setText(arredondarValor(somaValor()) + "");
            } else {
                double valorReduzido = valorTotal * (1 - ((double) porcentagemDeReducao / 100)); // Convertendo para double
                jLTotal.setText(String.valueOf(valorReduzido));
            }

        } else {
            JOptionPane.showMessageDialog(null, "Margem da percentagem não permitida para esse valor!", "Erro", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    /**
     * Este método faz a soma do numero de produtos e quantidades na tabela
     */
    private void countRow() {
        int totalQuantidade = 0;
        int rowCount = model.getRowCount();
        for (int i = 0; i < rowCount; i++) {
            double qtd = (double) model.getValueAt(i, 2);
            if (qtd < 0) {
                double novaQtd = -(qtd);
                totalQuantidade += novaQtd;
            } else {
                totalQuantidade += qtd;
            }
        }
        txtItensCount.setText(rowCount + "-" + totalQuantidade);
    }

    /**
     * Insere um produto na tabela
     */
    private void inserirProdutoNaTabela() {
        try {
            int codigoProduto = Integer.parseInt(txtCodProd.getText());

            // Obtém a quantidade inserida pelo utilizador
            String qtd = txtQtd.getText();
            double quantidade = 0.0;
            if (!qtd.isEmpty()) {
                quantidade = Double.parseDouble(qtd);
            }

            // Verifica se o produto com o mesmo código já existe na tabela
            int rowCount = model.getRowCount();
            boolean produtoExistente = false;
            int rowIndex = -1;

            for (int i = 0; i < rowCount; i++) {
                int codigoNaTabela = (int) model.getValueAt(i, 0);
                if (codigoNaTabela == codigoProduto) {
                    produtoExistente = true;
                    rowIndex = i;
                    break;
                }
            }
            produto = produtoDAO.retornarProduto(codigoProduto);

            if (produtoExistente) {

                double quantidadeExistente = (double) model.getValueAt(rowIndex, 2);
                double novaQuantidade = quantidadeExistente + quantidade;
                if (quantidadeExistente == produto.getQtdLoja()) {
                    JOptionPane.showMessageDialog(this, "A última unidade deste produto já foi adicionado ao carinho de compras!");
                } else if (novaQuantidade > produto.getQtdLoja()) {
                    JOptionPane.showMessageDialog(this, "Estoque insuficiente!");
                } else {
                    model.setValueAt(novaQuantidade, rowIndex, 2);
                    double preco = (double) model.getValueAt(rowIndex, 3);
                    double novoSubtotal = novaQuantidade * preco;
                    model.setValueAt(novoSubtotal, rowIndex, 4);
                }
            } else {
                double subtotal = quantidade * produto.getPrecoVenda();
                model.addRow(new Object[]{
                    produto.getCodigo(),
                    produto.getDescricao(),
                    quantidade,
                    produto.getPrecoVenda(),
                    arredondarValor(subtotal)
                });

                if (produto.getImagem() != null) {
                    jLimage.setIcon(new ImageIcon(produto.getImagem()));
                }
            }
            jLTotal.setText(arredondarValor(somaValor()) + "");
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Código do produto inválido. Insira um número válido", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * limpa a tabela
     *
     * @param table
     */
    public void limparTabela(JTable table) {
        DefaultTableModel modelo = (DefaultTableModel) table.getModel();
        int rowCount = modelo.getRowCount();
        for (int i = rowCount - 1; i >= 0; i--) {
            modelo.removeRow(i);
        }
        jLTotal.setText(0.0 + "");
    }

    public void setarJLabels() {
        jLTotal.setText(0.0 + "");
        txtItensCount.setText(0 + "");
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
        jTProdutos = new javax.swing.JTable();
        txtCodProd = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        txtQtd = new javax.swing.JTextField();
        jPanel2 = new javax.swing.JPanel();
        jbtFinalizar = new javax.swing.JButton();
        jbtLimpar = new javax.swing.JButton();
        jbtRemover = new javax.swing.JButton();
        jbtReprint = new javax.swing.JButton();
        jbtVDeQuote = new javax.swing.JButton();
        jLtrocos = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLUserLogado = new javax.swing.JLabel();
        txtItensCount = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jLTotal = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLimage = new javax.swing.JLabel();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenuReturn = new javax.swing.JMenuItem();
        jMenuDesconto = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        jmenuSalvarConta = new javax.swing.JMenuItem();
        jMenuAbrirConta = new javax.swing.JMenuItem();
        jmenuReprint = new javax.swing.JMenuItem();
        jmenuLogOut = new javax.swing.JMenuItem();
        jmenuSair = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();
        jMenuItem1 = new javax.swing.JMenuItem();
        jMenuItem2 = new javax.swing.JMenuItem();
        jMenuItem3 = new javax.swing.JMenuItem();
        jMenuItem4 = new javax.swing.JMenuItem();
        jMenuItem5 = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Caixa");
        setBackground(new java.awt.Color(51, 51, 51));
        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        setForeground(new java.awt.Color(242, 242, 242));

        jPanel1.setBackground(new java.awt.Color(51, 51, 51));
        jPanel1.setForeground(new java.awt.Color(51, 51, 51));

        jTProdutos.setBackground(new java.awt.Color(51, 51, 51));
        jTProdutos.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jTProdutos.setFont(new java.awt.Font("Times New Roman", 0, 14)); // NOI18N
        jTProdutos.setForeground(new java.awt.Color(255, 255, 255));
        jTProdutos.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Código", "Descrição", "Quantidade", "Preço Unit.", "SubTotal"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTProdutos.getTableHeader().setReorderingAllowed(false);
        jTProdutos.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTProdutosMouseClicked(evt);
            }
        });
        jTProdutos.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTProdutosKeyPressed(evt);
            }
        });
        jScrollPane1.setViewportView(jTProdutos);

        txtCodProd.setToolTipText("Introduza o código do producto");
        txtCodProd.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtCodProdKeyPressed(evt);
            }
        });

        jLabel2.setFont(new java.awt.Font("Times New Roman", 0, 12)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setText("Quantidade:");

        txtQtd.setToolTipText("Introduza a quantidade do produto");
        txtQtd.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtQtdKeyPressed(evt);
            }
        });

        jPanel2.setBackground(new java.awt.Color(51, 51, 51));
        jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel2.setForeground(new java.awt.Color(51, 51, 51));

        jbtFinalizar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/shopping-cart-16.png"))); // NOI18N
        jbtFinalizar.setText("Finalizar");
        jbtFinalizar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtFinalizarActionPerformed(evt);
            }
        });

        jbtLimpar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/trash-16.png"))); // NOI18N
        jbtLimpar.setText("Limpar");
        jbtLimpar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtLimparActionPerformed(evt);
            }
        });

        jbtRemover.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/remove-16.png"))); // NOI18N
        jbtRemover.setText("Remover");
        jbtRemover.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtRemoverActionPerformed(evt);
            }
        });

        jbtReprint.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/print-16.png"))); // NOI18N
        jbtReprint.setText("Reprint");
        jbtReprint.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtReprintActionPerformed(evt);
            }
        });

        jbtVDeQuote.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/printer.png"))); // NOI18N
        jbtVDeQuote.setText("VD/QUOTE");
        jbtVDeQuote.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtVDeQuoteActionPerformed(evt);
            }
        });

        jLtrocos.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLtrocos.setForeground(new java.awt.Color(255, 255, 255));

        jLabel4.setFont(new java.awt.Font("Times New Roman", 1, 14)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(255, 255, 255));
        jLabel4.setText("Operador:");

        jLUserLogado.setFont(new java.awt.Font("Times New Roman", 0, 14)); // NOI18N
        jLUserLogado.setForeground(new java.awt.Color(255, 255, 255));

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(8, 8, 8)
                        .addComponent(jLabel4)
                        .addGap(6, 6, 6)
                        .addComponent(jLUserLogado, javax.swing.GroupLayout.PREFERRED_SIZE, 168, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addComponent(jbtFinalizar)
                        .addGap(15, 15, 15)
                        .addComponent(jbtRemover)
                        .addGap(11, 11, 11)
                        .addComponent(jbtLimpar)))
                .addGap(11, 11, 11)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jbtReprint)
                        .addGap(9, 9, 9)
                        .addComponent(jbtVDeQuote))
                    .addComponent(jLtrocos, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(152, 152, 152))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(8, 8, 8)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jbtFinalizar)
                    .addComponent(jbtRemover)
                    .addComponent(jbtLimpar)
                    .addComponent(jbtReprint)
                    .addComponent(jbtVDeQuote))
                .addGap(26, 26, 26)
                .addComponent(jLtrocos, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(9, 9, 9)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLUserLogado, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        txtItensCount.setFont(new java.awt.Font("Times New Roman", 0, 36)); // NOI18N
        txtItensCount.setForeground(new java.awt.Color(255, 255, 255));
        txtItensCount.setFocusable(false);

        jPanel3.setBackground(new java.awt.Color(255, 255, 0));
        jPanel3.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLTotal.setBackground(new java.awt.Color(0, 0, 0));
        jLTotal.setFont(new java.awt.Font("Times New Roman", 1, 18)); // NOI18N
        jLTotal.setFocusable(false);
        jPanel3.add(jLTotal, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 0, 130, 40));

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setText("TOTAL:");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(25, 25, 25))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(12, 12, 12)
                        .addComponent(jScrollPane1))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(26, 26, 26)
                        .addComponent(txtItensCount, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel3)))
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, 175, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtCodProd, javax.swing.GroupLayout.PREFERRED_SIZE, 175, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel2)
                                .addGap(18, 18, 18)
                                .addComponent(txtQtd, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLimage, javax.swing.GroupLayout.PREFERRED_SIZE, 206, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtItensCount, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(11, 11, 11)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(7, 7, 7)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 303, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(txtCodProd, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(13, 13, 13)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtQtd, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel2))
                        .addGap(27, 27, 27)
                        .addComponent(jLimage, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(32, 32, 32))
        );

        jMenuBar1.setBackground(new java.awt.Color(51, 51, 51));

        jMenu1.setText("File");

        jMenuReturn.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F2, 0));
        jMenuReturn.setText("Return");
        jMenuReturn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuReturnActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuReturn);

        jMenuDesconto.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F3, 0));
        jMenuDesconto.setText("Desconto");
        jMenuDesconto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuDescontoActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuDesconto);
        jMenu1.add(jSeparator1);

        jmenuSalvarConta.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F7, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        jmenuSalvarConta.setText("Salvar Venda");
        jmenuSalvarConta.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmenuSalvarContaActionPerformed(evt);
            }
        });
        jMenu1.add(jmenuSalvarConta);

        jMenuAbrirConta.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F7, java.awt.event.InputEvent.ALT_DOWN_MASK));
        jMenuAbrirConta.setText("Abrir conta");
        jMenuAbrirConta.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuAbrirContaActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuAbrirConta);

        jmenuReprint.setText("Reprint pelo ID");
        jmenuReprint.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmenuReprintActionPerformed(evt);
            }
        });
        jMenu1.add(jmenuReprint);

        jmenuLogOut.setText("Log out");
        jmenuLogOut.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmenuLogOutActionPerformed(evt);
            }
        });
        jMenu1.add(jmenuLogOut);

        jmenuSair.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F4, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        jmenuSair.setText("Sair");
        jmenuSair.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmenuSairActionPerformed(evt);
            }
        });
        jMenu1.add(jmenuSair);

        jMenuBar1.add(jMenu1);

        jMenu2.setText("Comandos");

        jMenuItem1.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F1, 0));
        jMenuItem1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/shopping-cart-16.png"))); // NOI18N
        jMenuItem1.setText("Finalizar Venda");
        jMenuItem1.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem1);

        jMenuItem2.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F5, 0));
        jMenuItem2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/remove-16.png"))); // NOI18N
        jMenuItem2.setText("Remover");
        jMenuItem2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem2ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem2);

        jMenuItem3.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F6, 0));
        jMenuItem3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/trash-16.png"))); // NOI18N
        jMenuItem3.setText("Limpar");
        jMenuItem3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem3ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem3);

        jMenuItem4.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F8, 0));
        jMenuItem4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/print-16.png"))); // NOI18N
        jMenuItem4.setText("Reprint");
        jMenuItem4.setToolTipText("Faz o reprint da última venda");
        jMenuItem4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem4ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem4);

        jMenuItem5.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F9, 0));
        jMenuItem5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/print-16.png"))); // NOI18N
        jMenuItem5.setText("VD/Quote");
        jMenuItem5.setToolTipText("Imprime a VD e Quotação");
        jMenuItem5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem5ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem5);

        jMenuBar1.add(jMenu2);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(0, 0, 0))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jbtFinalizarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtFinalizarActionPerformed
        String mensagem = "Método de Pagamento";

        Object[] botoes = {"Numerário", "Cartão"};
        int opcao = JOptionPane.showOptionDialog(this, mensagem, "Opções de pagamentos", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, botoes, botoes[0]);
        double valor = Double.parseDouble(jLTotal.getText());
        int count = jTProdutos.getRowCount();
        if (opcao == 0) {
            if (count == 0) {
                JOptionPane.showMessageDialog(this, "Nenhum valor definido para a venda.Por favor adicione produtos para vender!", "Atenção", JOptionPane.INFORMATION_MESSAGE);
            } else if (jLUserLogado.getText() == null) {
                JOptionPane.showMessageDialog(this, "Por favor, Faça Login!!", "Atenção", JOptionPane.WARNING_MESSAGE);
            } else {
                PagamentoDinheiro vendaDinheiro = new PagamentoDinheiro(this, true, valor);

                vendaDinheiro.setTabelaProdutos((DefaultTableModel) jTProdutos.getModel());
                vendaDinheiro.dadosDoReturn(supervisor, produtosDevolvidos);
                vendaDinheiro.mostrarNomeOperador(jLUserLogado.getText());
                vendaDinheiro.setVisible(true);
                produtosDevolvidos.clear();
            }
            txtCodProd.requestFocus();
        } else if (opcao == 1) {
            if (count == 0) {
                JOptionPane.showMessageDialog(this, "Nenhum valor definido para a venda.Por favor adicione produtos para vender!", "Atenção", JOptionPane.INFORMATION_MESSAGE);
            } else if (jLUserLogado.getText() == null) {
                JOptionPane.showMessageDialog(this, "Por favor, Faça Login!!", "Atenção", JOptionPane.WARNING_MESSAGE);
            } else {

                PagamentoCartao vendaCartao = new PagamentoCartao(this, true, valor);

                vendaCartao.setTabelaProdutos((DefaultTableModel) jTProdutos.getModel());
                vendaCartao.dadosDoReturn(supervisor, produtosDevolvidos);
                vendaCartao.mostrarNomeOperador(jLUserLogado.getText());
                vendaCartao.setVisible(true);
                produtosDevolvidos.clear();
            }
            txtCodProd.requestFocus();
        }
    }//GEN-LAST:event_jbtFinalizarActionPerformed

    private void txtQtdKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtQtdKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            double quantidade = 1;
            if (txtQtd.getText().equals("")) {
                txtQtd.setText(String.valueOf(quantidade));
            } else {
                try {
                    quantidade = Double.parseDouble(txtQtd.getText());

                } catch (NumberFormatException e) {
                    System.out.println(e.getLocalizedMessage());
                    JOptionPane.showMessageDialog(this, "Código do produto inválido. Insira um número válido", "Erro", JOptionPane.ERROR_MESSAGE);
                    txtCodProd.setText("");
                    txtQtd.setText("");
                }
            }
            try {
                produto = produtoDAO.retornarProduto(Integer.parseInt(txtCodProd.getText()));
                if (produto.getType().equals("UNIDADE") && quantidade != Math.floor(quantidade)) {
                    JOptionPane.showMessageDialog(this, "Este produto é por unidade, insira uma quantidade inteira.");
                    txtCodProd.setText("");
                    txtQtd.setText("");
                    txtCodProd.requestFocus();
                } else if (quantidade < 0) {
                    JOptionPane.showMessageDialog(this, "Não pode digitar numero negativo!", "Atenção", JOptionPane.WARNING_MESSAGE);
                    txtQtd.setText("");
                    txtCodProd.setText("");
                    txtCodProd.requestFocus();

                } else {
                    if (produto != null && quantidade > produto.getQtdLoja()) {
                        JOptionPane.showMessageDialog(this, "Estoque insuficiente!", "Atenção", JOptionPane.WARNING_MESSAGE);
                        txtCodProd.setText("");
                        txtQtd.setText("");
                        txtCodProd.requestFocus();
                    } else {
                        inserirProdutoNaTabela();
                        txtCodProd.setText("");
                        txtQtd.setText("");
                        txtCodProd.requestFocus();
                        countRow();
                    }
                }
            } catch (NumberFormatException e) {
                System.out.println(e.getLocalizedMessage());
                JOptionPane.showMessageDialog(this, "Código do produto inválido. Insira um número válido", "Erro", JOptionPane.ERROR_MESSAGE);
                txtCodProd.setText("");
                txtQtd.setText("");
            }
        } else if (evt.getKeyCode() == KeyEvent.VK_UP) {
            txtCodProd.requestFocus();
        }
    }//GEN-LAST:event_txtQtdKeyPressed

    private void jbtLimparActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtLimparActionPerformed
        new Tables().limparTabela(jTProdutos);
        setarJLabels();
        produtosDevolvidos.clear();
        supervisor = "";
        txtCodProd.requestFocus();
    }//GEN-LAST:event_jbtLimparActionPerformed

    private void jbtRemoverActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtRemoverActionPerformed
        removerLinhaSelecionada(jTProdutos, jLTotal);
        countRow();
        txtCodProd.requestFocus();
    }//GEN-LAST:event_jbtRemoverActionPerformed

    private void jbtVDeQuoteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtVDeQuoteActionPerformed
        String mensagem = "O que deseja imprimir?";

        Object[] botoes = {"VD", "QUOTE"};

        int opcao = JOptionPane.showOptionDialog(this, mensagem, "Opções de impressão", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, botoes, botoes[0]);

        if (opcao == 0) {
            FrmAuthentic auth = new FrmAuthentic(this, true);
            auth.setOperacao(Operacao.VD);
            auth.setVisible(true);
        } else if (opcao == 1) {
            double valor = Double.parseDouble(jLTotal.getText());
            int count = jTProdutos.getRowCount();
            if (count == 0) {
                JOptionPane.showMessageDialog(this, "Nenhum produto na tabela para impressão da cotação!", "Atenção", JOptionPane.INFORMATION_MESSAGE);
            } else if (valor < 0) {
                JOptionPane.showMessageDialog(this, "Valor Total negativo! Não pode imprimir uma cotação com valor negativo", "Atenção", JOptionPane.INFORMATION_MESSAGE);
            } else {
                FrmQuote quote = new FrmQuote(this, true);
                quote.setTabelaProdutos(model);
                quote.setOperacao(Operacao.QUOTE);
                quote.setVisible(true);
            }
        }
    }//GEN-LAST:event_jbtVDeQuoteActionPerformed

    private void txtCodProdKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtCodProdKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            double quantidade = 1;
            if (txtQtd.getText().equals("")) {
                txtQtd.setText(String.valueOf(quantidade));
            } else {
                try {
                    quantidade = Double.parseDouble(txtQtd.getText());
                } catch (NumberFormatException e) {

                    JOptionPane.showMessageDialog(this, "Código do produto inválido. Insira um número válido", "Erro", JOptionPane.ERROR_MESSAGE);
                    txtCodProd.setText("");
                    txtQtd.setText("");
                    txtCodProd.requestFocus();
                }
            }
            try {
                produto = produtoDAO.retornarProduto(Integer.parseInt(txtCodProd.getText()));
                if (produto != null) {
                    if (produto.getType().equals("UNIDADE") && quantidade != Math.floor(quantidade)) {
                        JOptionPane.showMessageDialog(this, "Este produto é por unidade, insira uma quantidade inteira.");
                        txtCodProd.setText("");
                        txtQtd.setText("");
                        txtCodProd.requestFocus();
                    } else if (quantidade > produto.getQtdLoja()) {
                        JOptionPane.showMessageDialog(this, "Estoque insuficiente!", "Atenção", JOptionPane.WARNING_MESSAGE);
                        txtCodProd.setText("");
                        txtQtd.setText("");
                        txtCodProd.requestFocus();

                    } else {
                        inserirProdutoNaTabela();
                        txtCodProd.setText("");
                        txtQtd.setText("");
                        countRow();
                    }
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Código do produto inválido. Insira um número válido!", "Erro", JOptionPane.ERROR_MESSAGE);
                txtCodProd.setText("");
                txtQtd.setText("");
            }

        } else if (evt.getKeyCode() == KeyEvent.VK_DOWN) {
            txtQtd.requestFocus();
        }
    }//GEN-LAST:event_txtCodProdKeyPressed

    private void jTProdutosKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTProdutosKeyPressed
        if (jTProdutos.getSelectedRow() != -1) {
            int codProdSelecionado = (int) jTProdutos.getValueAt(jTProdutos.getSelectedRow(), 0);
            ProdutoDAO prodDao = new ProdutoDAO();
            produto = prodDao.retornarProduto(codProdSelecionado);
            if (produto.getImagem() != null) {
                jLimage.setIcon(new ImageIcon(produto.getImagem()));
            }
        }

    }//GEN-LAST:event_jTProdutosKeyPressed

    private void jbtReprintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtReprintActionPerformed
        String nomeuser = jLUserLogado.getText();
        Utilizador utilizador = UtilizadorDAO.procurarUtilizadorPeloNome(nomeuser);
        if (utilizador.isAcessoAdmin() || utilizador.isAcessoGestao() || utilizador.isAcessoContabilista()) {
            new ImpressoraRecibo().reprintLastSale();
            au = new Authorized(nomeuser, "REPRINT");
            auDAO.save(au);
        } else if (utilizador.isAcessoCaixa()) {
            FrmAuthentic auth = new FrmAuthentic(this, true);
            auth.setOperacao(Operacao.REPRINT);
            auth.setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this, "Acesso não autorizado.", "Atenção", JOptionPane.WARNING_MESSAGE);
        }
        txtCodProd.requestFocus();
    }//GEN-LAST:event_jbtReprintActionPerformed

    private void jMenuItem3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem3ActionPerformed
        jbtLimparActionPerformed(evt);
    }//GEN-LAST:event_jMenuItem3ActionPerformed

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
        jbtFinalizarActionPerformed(evt);
    }//GEN-LAST:event_jMenuItem1ActionPerformed

    private void jMenuItem2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem2ActionPerformed
        jbtRemoverActionPerformed(evt);
    }//GEN-LAST:event_jMenuItem2ActionPerformed

    private void jMenuItem5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem5ActionPerformed
        jbtVDeQuoteActionPerformed(evt);
    }//GEN-LAST:event_jMenuItem5ActionPerformed

    private void jTProdutosMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTProdutosMouseClicked
        if (jTProdutos.getSelectedRow() != -1) {
            int codProdSelecionado = (int) jTProdutos.getValueAt(jTProdutos.getSelectedRow(), 0);
            produto = produtoDAO.retornarProduto(codProdSelecionado);
            if (produto.getImagem() != null) {
                jLimage.setIcon(new ImageIcon(produto.getImagem()));
            }
        }
    }//GEN-LAST:event_jTProdutosMouseClicked

    private void jMenuItem4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem4ActionPerformed
        jbtReprintActionPerformed(evt);
    }//GEN-LAST:event_jMenuItem4ActionPerformed

    private void jmenuSairActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmenuSairActionPerformed
        String nomeuser = jLUserLogado.getText();
        Server.removeUsuarioOnline(nomeuser);
        this.dispose();
    }//GEN-LAST:event_jmenuSairActionPerformed

    private void jmenuLogOutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmenuLogOutActionPerformed
        dispose();
        FrmLogin telaLogin = new FrmLogin();
        telaLogin.setVisible(true);
    }//GEN-LAST:event_jmenuLogOutActionPerformed

    private void jmenuReprintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmenuReprintActionPerformed
        String id = JOptionPane.showInputDialog("Digite o ID do recibo da venda:");
        FrmAuthentic authenticFrm = new FrmAuthentic(this, true);
        authenticFrm.setOperacao(Operacao.REPRINTID);
        authenticFrm.setId(id);
        authenticFrm.setVisible(true);
    }//GEN-LAST:event_jmenuReprintActionPerformed

    private void jMenuAbrirContaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuAbrirContaActionPerformed
        FrmAuthentic authenticFrm = new FrmAuthentic(this, true);
        authenticFrm.setOperacao(Operacao.ABRIRCONTA);
        authenticFrm.setVisible(true);
    }//GEN-LAST:event_jMenuAbrirContaActionPerformed

    private void jmenuSalvarContaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmenuSalvarContaActionPerformed
        double valor = Double.parseDouble(jLTotal.getText());
        if (valor != 0) {
            int rowCount = model.getRowCount();
            int colCount = model.getColumnCount();
            List<Produto> produtos = new ArrayList<>();

            for (int row = 0; row < rowCount; row++) {
                produto = new Produto();
                for (int col = 0; col < colCount; col++) {
                    switch (col) {
                        case 0 ->
                        produto.setCodigo((int) model.getValueAt(row, col));
                        case 1 ->
                        produto.setDescricao((String) model.getValueAt(row, col));
                        case 2 ->
                        produto.setQtdArmazem((double) model.getValueAt(row, col));
                        case 3 ->
                        produto.setPreco((double) model.getValueAt(row, col));
                        case 4 ->
                        produto.setSubtotal((double) model.getValueAt(row, col));
                    }
                }
                
                produtos.add(produto);
            }
            CarrinhoDeCompras ccompras = new CarrinhoDeCompras();
            ccompras.salvarCarrinho(produtos);

            limparTabela(jTProdutos);
            countRow();
            JOptionPane.showMessageDialog(this, "O numero da venda salva é: " + ccompras.getNumeroDoCarrinho());
        }
    }//GEN-LAST:event_jmenuSalvarContaActionPerformed

    private void jMenuDescontoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuDescontoActionPerformed
        String nomeuser = jLUserLogado.getText();
        Utilizador utilizador = UtilizadorDAO.procurarUtilizadorPeloNome(nomeuser);
        if (utilizador.isAcessoAdmin() || utilizador.isAcessoGestao()) {
            au = new Authorized(nomeuser, "DESCONTO");
            auDAO.save(au);
            FrmDesconto desconto = new FrmDesconto(this, true);
            desconto.setVisible(true);
        } else if (utilizador.isAcessoCaixa()) {
            FrmAuthentic authenticFrm = new FrmAuthentic(this, true);
            authenticFrm.setOperacao(Operacao.DESCONTO);
            authenticFrm.setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this, "Acesso não autorizado.", "Atenção", JOptionPane.WARNING_MESSAGE);
        }
    }//GEN-LAST:event_jMenuDescontoActionPerformed

    private void jMenuReturnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuReturnActionPerformed
        String nomeuser = jLUserLogado.getText();
        Utilizador utilizador = UtilizadorDAO.procurarUtilizadorPeloNome(nomeuser);
        if (utilizador.isAcessoAdmin() || utilizador.isAcessoGestao()) {
            au = new Authorized(nomeuser, "TROCA");
            auDAO.save(au);
            FrmReturn vReturn = new FrmReturn(this, true);
            vReturn.setVisible(true);
        } else if (utilizador.isAcessoCaixa()) {
            FrmAuthentic authenticFrm = new FrmAuthentic(this, true);
            authenticFrm.setOperacao(Operacao.TROCA);
            authenticFrm.setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this, "Acesso não autorizado.", "Atenção", JOptionPane.WARNING_MESSAGE);
        }
    }//GEN-LAST:event_jMenuReturnActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Windows".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(FrmCaixa.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> {
            new FrmCaixa().setVisible(true);
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLTotal;
    private javax.swing.JLabel jLUserLogado;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLimage;
    private javax.swing.JLabel jLtrocos;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenuItem jMenuAbrirConta;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuDesconto;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JMenuItem jMenuItem3;
    private javax.swing.JMenuItem jMenuItem4;
    private javax.swing.JMenuItem jMenuItem5;
    private javax.swing.JMenuItem jMenuReturn;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JTable jTProdutos;
    private javax.swing.JButton jbtFinalizar;
    private javax.swing.JButton jbtLimpar;
    private javax.swing.JButton jbtRemover;
    private javax.swing.JButton jbtReprint;
    private javax.swing.JButton jbtVDeQuote;
    private javax.swing.JMenuItem jmenuLogOut;
    private javax.swing.JMenuItem jmenuReprint;
    private javax.swing.JMenuItem jmenuSair;
    private javax.swing.JMenuItem jmenuSalvarConta;
    private javax.swing.JTextField txtCodProd;
    private javax.swing.JLabel txtItensCount;
    private javax.swing.JTextField txtQtd;
    // End of variables declaration//GEN-END:variables
    /**
     * Faz a soma de todos os produtos inseridos na tabela
     *
     * @return
     */
    private double somaValor() {
        double valor, soma = 0;
        int count = jTProdutos.getRowCount();
        for (int i = 0; i < count; i++) {
            valor = Double.parseDouble(String.valueOf(jTProdutos.getValueAt(i, 4)));
            soma += valor;
        }
        return soma;
    }

    public void trocos(double tro) {
        jLtrocos.setText("CHANGE IS " + String.format("%.2f", tro) + "MT");
    }

    /**
     * Remove a linha selecionada
     *
     * @param table
     * @param txtTotal
     */
    private void removerLinhaSelecionada(JTable table, JLabel txtTotal) {
        DefaultTableModel modelo = (DefaultTableModel) table.getModel();
        int selectedRow = table.getSelectedRow();

        if (selectedRow != -1) {
            double quantidade = (double) modelo.getValueAt(selectedRow, 2);
            int codProduto = (int) modelo.getValueAt(selectedRow, 0);
            produto = produtoDAO.retornarProduto(codProduto);
            if (produto.getType().equals("UNIDADE")) {
                if (quantidade > 1) {
                    quantidade--; // 

                    modelo.setValueAt(quantidade, selectedRow, 2);

                    double preco = (double) modelo.getValueAt(selectedRow, 3);

                    double novoSubtotal = quantidade * preco;

                    modelo.setValueAt(novoSubtotal, selectedRow, 4);

                    txtTotal.setText(arredondarValor(somaValor()) + "");
                } else {
                    modelo.removeRow(selectedRow);
                    JOptionPane.showMessageDialog(this, "Produto removido.", "Atenção", JOptionPane.INFORMATION_MESSAGE);
                    txtTotal.setText(arredondarValor(somaValor()) + "");
                }
            } else {
                modelo.removeRow(selectedRow);
                txtTotal.setText(arredondarValor(somaValor()) + "");
            }
        } else {
            JOptionPane.showMessageDialog(this, "Selecione um produto para remover uma unidade!", "Atenção", JOptionPane.WARNING_MESSAGE);
        }
    }

    Object getBtnFinalizar(boolean man) {
        jbtFinalizar.setEnabled(man);
        jMenuItem1.setEnabled(man);
        return true;
    }
    /**
    *Método para arredondar valor
    * @param valor
    */
    private static double arredondarValor(double valor) {
        double parteDecimal = valor - Math.floor(valor);

        // Se a parte decimal for maior que 0.51, arredonda para 0.50
        // Se for menor que 0.49, arredonda para 0.00
        if (parteDecimal > 0.51) {
            return Math.floor(valor) + 0.50;
        } else if (parteDecimal < 0.49) {
            return Math.floor(valor);
        } else {
            return valor;
        }
    }

    public void abrirConta() {
        try {
            int numeroDaFatura = Integer.parseInt(JOptionPane.showInputDialog("Digite o código da venda salva:"));
            CarrinhoDeCompras ccompras = new CarrinhoDeCompras();

            for (Produto prod : ccompras.lerCarrinho(numeroDaFatura)) {
                int codigoProduto = prod.getCodigo();
                double quantidade = prod.getQtdArmazem();

                int rowCount = model.getRowCount();
                boolean produtoExistente = false;
                int rowIndex = -1;

                for (int i = 0; i < rowCount; i++) {
                    int codigoNaTabela = (int) model.getValueAt(i, 0);
                    //double qtdNaTabela = (double) model.getValueAt(i, 2);
                    if (codigoNaTabela == codigoProduto) {
                        produtoExistente = true;
                        rowIndex = i;
                        break;
                    }
                }
                Produto produtoFromDAO = produtoDAO.retornarProduto(codigoProduto);

                if (produtoExistente) {
                    double quantidadeExistente = (double) model.getValueAt(rowIndex, 2);
                    double novaQuantidade = quantidadeExistente + quantidade;
                    if (quantidadeExistente == produtoFromDAO.getQtdLoja()) {
                        JOptionPane.showMessageDialog(this, "A última unidade deste produto já foi adicionada ao carrinho de compras!", "INFO", JOptionPane.INFORMATION_MESSAGE);
                    } else if (novaQuantidade > produtoFromDAO.getQtdLoja()) {

                        if (quantidade == produtoFromDAO.getQtdLoja()) {
                            novaQuantidade -= quantidadeExistente;
                            model.setValueAt(novaQuantidade, rowIndex, 2);
                            double preco = (double) model.getValueAt(rowIndex, 3);
                            double novoSubtotal = novaQuantidade * preco;
                            model.setValueAt(novoSubtotal, rowIndex, 4);
                            jLTotal.setText(arredondarValor(somaValor()) + "");
                            countRow();
                        } else if (quantidade < novaQuantidade) {
                            double diferenca = produtoFromDAO.getQtdLoja() - quantidade;

                            quantidade += diferenca;
                            model.setValueAt(quantidade, rowIndex, 2);
                            double preco = (double) model.getValueAt(rowIndex, 3);
                            double novoSubtotal = quantidade * preco;
                            model.setValueAt(novoSubtotal, rowIndex, 4);
                            jLTotal.setText(arredondarValor(somaValor()) + "");
                            countRow();
                        }
                    } else {
                        model.setValueAt(novaQuantidade, rowIndex, 2);
                        double preco = (double) model.getValueAt(rowIndex, 3);
                        double novoSubtotal = novaQuantidade * preco;
                        model.setValueAt(novoSubtotal, rowIndex, 4);
                        jLTotal.setText(arredondarValor(somaValor()) + "");
                        countRow();
                    }
                } else {
                    if (quantidade > produtoFromDAO.getQtdLoja()) {
                        JOptionPane.showMessageDialog(this, "Estoque insuficiente!");
                    } else {
                        double subtotal = quantidade * prod.getPreco();
                        model.addRow(new Object[]{
                            prod.getCodigo(),
                            prod.getDescricao(),
                            quantidade,
                            prod.getPreco(),
                            subtotal
                        });
                        jLTotal.setText(arredondarValor(somaValor()) + "");
                        countRow();
                    }

                }
            }
            ccompras.deletarCarrinho(numeroDaFatura);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Numero Invalido!" + e);
        }

    }

}
