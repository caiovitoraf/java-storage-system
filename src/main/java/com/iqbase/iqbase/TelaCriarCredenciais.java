package com.iqbase.iqbase;

import javax.swing.*;
import java.awt.*;

public class TelaCriarCredenciais extends JDialog {
    private JTextField campoUsuario;
    private JPasswordField campoSenha;
    private JButton botaoSalvar;
    private JButton botaoCancelar;
    private Funcionario funcionario;

    public TelaCriarCredenciais(Dialog owner, Funcionario funcionario) {
        super(owner, "Criar Credenciais de Acesso", true);
        this.funcionario = funcionario;
        
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
        
        botaoSalvar = new JButton("Salvar");
        botaoCancelar = new JButton("Cancelar");
        
        painelBotoes.add(botaoSalvar);
        painelBotoes.add(botaoCancelar);
        add(painelBotoes, BorderLayout.SOUTH);

        configurarAcoes();
    }

    private void configurarAcoes() {
        botaoSalvar.addActionListener(e -> salvarCredenciais());
        botaoCancelar.addActionListener(e -> {
            // Se cancelar, invalida o funcionário para que não seja salvo incompleto
            this.funcionario = null;
            dispose();
        });
    }

    private void salvarCredenciais() {
        String usuario = campoUsuario.getText();
        String senha = new String(campoSenha.getPassword());

        if (usuario.trim().isEmpty() || senha.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Usuário e Senha não podem estar vazios.", "Erro de Validação", JOptionPane.ERROR_MESSAGE);
            return;
        }

        this.funcionario.setUsuario(usuario);
        this.funcionario.setSenha(senha);
        dispose();
    }

    public Funcionario getFuncionario() {
        return this.funcionario;
    }
}
