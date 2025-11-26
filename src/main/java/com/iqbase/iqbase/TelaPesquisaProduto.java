
package com.iqbase.iqbase;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class TelaPesquisaProduto extends JDialog {

    private JList<Produto> listaResultados;
    private DefaultListModel<Produto> modeloLista;
    private JButton botaoAdicionar;
    private JButton botaoCancelar;
    private Produto produtoSelecionado;

    public TelaPesquisaProduto(Dialog owner, ProdutoDAO produtoDAO, String termoBusca) {
        super(owner, "Resultados da Pesquisa", true);
        setSize(500, 400);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout(10, 10));

        // -- LISTA DE RESULTADOS --
        modeloLista = new DefaultListModel<>();
        listaResultados = new JList<>(modeloLista);
        listaResultados.setCellRenderer(new ProdutoListCellRenderer());
        JScrollPane scrollPane = new JScrollPane(listaResultados);
        add(scrollPane, BorderLayout.CENTER);

        // -- PAINEL DE BOTÕES --
        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        botaoAdicionar = new JButton("Adicionar à Venda");
        botaoCancelar = new JButton("Cancelar");
        painelBotoes.add(botaoAdicionar);
        painelBotoes.add(botaoCancelar);
        add(painelBotoes, BorderLayout.SOUTH);

        configurarAcoes();
        realizarBusca(produtoDAO, termoBusca);
    }

    private void configurarAcoes() {
        botaoAdicionar.addActionListener(e -> {
            produtoSelecionado = listaResultados.getSelectedValue();
            if (produtoSelecionado != null) {
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Selecione um produto da lista.", "Nenhum Produto Selecionado", JOptionPane.WARNING_MESSAGE);
            }
        });

        botaoCancelar.addActionListener(e -> {
            produtoSelecionado = null;
            dispose();
        });
    }

    private void realizarBusca(ProdutoDAO produtoDAO, String termoBusca) {
        List<Produto> resultados = produtoDAO.buscarPorNome(termoBusca);
        // Se a busca por nome não retornar nada, tenta buscar por código
        if (resultados.isEmpty()) {
            Produto p = produtoDAO.buscarPorCodigo(termoBusca);
            if (p != null) {
                resultados.add(p);
            }
        }

        if (resultados.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nenhum produto encontrado para '" + termoBusca + "'.", "Busca Sem Resultados", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } else {
            for (Produto p : resultados) {
                modeloLista.addElement(p);
            }
        }
    }
    
    public Produto getProdutoSelecionado() {
        return produtoSelecionado;
    }

    // Custom Cell Renderer para mostrar o nome e o preço do produto na lista
    private static class ProdutoListCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (value instanceof Produto) {
                Produto produto = (Produto) value;
                setText(String.format("%s (Cód: %s) - R$ %.2f", produto.getNome(), produto.getCodigo(), produto.getPrecoUnitario()));
            }
            return this;
        }
    }
}
