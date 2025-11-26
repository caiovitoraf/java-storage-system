package com.iqbase.iqbase;

import javax.swing.*;
import java.awt.*;

public class TelaInicial extends JFrame {

    public TelaInicial() {
        setTitle("IQBase - Bem-vindo");
        setSize(400, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Centraliza a janela

        JPanel painel = new JPanel(new GridLayout(2, 1, 10, 10));
        painel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));

        JButton botaoLogin = new JButton("Fazer Login");
        botaoLogin.addActionListener(e -> {
            TelaLogin telaLogin = new TelaLogin(this);
            telaLogin.setVisible(true);
            
            // Se o login for bem-sucedido, a tela de login vai esconder a tela inicial
            if (telaLogin.isLoginSucedido()) {
                dispose(); // Fecha a tela inicial
            }
        });
        painel.add(botaoLogin);

        JButton botaoSair = new JButton("Sair");
        botaoSair.addActionListener(e -> System.exit(0));
        painel.add(botaoSair);

        add(painel);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new TelaInicial().setVisible(true));
    }
}
