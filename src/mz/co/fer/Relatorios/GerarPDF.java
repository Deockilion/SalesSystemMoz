package mz.co.fer.Relatorios;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import mz.co.fer.DTO.Notas;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import static com.itextpdf.text.Rectangle.NO_BORDER;
import com.itextpdf.text.pdf.Barcode128;
import com.itextpdf.text.pdf.BarcodeEAN;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import java.awt.Desktop;
import java.awt.Font;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import mz.co.fer.DTO.CustomerVD;

/**
 *
 * @author Deockilion
 */
public class GerarPDF {

    private final Document documento;
    private final Font fonteCabecalho;
    private final Font fonteCorpo;
    PdfWriter writer;
    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
    Date date = new Date();

    public GerarPDF() throws DocumentException {
        this.documento = DocumentFactory.getInstance().newDocument();
        this.fonteCabecalho = new Font("Arial", Font.BOLD, 16);
        this.fonteCorpo = new Font("Arial", Font.PLAIN, 12);
    }

    private void abrirDocumento(String nomeArquivo) throws DocumentException, FileNotFoundException {
        writer = PdfWriter.getInstance(documento, new FileOutputStream(nomeArquivo));
        //PdfWriter.getInstance(documento, new FileOutputStream(nomeArquivo));
        documento.open();
    }

    private void fecharDocumento() {
        if (documento != null && documento.isOpen()) {
            documento.close();
        }
    }

    public void gerarCotacao(DefaultTableModel modelo, CustomerVD customer, int recibo) throws IOException {
        try {
            abrirDocumento("Cotação de Produtos.pdf");

            PdfPTable table2 = new PdfPTable(2);
            table2.setWidthPercentage(100);

            Image imageLogo = Image.getInstance(getClass().getResource("/Images/logotipo.png"));
            imageLogo.scaleAbsolute(100, 50);

            Paragraph cliente = new Paragraph(customer.getEmpresa() + "\n" + customer.getNuit() + "\n" + customer.getEndereco());
            //Paragraph loja = new Paragraph("Endereço: Av. Ahmed Sekou Touré nº 610 Maputo \nNUIT: 700057585 \nCONTACTO: 843450746/877226931 \nEMAIL: deockilion@gmail.com");
            

            PdfPCell logo = new PdfPCell(imageLogo);
            logo.setBorder(NO_BORDER);
            PdfPCell dados = new PdfPCell(cliente);
            
            table2.addCell(logo);
            table2.addCell(dados);           
            this.documento.add(table2);
            
            Paragraph algo = new Paragraph("     ");
            this.documento.add(algo);

            
            Paragraph paragrafo = new Paragraph("Data: " + dateFormat.format(date));
            this.documento.add(paragrafo);

            Paragraph titulo = new Paragraph(new Chunk("Cotação de produtos", FontFactory.getFont(FontFactory.HELVETICA, 12)));
            titulo.setAlignment(Element.ALIGN_CENTER);
            this.documento.add(titulo);

            titulo = new Paragraph("     ");
            this.documento.add(titulo);

            PdfPTable table = new PdfPTable(5);
            table.setWidthPercentage(100);
//            float[] columnWidths = {2f, 9f, 3f, 2f, 2f};
//            table.setTotalWidth(columnWidths);
            PdfPCell cell = new PdfPCell(new Paragraph("Código"));
            PdfPCell cell2 = new PdfPCell(new Paragraph("Descrição"));
            PdfPCell cell3 = new PdfPCell(new Paragraph("Quantidade"));
            PdfPCell cell4 = new PdfPCell(new Paragraph("Preço Unit."));
            PdfPCell cell5 = new PdfPCell(new Paragraph("SubTotal"));

            table.addCell(cell);
            table.addCell(cell2);
            table.addCell(cell3);
            table.addCell(cell4);
            table.addCell(cell5);

            for (int i = 0; i < modelo.getRowCount(); i++) {
                cell = new PdfPCell(new Paragraph(modelo.getValueAt(i, 0) + ""));
                cell2 = new PdfPCell(new Paragraph(modelo.getValueAt(i, 1) + ""));
                cell3 = new PdfPCell(new Paragraph(modelo.getValueAt(i, 2) + ""));
                cell4 = new PdfPCell(new Paragraph(modelo.getValueAt(i, 3) + ""));
                cell5 = new PdfPCell(new Paragraph(modelo.getValueAt(i, 4) + ""));

                table.addCell(cell);
                table.addCell(cell2);
                table.addCell(cell3);
                table.addCell(cell4);
                table.addCell(cell5);

            }
            // Calcular o total
            float total = 0;
            for (int i = 0; i < modelo.getRowCount(); i++) {
                total += Float.parseFloat(modelo.getValueAt(i, 4).toString());
            }
            // Adicionar célula para o total
            PdfPCell totalCell = new PdfPCell(new Paragraph("Total: " + String.format("%.2f", total) + "MT"));
            totalCell.setColspan(5); // Colspan para ocupar todas as colunas da tabela
            table.addCell(totalCell);

            this.documento.add(table);

            Paragraph msg = new Paragraph("Válido enquanto houver Estoque");
            this.documento.add(msg);

            msg = new Paragraph("     ");
            this.documento.add(msg);

            Barcode128 barcode = new Barcode128();
            String barcodeString = String.valueOf(recibo);
            // Definir o valor do código de barras
            barcode.setCode(barcodeString);

            Image imagebarcode = barcode.createImageWithBarcode(writer.getDirectContent(), null, null);

            this.documento.add(imagebarcode);

        } catch (DocumentException | FileNotFoundException e) {
            JOptionPane.showMessageDialog(null, "erro : " + e);
        } finally {
            fecharDocumento();
            String nomeArquivoPDF = "Cotação de Produtos.pdf";
            File arquivoPDF = new File(nomeArquivoPDF);
            Desktop.getDesktop().open(arquivoPDF);
        }
    }

