
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
        painel.setLayout(new GridLayout(6, 1, 10, 10)); // 6 linhas, 1 coluna, com espaçamento para 6 botões
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
        
        // Botão "Fechar Caixa"
        JButton botaoFecharCaixa = new JButton("Fechar Caixa");
        botaoFecharCaixa.addActionListener(e -> {
            String arquivoSessao = GerenciadorDeSessao.getInstancia().fecharSessaoAtual();
            if (arquivoSessao != null) {
                JOptionPane.showMessageDialog(TelaMenu.this, 
                    "Caixa fechado com sucesso.\nRelatório da sessão salvo em:\n" + arquivoSessao +
                    "\n\nPara iniciar um novo caixa, apenas inicie uma 'Nova Venda'.", 
                    "Caixa Fechado", 
                    JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(TelaMenu.this, 
                    "Nenhuma sessão de caixa está aberta no momento.", 
                    "Caixa Já Fechado", 
                    JOptionPane.INFORMATION_MESSAGE);
            }
        });
        painel.add(botaoFecharCaixa);

        // Botão "Ver Relatório"
        JButton botaoVerRelatorio = new JButton("Ver Relatório");
        botaoVerRelatorio.addActionListener(e -> {
            TelaRelatorio telaRelatorio = new TelaRelatorio(TelaMenu.this);
            telaRelatorio.setVisible(true);
        });
        painel.add(botaoVerRelatorio);

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
