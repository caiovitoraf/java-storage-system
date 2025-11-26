
package com.iqbase.iqbase;

import javax.swing.*;
import java.awt.*;

public class TelaSobre extends JDialog {

    public TelaSobre(Frame owner) {
        super(owner, "Sobre o Sistema IQBase", true);
        setSize(500, 300);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout(10, 10));

        // Título do Projeto
        JLabel lblTitulo = new JLabel("IQBase", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 30));
        add(lblTitulo, BorderLayout.NORTH);

        // Informações Detalhadas
        JPanel painelInfo = new JPanel();
        painelInfo.setLayout(new GridLayout(4, 1, 5, 5));
        painelInfo.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        painelInfo.add(new JLabel("<html><b>Equipe de Desenvolvimento:</b></html>"));
        painelInfo.add(new JLabel("Desenvolvido por: Caio Vitor de Andrade Freitas - 1230209698"));
        painelInfo.add(new JLabel("Nome do professor orientador: Lázaro Pereira de Oliveira"));
        painelInfo.add(new JLabel("Versão: 1.0 - Ano de criação: 2025.2"));

        add(painelInfo, BorderLayout.CENTER);

        // Botão Fechar
        JButton btnFechar = new JButton("Fechar");
        btnFechar.addActionListener(e -> dispose());
        JPanel painelRodape = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        painelRodape.add(btnFechar);
        add(painelRodape, BorderLayout.SOUTH);
    }
}