    public void imprimirProdutos(DefaultTableModel modelo) throws FileNotFoundException, IOException {

        try {
            abrirDocumento("Lista de Produtos.pdf");
            Paragraph paragrafo = new Paragraph();
            Paragraph titulo = new Paragraph();
            titulo.setAlignment(Element.ALIGN_CENTER);

            titulo.add(new Chunk("Lista de Produtos", FontFactory.getFont(FontFactory.TIMES_BOLD, 12)));
            this.documento.add(titulo);

            paragrafo.add("Data: " + dateFormat.format(date));
            this.documento.add(paragrafo);

            paragrafo = new Paragraph("     ");
            this.documento.add(paragrafo);

            PdfPTable table = new PdfPTable(5);
            //float[] columnWidths = {3f, 9f, 3f, 3f, 3f};
            table.setWidthPercentage(100);
            PdfPCell cell = new PdfPCell(new Paragraph("Código"));
            PdfPCell cell2 = new PdfPCell(new Paragraph("Descrição"));
            PdfPCell cell3 = new PdfPCell(new Paragraph("Stock da Loja"));
            PdfPCell cell4 = new PdfPCell(new Paragraph("Stock do Armazem"));
            PdfPCell cell5 = new PdfPCell(new Paragraph("Preço Unit."));

            table.addCell(cell);
            table.addCell(cell2);
            table.addCell(cell3);
            table.addCell(cell4);
            table.addCell(cell5);

            for (int i = 0; i < modelo.getRowCount(); i++) {
                cell = new PdfPCell(new Paragraph(modelo.getValueAt(i, 0) + ""));
                cell2 = new PdfPCell(new Paragraph(modelo.getValueAt(i, 1) + ""));
                cell3 = new PdfPCell(new Paragraph(modelo.getValueAt(i, 2) + ""));
                cell4 = new PdfPCell(new Paragraph(modelo.getValueAt(i, 3) + ""));
                cell5 = new PdfPCell(new Paragraph(modelo.getValueAt(i, 4) + ""));

                table.addCell(cell);
                table.addCell(cell2);
                table.addCell(cell3);
                table.addCell(cell4);
                table.addCell(cell5);

            }

            this.documento.add(table);

        } catch (DocumentException e) {
            JOptionPane.showMessageDialog(null, "Document ERROR: " + e);
        } finally {
            fecharDocumento();
            String nomeArquivoPDF = "Lista de Produtos.pdf";
            File arquivoPDF;
            arquivoPDF = new File(nomeArquivoPDF);
            Desktop.getDesktop().open(arquivoPDF);
        }
    }

