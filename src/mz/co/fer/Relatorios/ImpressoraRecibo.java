package mz.co.fer.Relatorios;

import com.itextpdf.text.pdf.Barcode128;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import mz.co.fer.DAO.VendasDAO;
import mz.co.fer.DTO.Produto;
import mz.co.fer.DTO.Recibo;
import mz.co.fer.DTO.Vendas;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.print.DocFlavor;
import javax.print.DocPrintJob;
import javax.print.PrintException;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.SimpleDoc;
import javax.swing.JOptionPane;

/**
 *
 * @author Deockilion
 */
public class ImpressoraRecibo {

    public static void imprimirRecibo(Recibo recibo) throws FileNotFoundException {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            StringBuilder content = new StringBuilder();
            content.append("+---------------------+\n");
            content.append("|   Recibo de Venda   |\n");
            content.append("+---------------------+\n");
            content.append("Operador: ").append(recibo.getOperador()).append("\n");
            content.append("Data: ").append(dateFormat.format(recibo.getDataVenda())).append("\n");
            content.append("+---------------------+\n");
            content.append("| Código | Descrição  | Quantidade | Preço Unit. | SubTotal |\n");
            content.append("+---------------------+\n");

            for (Produto produto : recibo.getProdutosVendidos()) {
                content.append(String.format("| %-6s | %-10s | %-9f | %-14.2f | %-8.2f |\n",
                        produto.getCodigo(), produto.getDescricao(), produto.getQtdArmazem(),
                        produto.getPreco(), produto.getSubtotal()));
            }

            content.append("+---------------------+\n");
            content.append("Valor Total: ").append(String.format("%.2f", recibo.getValorTotal())).append("\n");
            content.append("+---------------------+\n");
            content.append("IVA incluido em todos produtos.\n");
            // Generate the barcode image
            Barcode128 barcode = new Barcode128();
            barcode.setCode(recibo.getIdRecibo());
            java.awt.Image awtImage = barcode.createAwtImage(java.awt.Color.BLACK, java.awt.Color.WHITE);

            // Save the barcode image to a file
            BufferedImage bufferedImage = new BufferedImage(awtImage.getWidth(null), awtImage.getHeight(null), BufferedImage.TYPE_INT_RGB);
            Graphics2D graphics = bufferedImage.createGraphics();
            graphics.drawImage(awtImage, 0, 0, null);
            graphics.dispose();

            String barcodeFilePath = "barcode.png";
            File outputFile = new File(barcodeFilePath);
            ImageIO.write(bufferedImage, "png", outputFile);
            // Convert the barcode image to a Base64 string

            BufferedImage barcodeImage = ImageIO.read(new File(barcodeFilePath));

            ByteArrayOutputStream os = new ByteArrayOutputStream();

            ImageIO.write(barcodeImage, "png", os);

            byte[] imageBytes = os.toByteArray();

            String base64Image = Base64.getEncoder().encodeToString(imageBytes);
            
            
            // Adicionar a imagem do código de barras ao recibo
            content.append("+---------------------+\n");
            content.append("|       Código de Barras      |\n");
            content.append("+---------------------+\n");
            content.append("<img src='data:image/png;base64,").append(base64Image).append("' alt='Código de Barras'>\n");
            content.append("+ID: ").append(recibo.getId()).append("\n");
            content.append("+---------------------+\n");
            content.append("+   Obrigado pela Preferencia  +\n");

            // Enviar para a impressora
            PrintService defaultPrintService = PrintServiceLookup.lookupDefaultPrintService();
            DocPrintJob job = defaultPrintService.createPrintJob();
            SimpleDoc doc = new SimpleDoc(content.toString().getBytes(), DocFlavor.BYTE_ARRAY.AUTOSENSE, null);
            job.print(doc, null);

        } catch (PrintException e) {
            JOptionPane.showMessageDialog(null, "Erro ao tentar imprimir: " + e.getMessage());
        } catch (IOException ex) {
            Logger.getLogger(ImpressoraRecibo.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void imprimirCopiaRecibo(Recibo recibo) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");

            StringBuilder content = new StringBuilder();
            content.append("+---------------------+\n");
            content.append("|   Recibo de Venda   |\n");
            content.append("|******DUPLICATE******|\n");
            content.append("+---------------------+\n");
            content.append("Operador: ").append(recibo.getOperador()).append("\n");
            content.append("Data: ").append(dateFormat.format(recibo.getDataVenda())).append("\n");
            content.append("+---------------------+\n");
            content.append("| Código | Descrição  | Quantidade | Preço Unit. | Subtotal |\n");
            content.append("+---------------------+\n");

            for (Produto produto : recibo.getProdutosVendidos()) {
                content.append(String.format("| %-6s | %-10s | %-9.2f | %-14.2f | %-8.2f |\n",
                        produto.getCodigo(), produto.getDescricao(), produto.getQtdArmazem(),
                        produto.getPreco(), produto.getSubtotal()));
            }

            content.append("+---------------------+\n");
            content.append("Valor Total: ").append(String.format("%.2f", recibo.getValorTotal())).append("\n");
            content.append("+---------------------+\n");
            content.append("IVA incluido em todos produtos.\n");

            //Gerar código de barras usando o iText
            Barcode128 barcode = new Barcode128();
            barcode.setCode(recibo.getIdRecibo());
            java.awt.Image awtImage = barcode.createAwtImage(java.awt.Color.BLACK, java.awt.Color.WHITE);
            // Save the barcode image to a file
            BufferedImage bufferedImage = new BufferedImage(awtImage.getWidth(null), awtImage.getHeight(null), BufferedImage.TYPE_INT_RGB);
            Graphics2D graphics = bufferedImage.createGraphics();
            graphics.drawImage(awtImage, 0, 0, null);
            graphics.dispose();

            String barcodeFilePath = "barcode.png";
            File outputFile = new File(barcodeFilePath);
            ImageIO.write(bufferedImage, "png", outputFile);
            // Convert the barcode image to a Base64 string

            BufferedImage barcodeImage = ImageIO.read(new File(barcodeFilePath));

            ByteArrayOutputStream os = new ByteArrayOutputStream();

            ImageIO.write(barcodeImage, "png", os);

            byte[] imageBytes = os.toByteArray();

            String base64Image = Base64.getEncoder().encodeToString(imageBytes);

            // Adicionar a imagem do código de barras ao recibo
            content.append("+---------------------+\n");
            content.append("|       Código de Barras      |\n");
            content.append("+---------------------+\n");
            content.append("<img src='data:image/png;base64,").append(base64Image).append("' alt='Código de Barras'>\n");
            content.append("+ID: ").append(recibo.getId()).append("\n");
            content.append("+---------------------+\n");
            content.append("+   Obrigado pela Preferencia  +\n");
            //content.append("+ID: ").append(recibo.getId()).append("\n");
            content.append("+   Obrigado pela Preferencia  +\n");

            // Enviar para a impressora
            PrintService defaultPrintService = PrintServiceLookup.lookupDefaultPrintService();
            DocPrintJob job = defaultPrintService.createPrintJob();
            SimpleDoc doc = new SimpleDoc(content.toString().getBytes(), DocFlavor.BYTE_ARRAY.AUTOSENSE, null);
            job.print(doc, null);

        } catch (PrintException e) {
            JOptionPane.showMessageDialog(null, "Erro ao tentar imprimir: " + e.getMessage());
        } catch (IOException ex) {
            Logger.getLogger(ImpressoraRecibo.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void reprintLastSale() {
        VendasDAO vendaDAO = new VendasDAO();
        Vendas ultimaVenda = vendaDAO.obterUltimaVenda();

        if (ultimaVenda != null) {
            // Criar um objeto Recibo com os detalhes da última venda
            Recibo reciboReimpressao = criarRecibo(ultimaVenda);
            reciboReimpressao.getIdRecibo();
            reciboReimpressao.getDataVenda();
            reciboReimpressao.getOperador();
            reciboReimpressao.getValorTotal();
            reciboReimpressao.getProdutosVendidos();

            // Imprimir a copia do recibo
            imprimirCopiaRecibo(reciboReimpressao);
        } else {
        }
    }

    private Recibo criarRecibo(Vendas ultimaVenda) {
        Recibo recibo = new Recibo();
        recibo.setIdRecibo(ultimaVenda.getIdRecibo());
        recibo.setOperador(ultimaVenda.getOperador());
        recibo.setDataVenda(ultimaVenda.getDataVenda());
        recibo.setValorTotal(ultimaVenda.getValorTotal());
        recibo.setProdutosVendidos(ultimaVenda.getProdutosVendidos());

        return recibo;

    }

    public void reprintSaleById(String idRecibo) {
        VendasDAO vendaDAO = new VendasDAO();
        Vendas reprintID = vendaDAO.lerVendaPorIdRecibo(idRecibo);

        if (reprintID != null) {
            // Criar um objeto Recibo com os detalhes da venda do ID
            Recibo reciboReimpressao = criarRecibo(reprintID);
            reciboReimpressao.getIdRecibo();
            reciboReimpressao.getDataVenda();
            reciboReimpressao.getOperador();
            reciboReimpressao.getValorTotal();
            reciboReimpressao.getProdutosVendidos();

            // Imprimir a copia do recibo
            imprimirCopiaRecibo(reciboReimpressao);
        } else {
            JOptionPane.showMessageDialog(null, "ID do recibo não existe!!!", "Atenção", JOptionPane.WARNING_MESSAGE);
        }
    }
   

}
