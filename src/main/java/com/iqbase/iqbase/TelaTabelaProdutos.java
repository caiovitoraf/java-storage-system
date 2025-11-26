
package com.iqbase.iqbase;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class TelaTabelaProdutos extends JDialog {

    private JTable tabelaProdutos;
    private DefaultTableModel modeloTabela;
    private ProdutoDAO produtoDAO;

    public TelaTabelaProdutos(Dialog owner) {
        super(owner, "Tabela de Produtos", true);
        setSize(600, 400);
        setLocationRelativeTo(owner);
        
        produtoDAO = new ProdutoDAO();

        // -- Tabela de Produtos --
        String[] colunas = {"Código", "Nome", "Descrição", "Preço Unit."};
        modeloTabela = new DefaultTableModel(colunas, 0);
        tabelaProdutos = new JTable(modeloTabela);
        tabelaProdutos.setEnabled(false); // Tabela somente leitura

        JScrollPane scrollPane = new JScrollPane(tabelaProdutos);
        add(scrollPane, BorderLayout.CENTER);

        carregarProdutos();
    }

    private void carregarProdutos() {
        List<Produto> todosOsProdutos = produtoDAO.getTodosOsProdutos();
        for (Produto p : todosOsProdutos) {
            modeloTabela.addRow(new Object[]{
                p.getCodigo(),
                p.getNome(),
                p.getDescricao(),
                String.format("R$ %.2f", p.getPrecoUnitario())
            });
        }
    }
}