    public void imprimirFecho(String nome, Notas nota, double totalcash, double cashR, double h, double i, double j, double cards) throws DocumentException, FileNotFoundException, IOException {

        try {
            abrirDocumento("Fecho do dia.pdf");
            Paragraph titulo = new Paragraph();
            titulo.setAlignment(Element.ALIGN_CENTER);
            titulo.add(new Chunk("Fecho do Dia", FontFactory.getFont(FontFactory.TIMES_BOLD, 12)));
            this.documento.add(titulo);


            Paragraph paragrafo = new Paragraph("Data: " + dateFormat.format(date));
            this.documento.add(paragrafo);

            paragrafo = new Paragraph("Funcionário: " + nome);
            documento.add(paragrafo);

            paragrafo = new Paragraph("-------------------------------------");
            this.documento.add(paragrafo);

            paragrafo = new Paragraph("Cartões:........" + cards);
            this.documento.add(paragrafo);

            paragrafo = new Paragraph("Cartões:........" + nota.getCard());
            this.documento.add(paragrafo);

            paragrafo = new Paragraph("-------------------------------------");
            this.documento.add(paragrafo);

            Paragraph nota1000 = new Paragraph();
            nota1000.add("1000................" + nota.getV1000());
            this.documento.add(nota1000);

            Paragraph n500 = new Paragraph("500................" + nota.getV500());
            this.documento.add(n500);

            Paragraph n200 = new Paragraph("200................" + nota.getV200());
            this.documento.add(n200);

            Paragraph n100 = new Paragraph("100................" + nota.getV100());
            this.documento.add(n100);

            Paragraph n50 = new Paragraph("50.................." + nota.getV50());
            this.documento.add(n50);

            Paragraph n20 = new Paragraph("20.................." + nota.getV20());
            this.documento.add(n20);

            Paragraph moeda = new Paragraph("Moedas..................." + nota.getMoedas());
            this.documento.add(moeda);

            Paragraph mo = new Paragraph("---------------------------------------");
            this.documento.add(mo);

            moeda = new Paragraph("TOTAL CASH:" + totalcash);
            this.documento.add(moeda);

            moeda = new Paragraph("TOTAL CASH:" + cashR);
            this.documento.add(moeda);

            mo = new Paragraph("---------------------------------------");
            this.documento.add(mo);

            Paragraph totalVendido = new Paragraph("Total Vendido: " + h);
            this.documento.add(totalVendido);

            Paragraph valorrecebido = new Paragraph("Valor recebido: " + i);
            this.documento.add(valorrecebido);

            Paragraph valorfalta = new Paragraph();
            if (j < 0) {
                valorfalta.add("Falta: " + j);
                this.documento.add(valorfalta);
            } else if (j == 0) {
                Paragraph valorfalt = new Paragraph();
                valorfalt.add("" + j);
                this.documento.add(valorfalt);

            } else {
                Paragraph valorfalt = new Paragraph();
                valorfalt.add("Excesso: " + j);
                this.documento.add(valorfalt);
            }

            Paragraph nada = new Paragraph("---------------------------------------");
            this.documento.add(nada);

            Paragraph assinatura = new Paragraph("ASSINATURA");
            this.documento.add(assinatura);

            paragrafo = new Paragraph("");
            this.documento.add(paragrafo);

            Paragraph linh = new Paragraph("__________________________________________");
            this.documento.add(linh);

        } finally {
            fecharDocumento();
            String nomeArquivoPDF = "Fecho do dia.pdf";
            File arquivoPDF;
            arquivoPDF = new File(nomeArquivoPDF);
            Desktop.getDesktop().open(arquivoPDF);
        }

    }

