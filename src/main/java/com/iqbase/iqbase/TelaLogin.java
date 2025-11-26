package com.iqbase.iqbase;

import javax.swing.*;
import java.awt.*;

public class TelaLogin extends JDialog {
    private JTextField campoUsuario;
    private JPasswordField campoSenha;
    private JButton botaoLogin;
    private JButton botaoCancelar;
    private boolean loginSucedido = false;

    public TelaLogin(Frame owner) {
        super(owner, "Login", true);
        setSize(350, 200);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout(10, 10));

        JPanel painelCampos = new JPanel(new GridLayout(2, 2, 10, 10));
        painelCampos.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));

        painelCampos.add(new JLabel("Usuário:"));
        campoUsuario = new JTextField();
        painelCampos.add(campoUsuario);

        painelCampos.add(new JLabel("Senha:"));
        campoSenha = new JPasswordField();
        painelCampos.add(campoSenha);

        add(painelCampos, BorderLayout.CENTER);

        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        painelBotoes.setBorder(BorderFactory.createEmptyBorder(0, 20, 10, 20));
        
        botaoLogin = new JButton("Login");
        botaoCancelar = new JButton("Cancelar");
        
        painelBotoes.add(botaoLogin);
        painelBotoes.add(botaoCancelar);
        add(painelBotoes, BorderLayout.SOUTH);

        configurarAcoes();
    }

    private void configurarAcoes() {
        botaoLogin.addActionListener(e -> realizarLogin());
        botaoCancelar.addActionListener(e -> dispose());
        // Permite login com a tecla Enter no campo de senha
        campoSenha.addActionListener(e -> realizarLogin());
    }

    private void realizarLogin() {
        String usuario = campoUsuario.getText();
        String senha = new String(campoSenha.getPassword());
        
        FuncionarioDAO funcionarioDAO = new FuncionarioDAO();
        Funcionario funcionario = funcionarioDAO.autenticar(usuario, senha);

        if (funcionario != null) {
            loginSucedido = true;
            JOptionPane.showMessageDialog(this, "Bem-vindo(a), " + funcionario.getNome() + "!", "Login Bem-sucedido", JOptionPane.INFORMATION_MESSAGE);
            
            // Abre a tela de menu principal
            SwingUtilities.invokeLater(() -> new TelaMenu().setVisible(true));
            
            dispose(); // Fecha a tela de login
        } else {
            JOptionPane.showMessageDialog(this, "Usuário ou senha inválidos.", "Erro de Login", JOptionPane.ERROR_MESSAGE);
            campoSenha.setText(""); // Limpa o campo de senha
        }
    }

    public boolean isLoginSucedido() {
        return loginSucedido;
    }
}
