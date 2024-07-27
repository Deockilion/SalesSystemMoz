package mz.co.fer.Outros;

import mz.co.fer.DTO.Produto;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Deockilion
 */
public class Tables {

    public Tables() {

    }

    public void limparTabela(JTable table) {
        DefaultTableModel modelo = (DefaultTableModel) table.getModel();
        int rowCount = modelo.getRowCount();
        for (int i = rowCount - 1; i >= 0; i--) {
            modelo.removeRow(i);
        }
    }

    public void removerLinhaSelecionada(JTable table) {
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        int selectedRow = table.getSelectedRow();
        if (selectedRow != -1) {
            model.removeRow(selectedRow);
        } else {
            JOptionPane.showMessageDialog(null, "Por favor selecione uma linha!", "Atenção", JOptionPane.INFORMATION_MESSAGE);
        }

    }
    public static List<Produto> getProdutosFromTable(DefaultTableModel model) {
        List<Produto> produtos = new ArrayList<>();

        int rowCount = model.getRowCount();
        int colCount = model.getColumnCount();

        for (int row = 0; row < rowCount; row++) {
            Produto produto = new Produto();
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

        return produtos;
    }

}
