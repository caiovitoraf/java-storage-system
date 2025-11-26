
package com.iqbase.iqbase;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 *
 * @author caiov-fedora
 */
public class TelaMenu extends JFrame {

    public TelaMenu() {
        // Configurações da janela principal
        setTitle("Menu Principal - IQBase");
        setSize(400, 500); // Aumenta o tamanho para acomodar mais botões
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Centraliza a janela

        // Painel para os botões
        JPanel painel = new JPanel();
        painel.setLayout(new GridLayout(5, 1, 10, 10)); // 5 linhas, 1 coluna, com espaçamento para 5 botões
        painel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50)); // Margem

        // Botão "Nova Venda"
        JButton botaoNovaVenda = new JButton("Nova Venda");
        botaoNovaVenda.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                TelaVenda telaVenda = new TelaVenda(TelaMenu.this);
                telaVenda.setVisible(true);
            }
        });
        painel.add(botaoNovaVenda);

        // Botão "Ver Estoque"
        JButton botaoVerEstoque = new JButton("Ver Estoque");
        botaoVerEstoque.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                TelaEstoque telaEstoque = new TelaEstoque(TelaMenu.this);
                telaEstoque.setVisible(true);
            }
        });
        painel.add(botaoVerEstoque);

        // Botão "Gerenciar Funcionários"
        JButton botaoGerenciarFuncionarios = new JButton("Gerenciar Funcionários");
        botaoGerenciarFuncionarios.addActionListener(e -> {
            TelaGerenciarFuncionarios telaFuncionarios = new TelaGerenciarFuncionarios(TelaMenu.this);
            telaFuncionarios.setVisible(true);
        });
        painel.add(botaoGerenciarFuncionarios);

        // Botão "Sobre o Sistema"
        JButton botaoSobreSistema = new JButton("Sobre o Sistema");
        botaoSobreSistema.addActionListener(e -> {
            TelaSobre telaSobre = new TelaSobre(TelaMenu.this);
            telaSobre.setVisible(true);
        });
        painel.add(botaoSobreSistema);

        // Botão "Sair"
        JButton botaoSair = new JButton("Sair");
        botaoSair.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(TelaMenu.this, "Tem certeza que deseja sair do sistema?", "Confirmar Saída", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                System.exit(0);
            }
        });
        painel.add(botaoSair);

        // Adiciona o painel à janela
        add(painel);
    }

    public static void main(String[] args) {
        // Garante que a UI seja atualizada na Event Dispatch Thread
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new TelaMenu().setVisible(true);
            }
        });
    }
}
