
package com.iqbase.iqbase;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class TelaVenda extends JDialog {

    private JTextField campoCodigoProduto;
    private JButton botaoAdicionarProduto;
    private JTable tabelaItensVenda;
    private DefaultTableModel modeloTabela;
    private JLabel labelTotalVenda;
    private JButton botaoFinalizarVenda;
    private JTextField campoPesquisaProduto;
    private JButton botaoPesquisar;
    private JButton botaoVerTabelaProdutos;
    private JButton botaoRemoverItem;
    private JButton botaoCancelarVenda;

    private ProdutoDAO produtoDAO;
    private List<ItemVenda> itensVenda;
    private double totalVenda;

    public TelaVenda(Frame owner) {
        super(owner, "Nova Venda", true);
        
        produtoDAO = new ProdutoDAO();
        itensVenda = new ArrayList<>();
        totalVenda = 0.0;
        
        configurarUI();
        configurarAcoes();
    }
    
    private void configurarUI() {
        setSize(800, 600);
        setLocationRelativeTo(getOwner());
        setLayout(new BorderLayout(10, 10));

        // -- PAINEL SUPERIOR (Entrada e Pesquisa) --
        JPanel painelSuperior = new JPanel(new BorderLayout());
        
        JPanel painelEntrada = new JPanel(new FlowLayout(FlowLayout.LEFT));
        painelEntrada.setBorder(BorderFactory.createTitledBorder("Adicionar Produto"));
        painelEntrada.add(new JLabel("Código do Produto:"));
        campoCodigoProduto = new JTextField(10);
        painelEntrada.add(campoCodigoProduto);
        botaoAdicionarProduto = new JButton("Adicionar");
        painelEntrada.add(botaoAdicionarProduto);
        
        JPanel painelPesquisa = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        painelPesquisa.setBorder(BorderFactory.createTitledBorder("Pesquisar Produto"));
        painelPesquisa.add(new JLabel("Nome ou Código:"));
        campoPesquisaProduto = new JTextField(15);
        painelPesquisa.add(campoPesquisaProduto);
        botaoPesquisar = new JButton("Pesquisar");
        painelPesquisa.add(botaoPesquisar);
        
        painelSuperior.add(painelEntrada, BorderLayout.WEST);
        painelSuperior.add(painelPesquisa, BorderLayout.EAST);
        add(painelSuperior, BorderLayout.NORTH);

        // -- PAINEL CENTRAL (Extrato da Venda e Opções) --
        JPanel painelCentral = new JPanel(new BorderLayout(10, 0));

        String[] colunas = {"Código", "Nome", "Qtd", "Preço Unit.", "Preço Total"};
        modeloTabela = new DefaultTableModel(colunas, 0);
        tabelaItensVenda = new JTable(modeloTabela);
        JScrollPane scrollPane = new JScrollPane(tabelaItensVenda);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Itens da Venda"));
        painelCentral.add(scrollPane, BorderLayout.CENTER);

        // Painel de Opções
        JPanel painelOpcoes = new JPanel();
        painelOpcoes.setLayout(new BoxLayout(painelOpcoes, BoxLayout.Y_AXIS));
        painelOpcoes.setBorder(BorderFactory.createTitledBorder("Opções"));

        botaoVerTabelaProdutos = new JButton("Ver Produtos");
        botaoRemoverItem = new JButton("Remover Item");
        
        painelOpcoes.add(botaoVerTabelaProdutos);
        painelOpcoes.add(Box.createRigidArea(new Dimension(0, 10)));
        painelOpcoes.add(botaoRemoverItem);
        painelOpcoes.add(Box.createVerticalGlue()); 

        painelCentral.add(painelOpcoes, BorderLayout.EAST);
        add(painelCentral, BorderLayout.CENTER);


        // -- PAINEL INFERIOR (Total e Finalizar) --
        JPanel painelInferior = new JPanel(new BorderLayout());
        painelInferior.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        labelTotalVenda = new JLabel("Total: R$ 0,00");
        labelTotalVenda.setFont(new Font("Arial", Font.BOLD, 20));
        
        JPanel painelBotoesFinais = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        botaoCancelarVenda = new JButton("Cancelar Venda");
        botaoFinalizarVenda = new JButton("Finalizar Venda");
        botaoFinalizarVenda.setFont(new Font("Arial", Font.BOLD, 16));
        painelBotoesFinais.add(botaoCancelarVenda);
        painelBotoesFinais.add(botaoFinalizarVenda);

        painelInferior.add(labelTotalVenda, BorderLayout.WEST);
        painelInferior.add(painelBotoesFinais, BorderLayout.EAST);

        add(painelInferior, BorderLayout.SOUTH);
    }
    
    private void configurarAcoes() {
        ActionListener acaoAdicionar = e -> adicionarProdutoPeloCodigo(campoCodigoProduto.getText());
        botaoAdicionarProduto.addActionListener(acaoAdicionar);
        campoCodigoProduto.addActionListener(acaoAdicionar);

        ActionListener acaoPesquisar = e -> pesquisarProduto();
        botaoPesquisar.addActionListener(acaoPesquisar);
        campoPesquisaProduto.addActionListener(acaoPesquisar);
        
        botaoFinalizarVenda.addActionListener(e -> finalizarVenda());
        
        botaoVerTabelaProdutos.addActionListener(e -> verTabelaProdutos());
        
        botaoRemoverItem.addActionListener(e -> removerItemVenda());
        
        botaoCancelarVenda.addActionListener(e -> cancelarVenda());
    }

    private void adicionarProdutoPeloCodigo(String codigo) {
        if (codigo == null || codigo.trim().isEmpty()) { return; }
        Produto produto = produtoDAO.buscarPorCodigo(codigo.trim());
        if (produto == null) {
            JOptionPane.showMessageDialog(this, "Produto com o código '" + codigo + "' não encontrado.", "Produto Inexistente", JOptionPane.WARNING_MESSAGE);
            return;
        }
        adicionarProdutoNaVenda(produto);
        campoCodigoProduto.setText("");
        campoCodigoProduto.requestFocus();
    }
    
    private void adicionarProdutoNaVenda(Produto produto) {
        // Verifica se o produto já está na lista
        for (ItemVenda item : itensVenda) {
            if (item.getProduto().getCodigo().equals(produto.getCodigo())) {
                // Verifica se há estoque para adicionar mais uma unidade
                if (item.getProduto().getQuantidadeEstoque() > item.getQuantidade()) {
                    item.setQuantidade(item.getQuantidade() + 1);
                    atualizarTabelaEtotal();
                } else {
                    JOptionPane.showMessageDialog(this, "Estoque insuficiente para '" + produto.getNome() + "'.", "Estoque Insuficiente", JOptionPane.WARNING_MESSAGE);
                }
                return;
            }
        }

        // Se não está na lista, adiciona um novo item se houver estoque
        if (produto.getQuantidadeEstoque() > 0) {
            itensVenda.add(new ItemVenda(produto, 1));
            atualizarTabelaEtotal();
        } else {
            JOptionPane.showMessageDialog(this, "Produto '" + produto.getNome() + "' sem estoque.", "Estoque Esgotado", JOptionPane.WARNING_MESSAGE);
        }
    }
    
    private void pesquisarProduto() {
        String termoBusca = campoPesquisaProduto.getText();
        if(termoBusca.trim().isEmpty()) { return; }
        TelaPesquisaProduto telaPesquisa = new TelaPesquisaProduto(this, produtoDAO, termoBusca);
        telaPesquisa.setVisible(true);
        Produto produtoSelecionado = telaPesquisa.getProdutoSelecionado();
        if (produtoSelecionado != null) {
            adicionarProdutoNaVenda(produtoSelecionado);
            campoPesquisaProduto.setText("");
            campoCodigoProduto.requestFocus();
        }
    }
    
    private void atualizarTabelaEtotal() {
        modeloTabela.setRowCount(0);
        totalVenda = 0;
        for (ItemVenda item : itensVenda) {
            Produto p = item.getProduto();
            double precoTotalItem = item.getPrecoTotal();
            modeloTabela.addRow(new Object[]{p.getCodigo(), p.getNome(), item.getQuantidade(), String.format("R$ %.2f", p.getPrecoUnitario()), String.format("R$ %.2f", precoTotalItem)});
            totalVenda += precoTotalItem;
        }
        labelTotalVenda.setText(String.format("Total: R$ %.2f", totalVenda));
    }
    
    private void verTabelaProdutos() {
        TelaTabelaProdutos telaTabela = new TelaTabelaProdutos(this);
        telaTabela.setVisible(true);
    }
    
    private void removerItemVenda() {
        int linhaSelecionada = tabelaItensVenda.getSelectedRow();
        if (linhaSelecionada == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um item para remover.", "Nenhum Item Selecionado", JOptionPane.WARNING_MESSAGE);
            return;
        }
        itensVenda.remove(linhaSelecionada);
        atualizarTabelaEtotal();
    }
    
    private void finalizarVenda() {
        if (itensVenda.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nenhum item na venda para finalizar.", "Venda vazia", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int resposta = JOptionPane.showConfirmDialog(this, String.format("Total da venda: R$ %.2f. Deseja finalizar?", totalVenda), "Confirmar Finalização", JOptionPane.YES_NO_OPTION);
        if (resposta == JOptionPane.YES_OPTION) {
            // Lógica para atualizar o estoque
            for(ItemVenda item : itensVenda) {
                Produto produtoVendido = item.getProduto();
                int quantidadeVendida = item.getQuantidade();
                produtoVendido.setQuantidadeEstoque(produtoVendido.getQuantidadeEstoque() - quantidadeVendida);
            }
            
            // Salva as alterações no arquivo de produtos
            produtoDAO.salvarAlteracoes();
            
            // Registra a venda no log da sessão
            GerenciadorDeSessao.getInstancia().registrarVenda(itensVenda, totalVenda);
            
            JOptionPane.showMessageDialog(this, "Venda finalizada com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        }
    }
    
    private void cancelarVenda() {
        if (!itensVenda.isEmpty()) {
            int resposta = JOptionPane.showConfirmDialog(this, "Tem certeza que deseja cancelar a venda atual? Todos os itens serão removidos.", "Cancelar Venda", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (resposta == JOptionPane.NO_OPTION) {
                return;
            }
        }
        dispose();
    }
}
