
package com.iqbase.iqbase;

import javax.swing.*;
import java.awt.*;

public class TelaNovoProduto extends JDialog {

    private JTextField campoNome, campoDescricao, campoPreco, campoEstoque;
    private JButton botaoSalvar, botaoCancelar;
    private Produto novoProduto = null;

    public TelaNovoProduto(Dialog owner) {
        super(owner, "Adicionar Novo Produto", true);
        setSize(400, 250);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout(10, 10));

        // -- PAINEL DE CAMPOS --
        JPanel painelCampos = new JPanel(new GridLayout(4, 2, 5, 5));
        painelCampos.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        campoNome = new JTextField();
        campoDescricao = new JTextField();
        campoPreco = new JTextField();
        campoEstoque = new JTextField();

        painelCampos.add(new JLabel("Nome:"));
        painelCampos.add(campoNome);
        painelCampos.add(new JLabel("Descrição:"));
        painelCampos.add(campoDescricao);
        painelCampos.add(new JLabel("Preço (ex: 9.99):"));
        painelCampos.add(campoPreco);
        painelCampos.add(new JLabel("Estoque Inicial:"));
        painelCampos.add(campoEstoque);

        add(painelCampos, BorderLayout.CENTER);

        // -- PAINEL DE BOTÕES --
        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        botaoSalvar = new JButton("Salvar");
        botaoCancelar = new JButton("Cancelar");
        painelBotoes.add(botaoSalvar);
        painelBotoes.add(botaoCancelar);
        
        add(painelBotoes, BorderLayout.SOUTH);

        configurarAcoes();
    }
    
    private void configurarAcoes() {
        botaoSalvar.addActionListener(e -> salvarProduto());
        botaoCancelar.addActionListener(e -> dispose());
    }
    
    private void salvarProduto() {
        // Validação simples
        if (campoNome.getText().trim().isEmpty() || campoPreco.getText().trim().isEmpty() || 
            campoEstoque.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nome, Preço e Estoque são obrigatórios.", "Erro de Validação", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            String nome = campoNome.getText().trim();
            String desc = campoDescricao.getText().trim();
            double preco = Double.parseDouble(campoPreco.getText().trim().replace(",", "."));
            int estoque = Integer.parseInt(campoEstoque.getText().trim());
            
            // O código será gerado pelo DAO, então passamos uma string vazia ou null
            this.novoProduto = new Produto("", nome, desc, preco, estoque);
            dispose();

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Preço e Estoque devem ser números válidos.", "Erro de Formato", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public Produto getProduto() {
        return this.novoProduto;
    }
}
