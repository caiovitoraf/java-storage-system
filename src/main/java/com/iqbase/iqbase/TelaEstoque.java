
package com.iqbase.iqbase;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

public class TelaEstoque extends JDialog {

    private JTable tabelaEstoque;
    private DefaultTableModel modeloTabela;
    private JButton botaoAdicionar;
    private JButton botaoExcluir;
    private JButton botaoSalvar;
    private JButton botaoFechar;
    private ProdutoDAO produtoDAO;
    private boolean dadosAlterados = false;

    public TelaEstoque(Frame owner) {
        super(owner, "Controle de Estoque", true);
        setSize(800, 600);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout(10, 10));

        produtoDAO = new ProdutoDAO();

        // -- PAINEL CENTRAL (Tabela) --
        String[] colunas = {"Código", "Nome", "Descrição", "Preço Unit.", "Estoque"};
        
        // Faz com que a coluna de código não seja editável
        modeloTabela = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column != 0; // Apenas a coluna 0 (Código) não é editável
            }
        };
        
        tabelaEstoque = new JTable(modeloTabela);
        
        modeloTabela.addTableModelListener(new TableModelListener() {
            @Override
            public void tableChanged(TableModelEvent e) {
                if (e.getType() == TableModelEvent.UPDATE) {
                    dadosAlterados = true;
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(tabelaEstoque);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Produtos em Estoque"));
        add(scrollPane, BorderLayout.CENTER);

        // -- PAINEL INFERIOR (Botões) --
        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        botaoAdicionar = new JButton("Adicionar Novo Produto");
        botaoExcluir = new JButton("Excluir Selecionado");
        botaoSalvar = new JButton("Salvar Alterações");
        botaoFechar = new JButton("Fechar");
        
        painelBotoes.add(botaoAdicionar);
        painelBotoes.add(botaoExcluir);
        painelBotoes.add(botaoSalvar);
        painelBotoes.add(new JSeparator(SwingConstants.VERTICAL));
        painelBotoes.add(botaoFechar);
        add(painelBotoes, BorderLayout.SOUTH);

        carregarDadosNaTabela();
        configurarAcoes();
    }
    
    private void configurarAcoes() {
        botaoAdicionar.addActionListener(e -> adicionarProduto());
        botaoExcluir.addActionListener(e -> excluirProduto());
        botaoSalvar.addActionListener(e -> salvarAlteracoes());
        botaoFechar.addActionListener(e -> fecharJanela());
        
        this.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                fecharJanela();
            }
        });
    }

    private void carregarDadosNaTabela() {
        modeloTabela.setRowCount(0); 
        List<Produto> produtos = produtoDAO.getTodosOsProdutos();
        for (Produto p : produtos) {
            modeloTabela.addRow(new Object[]{
                p.getCodigo(),
                p.getNome(),
                p.getDescricao(),
                String.format("%.2f", p.getPrecoUnitario()),
                p.getQuantidadeEstoque()
            });
        }
        dadosAlterados = false;
    }
    
    private void adicionarProduto() {
        TelaNovoProduto telaNovo = new TelaNovoProduto(this);
        telaNovo.setVisible(true);
        
        Produto novoProduto = telaNovo.getProduto();
        if (novoProduto != null) {
            produtoDAO.adicionarProduto(novoProduto);
            carregarDadosNaTabela(); // Recarrega a tabela para mostrar o novo código
            dadosAlterados = true;
        }
    }
    
    private void excluirProduto() {
        int linhaSelecionada = tabelaEstoque.getSelectedRow();
        if (linhaSelecionada == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um produto para excluir.", "Nenhum Produto Selecionado", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this, "Tem certeza que deseja excluir o produto selecionado?\nIsso reordenará os códigos dos produtos seguintes.", "Confirmar Exclusão", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            produtoDAO.removerProduto(linhaSelecionada);
            carregarDadosNaTabela(); // Recarrega a tabela para mostrar os códigos reordenados
            dadosAlterados = true;
        }
    }
    
    private void salvarAlteracoes() {
        if (tabelaEstoque.isEditing()) {
            tabelaEstoque.getCellEditor().stopCellEditing();
        }

        for (int i = 0; i < modeloTabela.getRowCount(); i++) {
            Produto p = produtoDAO.getProduto(i);
            if(p != null) {
                // O código não é atualizado a partir da tabela
                p.setNome((String) modeloTabela.getValueAt(i, 1));
                p.setDescricao((String) modeloTabela.getValueAt(i, 2));
                try {
                    p.setPrecoUnitario(Double.parseDouble(modeloTabela.getValueAt(i, 3).toString().replace(",", ".")));
                    p.setQuantidadeEstoque(Integer.parseInt(modeloTabela.getValueAt(i, 4).toString()));
                } catch(NumberFormatException e) {
                    JOptionPane.showMessageDialog(this, "Erro de formato na linha " + (i+1) + ".\nPreço e Estoque devem ser números.", "Erro de Dados", JOptionPane.ERROR_MESSAGE);
                    return; // Interrompe o salvamento
                }
            }
        }
        
        produtoDAO.salvarAlteracoes();
        dadosAlterados = false;
        JOptionPane.showMessageDialog(this, "Alterações salvas com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void fecharJanela() {
        if (dadosAlterados) {
            int resposta = JOptionPane.showConfirmDialog(this, "Você possui alterações não salvas. Deseja salvar antes de sair?", "Alterações Pendentes", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
            if (resposta == JOptionPane.YES_OPTION) {
                salvarAlteracoes();
                dispose();
            } else if (resposta == JOptionPane.NO_OPTION) {
                dispose();
            }
        } else {
            dispose();
        }
    }
}