    public void imprimirVD(Date data, String op, CustomerVD customer, DefaultTableModel modelo, String id) throws DocumentException, FileNotFoundException, IOException {
        try {
            abrirDocumento("VD.pdf");
            Paragraph paragrafo;

            PdfPTable table2 = new PdfPTable(2);
            table2.setWidthPercentage(100);

            Image img = Image.getInstance(getClass().getResource("/Images/logotipo.png"));

            img.scaleAbsolute(100, 50);

            paragrafo = new Paragraph(customer.getEmpresa() + "\n" + customer.getNuit() + "\n" + customer.getEndereco());

            PdfPCell logo = new PdfPCell(img);
            logo.setBorder(NO_BORDER);
            PdfPCell dados = new PdfPCell(paragrafo);

            table2.addCell(logo);
            table2.addCell(dados);
            this.documento.add(table2);

            Paragraph datee = new Paragraph("Data: " + dateFormat.format(data));
            this.documento.add(datee);

            Paragraph operador = new Paragraph("Operador: " + op);
            this.documento.add(operador);

            Paragraph titulo = new Paragraph(new Chunk("Fatura VD", FontFactory.getFont(FontFactory.TIMES_BOLD, 12)));
            titulo.setAlignment(Element.ALIGN_CENTER);
            this.documento.add(titulo);

            Paragraph espaco = new Paragraph("            ");
            this.documento.add(espaco);

            PdfPTable table = new PdfPTable(5);
            //float[] columnWidths = {3f, 9f, 3f, 3f, 3f};
            table.setTotalWidth(100);
            PdfPCell cell = new PdfPCell(new Paragraph("Código"));
            PdfPCell cell2 = new PdfPCell(new Paragraph("Descrição"));
            PdfPCell cell3 = new PdfPCell(new Paragraph("Quantidade"));
            PdfPCell cell4 = new PdfPCell(new Paragraph("Preço Unit."));
            PdfPCell cell5 = new PdfPCell(new Paragraph("SubTotal"));

            table.addCell(cell);
            table.addCell(cell2);
            table.addCell(cell3);
            table.addCell(cell4);
            table.addCell(cell5);

            for (int i = 0; i < modelo.getRowCount(); i++) {
                cell = new PdfPCell(new Paragraph(modelo.getValueAt(i, 0) + ""));
                cell2 = new PdfPCell(new Paragraph(modelo.getValueAt(i, 1) + ""));
                cell3 = new PdfPCell(new Paragraph(modelo.getValueAt(i, 2) + ""));
                cell4 = new PdfPCell(new Paragraph(modelo.getValueAt(i, 3) + ""));
                cell5 = new PdfPCell(new Paragraph(modelo.getValueAt(i, 4) + ""));

                table.addCell(cell);
                table.addCell(cell2);
                table.addCell(cell3);
                table.addCell(cell4);
                table.addCell(cell5);

            }
            // Calcular o total
            float total = 0;
            for (int i = 0; i < modelo.getRowCount(); i++) {
                total += Float.parseFloat(modelo.getValueAt(i, 4).toString());
            }

            // Adicionar célula para o total
            PdfPCell totalCell = new PdfPCell(new Paragraph("Total: " + String.format("%.2f", total) + "MT"));
            totalCell.setColspan(5); // Colspan para ocupar todas as colunas da tabela
            table.addCell(totalCell);

            this.documento.add(table);

            espaco = new Paragraph("             ");
            this.documento.add(espaco);

            espaco = new Paragraph("IVA incluso em todos produtos.");
            this.documento.add(espaco);

            Barcode128 barcode = new Barcode128();

            String barcodeString = id;

            // Definir o valor do código de barras
            barcode.setCode(barcodeString);

            Image imge = barcode.createImageWithBarcode(writer.getDirectContent(), null, null);
            this.documento.add(imge);

            paragrafo = new Paragraph("Obrigado pela preferencia!");
            this.documento.add(paragrafo);

        } catch (DocumentException e) {
            JOptionPane.showMessageDialog(null, "erro : " + e);
        } finally {
            fecharDocumento();
            String nomeArquivoPDF = "VD.pdf";
            File arquivoPDF = new File(nomeArquivoPDF);
            Desktop.getDesktop().open(arquivoPDF);
        }
    }

