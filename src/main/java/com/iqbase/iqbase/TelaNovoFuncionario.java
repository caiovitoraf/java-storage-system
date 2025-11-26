package com.iqbase.iqbase;

import com.iqbase.iqbase.Funcionario;
import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class TelaNovoFuncionario extends JDialog {
    private JTextField campoNome;
    private JTextField campoDataNascimento;
    private JRadioButton radioMasculino;
    private JRadioButton radioFeminino;
    private ButtonGroup grupoSexo;
    private JTextField campoCargo;
    private JButton botaoSalvar;
    private JButton botaoCancelar;

    private Funcionario novoFuncionario;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");


    public TelaNovoFuncionario(Dialog owner) {
        super(owner, "Dados do Novo Funcionário", true);
        setSize(400, 250);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout(10, 10));

        JPanel painelCampos = new JPanel(new GridLayout(4, 2, 10, 10));
        painelCampos.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        painelCampos.add(new JLabel("Nome:"));
        campoNome = new JTextField();
        painelCampos.add(campoNome);

        painelCampos.add(new JLabel("Data Nascimento (dd/MM/yyyy):"));
        campoDataNascimento = new JTextField();
        painelCampos.add(campoDataNascimento);

        painelCampos.add(new JLabel("Sexo:"));
        JPanel painelSexo = new JPanel(new FlowLayout(FlowLayout.LEFT));
        radioMasculino = new JRadioButton("Masculino");
        radioFeminino = new JRadioButton("Feminino");
        grupoSexo = new ButtonGroup();
        grupoSexo.add(radioMasculino);
        grupoSexo.add(radioFeminino);
        painelSexo.add(radioMasculino);
        painelSexo.add(radioFeminino);
        radioMasculino.setSelected(true); // Seleção padrão
        painelCampos.add(painelSexo);

        painelCampos.add(new JLabel("Cargo:"));
        campoCargo = new JTextField();
        painelCampos.add(campoCargo);

        add(painelCampos, BorderLayout.CENTER);

        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        botaoSalvar = new JButton("Próximo"); // Botão agora leva para a criação de credenciais
        botaoCancelar = new JButton("Cancelar");
        painelBotoes.add(botaoSalvar);
        painelBotoes.add(botaoCancelar);
        add(painelBotoes, BorderLayout.SOUTH);

        configurarAcoes();
    }

    private void configurarAcoes() {
        botaoSalvar.addActionListener(e -> salvarDadosIniciais());
        botaoCancelar.addActionListener(e -> {
            novoFuncionario = null; // Garante que nenhum funcionário seja retornado se cancelar
            dispose();
        });
    }

    private void salvarDadosIniciais() {
        try {
            String nome = campoNome.getText();
            if (nome.trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "O nome não pode estar vazio.", "Erro de Validação", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            LocalDate dataNascimento = LocalDate.parse(campoDataNascimento.getText(), DATE_FORMATTER);
            
            String sexo;
            if (radioMasculino.isSelected()) {
                sexo = "Masculino";
            } else if (radioFeminino.isSelected()) {
                sexo = "Feminino";
            } else {
                // Caso nenhum esteja selecionado ou outro erro (não deveria acontecer com ButtonGroup)
                JOptionPane.showMessageDialog(this, "Selecione o sexo do funcionário.", "Erro de Validação", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            String cargo = campoCargo.getText();

            // Cria o funcionário com credenciais nulas por enquanto
            novoFuncionario = new Funcionario(0, nome, dataNascimento, sexo, cargo, null, null);
            
            dispose(); // Fecha esta janela para abrir a próxima

        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this, "Formato de data inválido. Use dd/MM/yyyy.", "Erro de Formato", JOptionPane.ERROR_MESSAGE);
        }
    }

    public Funcionario getFuncionario() {
        return novoFuncionario;
    }
}