    public void imprimirListaUsers(DefaultTableModel modelo) throws DocumentException, FileNotFoundException, IOException {

        try {
            abrirDocumento("Lista de Utilizadores.pdf");
            Paragraph paragrafo = new Paragraph();
            Paragraph titulo = new Paragraph();
            titulo.setAlignment(Element.ALIGN_CENTER);

            titulo.add("Lista de Utilizadores");
            this.documento.add(titulo);

            paragrafo.add("Data: " + dateFormat.format(date));
            this.documento.add(paragrafo);

            paragrafo = new Paragraph("     ");
            this.documento.add(paragrafo);

            PdfPTable table = new PdfPTable(7);
            table.setWidthPercentage(100);
            //float[] columnWidths = {2f, 7f, 9f, 7f, 7f, 7f, 7f};
            //table.setTotalWidth(columnWidths);
            PdfPCell cell = new PdfPCell(new Paragraph("ID"));
            PdfPCell cell2 = new PdfPCell(new Paragraph("Username"));
            PdfPCell cell3 = new PdfPCell(new Paragraph("Nome Completo"));
            PdfPCell cell4 = new PdfPCell(new Paragraph("Caixa"));
            PdfPCell cell5 = new PdfPCell(new Paragraph("Estoque"));
            PdfPCell cell6 = new PdfPCell(new Paragraph("Contabilidade"));
            PdfPCell cell7 = new PdfPCell(new Paragraph("Administrador"));

            table.addCell(cell);
            table.addCell(cell2);
            table.addCell(cell3);
            table.addCell(cell4);
            table.addCell(cell5);
            table.addCell(cell6);
            table.addCell(cell7);

            for (int i = 0; i < modelo.getRowCount(); i++) {
                cell = new PdfPCell(new Paragraph(modelo.getValueAt(i, 0) + ""));
                cell2 = new PdfPCell(new Paragraph(modelo.getValueAt(i, 1) + ""));
                cell3 = new PdfPCell(new Paragraph(modelo.getValueAt(i, 2) + ""));
                cell4 = new PdfPCell(new Paragraph(modelo.getValueAt(i, 3) + ""));
                cell5 = new PdfPCell(new Paragraph(modelo.getValueAt(i, 4) + ""));
                cell6 = new PdfPCell(new Paragraph(modelo.getValueAt(i, 5) + ""));
                cell7 = new PdfPCell(new Paragraph(modelo.getValueAt(i, 6) + ""));

                table.addCell(cell);
                table.addCell(cell2);
                table.addCell(cell3);
                table.addCell(cell4);
                table.addCell(cell5);
                table.addCell(cell6);
                table.addCell(cell7);

            }

            this.documento.add(table);

        } catch (DocumentException e) {
            JOptionPane.showMessageDialog(null, "Document ERROR: " + e);
        } finally {
            fecharDocumento();
            String nomeArquivoPDF = "Lista de Utilizadores.pdf";
            File arquivoPDF;
            arquivoPDF = new File(nomeArquivoPDF);
            Desktop.getDesktop().open(arquivoPDF);
        }
    }

    public void imprimirRelatorio(DefaultTableModel modelo) throws DocumentException, FileNotFoundException, IOException {

        try {
            abrirDocumento("Relatorio.pdf");
            Paragraph paragrafo = new Paragraph();
            Paragraph titulo = new Paragraph();
            titulo.setAlignment(Element.ALIGN_CENTER);

            titulo.add("Relatório de Vendas");
            this.documento.add(titulo);

            paragrafo.add("Data: " + dateFormat.format(date));
            this.documento.add(paragrafo);

            paragrafo = new Paragraph("     ");
            this.documento.add(paragrafo);

            PdfPTable table = new PdfPTable(3);
            float[] columnWidths = {5f, 5f, 6f,};
            table.setTotalWidth(columnWidths);
            PdfPCell cell = new PdfPCell(new Paragraph("Meses"));
            PdfPCell cell2 = new PdfPCell(new Paragraph("Quantidade"));
            PdfPCell cell3 = new PdfPCell(new Paragraph("Valor Total Vendido"));

            table.addCell(cell);
            table.addCell(cell2);
            table.addCell(cell3);

            for (int i = 0; i < modelo.getRowCount(); i++) {
                cell = new PdfPCell(new Paragraph(modelo.getValueAt(i, 0) + ""));
                cell2 = new PdfPCell(new Paragraph(modelo.getValueAt(i, 1) + ""));
                cell3 = new PdfPCell(new Paragraph(modelo.getValueAt(i, 2) + ""));

                table.addCell(cell);
                table.addCell(cell2);
                table.addCell(cell3);
            }

            this.documento.add(table);

        } catch (DocumentException e) {
            JOptionPane.showMessageDialog(null, "Document ERROR: " + e);
        } finally {
            fecharDocumento();
            String nomeArquivoPDF = "Relatorio.pdf";
            File arquivoPDF;
            arquivoPDF = new File(nomeArquivoPDF);
            Desktop.getDesktop().open(arquivoPDF);
        }
    }

    public void gerarRequisicao(DefaultTableModel modelo, String fornecedor, String nuit, String type) throws DocumentException, FileNotFoundException, IOException {

        try {

            abrirDocumento("Requisição de Produtos.pdf");
            Paragraph paragrafo;
            Paragraph titulo = new Paragraph();
            titulo.setAlignment(Element.ALIGN_CENTER);
            titulo.add(type);
            this.documento.add(titulo);
            paragrafo = new Paragraph("   ");
            this.documento.add(paragrafo);

            PdfPTable table2 = new PdfPTable(2);
            table2.setWidthPercentage(100);

            // Abrir documento e definir imagem
            Image img = Image.getInstance("C:\\Users\\Deockilion\\Documents\\NetBeansProjects\\Sistema de Vendas com Gestão de estoque\\src\\Images\\logotipo.png");
            img.scaleAbsolute(100, 50);

            paragrafo = new Paragraph();
            paragrafo.add(fornecedor + "\n" + nuit);

            PdfPCell logo = new PdfPCell(img);
            logo.setBorder(NO_BORDER);
            PdfPCell dados = new PdfPCell(paragrafo);

            table2.addCell(logo);
            table2.addCell(dados);
            this.documento.add(table2);

            paragrafo = new Paragraph();
            paragrafo.add("Data: " + dateFormat.format(date));
            this.documento.add(paragrafo);

            paragrafo = new Paragraph("     ");
            this.documento.add(paragrafo);

            PdfPTable table = new PdfPTable(5);
            table.setWidthPercentage(100);
//            float[] columnWidths = {2f, 9f, 3f, 2f, 2f};
//            table.setTotalWidth(columnWidths);
            PdfPCell cell = new PdfPCell(new Paragraph("Código"));
            PdfPCell cell2 = new PdfPCell(new Paragraph("Descrição"));
            PdfPCell cell3 = new PdfPCell(new Paragraph("Quantidade"));
            PdfPCell cell4 = new PdfPCell(new Paragraph("Preço Unit."));
            PdfPCell cell5 = new PdfPCell(new Paragraph("SubTotal"));

            table.addCell(cell);
            table.addCell(cell2);
            table.addCell(cell3);
            table.addCell(cell4);
            table.addCell(cell5);

            for (int i = 0; i < modelo.getRowCount(); i++) {
                cell = new PdfPCell(new Paragraph(modelo.getValueAt(i, 0) + ""));
                cell2 = new PdfPCell(new Paragraph(modelo.getValueAt(i, 1) + ""));
                cell3 = new PdfPCell(new Paragraph(modelo.getValueAt(i, 2) + ""));
                cell4 = new PdfPCell(new Paragraph(modelo.getValueAt(i, 3) + ""));
                cell5 = new PdfPCell(new Paragraph(modelo.getValueAt(i, 4) + ""));

                table.addCell(cell);
                table.addCell(cell2);
                table.addCell(cell3);
                table.addCell(cell4);
                table.addCell(cell5);

            }
            // Calcular o total
            float total = 0;
            for (int i = 0; i < modelo.getRowCount(); i++) {
                total += Float.parseFloat(modelo.getValueAt(i, 4).toString());
            }
            // Adicionar célula para o total
            PdfPCell totalCell = new PdfPCell(new Paragraph("Total: " + String.format("%.2f", total) + "MT"));
            totalCell.setColspan(5); // Colspan para ocupar todas as colunas da tabela
            table.addCell(totalCell);

            this.documento.add(table);

        } catch (DocumentException e) {
            JOptionPane.showMessageDialog(null, "erro : " + e);
        } finally {
            fecharDocumento();
            String nomeArquivoPDF = "Requisição de Produtos.pdf";
            File arquivoPDF;
            arquivoPDF = new File(nomeArquivoPDF);
            Desktop.getDesktop().open(arquivoPDF);
        }
    }

    public void printPrices(DefaultTableModel modelo) throws FileNotFoundException, IOException {
        try {
            abrirDocumento("prices.pdf");
            PdfPTable table = new PdfPTable(2);
            table.setWidthPercentage(100);

            for (int i = 0; i < modelo.getRowCount(); i++) {
                String barcodeString = "60000" + modelo.getValueAt(i, 0);
                String desc = modelo.getValueAt(i, 1).toString();
                double preco = (double) modelo.getValueAt(i, 3);
                int qtd = (int) modelo.getValueAt(i, 2);

                // Criar o código de barras
                BarcodeEAN barcode = new BarcodeEAN();
                barcode.setCodeType(BarcodeEAN.EAN8);
                barcode.setCode(barcodeString);
                Image imagebarcode = barcode.createImageWithBarcode(writer.getDirectContent(), null, null);

                // Criar parágrafo com código de barras, descrição e preço
                Paragraph paragrafo = new Paragraph();
                paragrafo.add(new Chunk(imagebarcode, 0, 0));
                paragrafo.add(new Chunk(" " + desc, FontFactory.getFont(FontFactory.HELVETICA, 12)));
                paragrafo.add(new Chunk(" " + preco + "MT", FontFactory.getFont(FontFactory.TIMES_BOLD, 18)));
                paragrafo.setAlignment(Element.ALIGN_CENTER);

                // Adicionar célula com parágrafo à tabela, repetindo conforme a quantidade
                for (int j = 0; j < qtd; j++) {
                    PdfPCell cell = new PdfPCell(paragrafo);
                    cell.setBackgroundColor(BaseColor.YELLOW);
                    cell.setUseAscender(true);
                    table.addCell(cell);
                }
            }

            documento.add(table);

        } catch (DocumentException e) {
            JOptionPane.showMessageDialog(null, "erro : " + e);
        } finally {
            fecharDocumento();
            String nomeArquivoPDF = "prices.pdf";
            File arquivoPDF;
            arquivoPDF = new File(nomeArquivoPDF);
            Desktop.getDesktop().open(arquivoPDF);
        }
    }

}
