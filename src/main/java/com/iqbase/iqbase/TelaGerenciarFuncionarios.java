package com.iqbase.iqbase;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

public class TelaGerenciarFuncionarios extends JDialog {

    private JTable tabelaFuncionarios;
    private DefaultTableModel modeloTabela;
    private JButton botaoAdicionar;
    private JButton botaoExcluir;
    private JButton botaoSalvar;
    private JButton botaoFechar;
    private FuncionarioDAO funcionarioDAO;
    private boolean dadosAlterados = false;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public TelaGerenciarFuncionarios(Frame owner) {
        super(owner, "Gerenciar Funcionários", true);
        setSize(1000, 600);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout(10, 10));

        funcionarioDAO = new FuncionarioDAO();

        // -- PAINEL CENTRAL (Tabela) --
        String[] colunas = {"Código", "Nome", "Data Nascimento", "Sexo", "Cargo", "Usuário", "Senha"};
        
        modeloTabela = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column != 0; // Apenas a coluna 0 (Código) não é editável
            }
        };
        
        tabelaFuncionarios = new JTable(modeloTabela);
        
        modeloTabela.addTableModelListener(new TableModelListener() {
            @Override
            public void tableChanged(TableModelEvent e) {
                if (e.getType() == TableModelEvent.UPDATE) {
                    dadosAlterados = true;
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(tabelaFuncionarios);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Funcionários Cadastrados"));
        add(scrollPane, BorderLayout.CENTER);

        // -- PAINEL INFERIOR (Botões) --
        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        botaoAdicionar = new JButton("Adicionar Novo Funcionário");
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
        botaoAdicionar.addActionListener(e -> adicionarFuncionario());
        botaoExcluir.addActionListener(e -> excluirFuncionario());
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
        List<Funcionario> funcionarios = funcionarioDAO.getTodosOsFuncionarios();
        for (Funcionario f : funcionarios) {
            modeloTabela.addRow(new Object[]{
                f.getCodigo(),
                f.getNome(),
                f.getDataNascimento().format(DATE_FORMATTER),
                f.getSexo(),
                f.getCargo(),
                f.getUsuario(),
                f.getSenha()
            });
        }
        dadosAlterados = false;
    }
    
    private void adicionarFuncionario() {
        // Passo 1: Coletar dados iniciais
        TelaNovoFuncionario telaNovo = new TelaNovoFuncionario(this);
        telaNovo.setVisible(true);
        
        Funcionario funcionarioParcial = telaNovo.getFuncionario();
        
        // Se o primeiro passo foi bem-sucedido e não cancelado
        if (funcionarioParcial != null) {
            // Passo 2: Criar credenciais
            TelaCriarCredenciais telaCredenciais = new TelaCriarCredenciais(this, funcionarioParcial);
            telaCredenciais.setVisible(true);
            
            Funcionario funcionarioCompleto = telaCredenciais.getFuncionario();

            // Se o segundo passo foi bem-sucedido e não cancelado
            if (funcionarioCompleto != null) {
                funcionarioDAO.adicionarFuncionario(funcionarioCompleto);
                carregarDadosNaTabela();
                dadosAlterados = true;
            }
        }
    }
    
    private void excluirFuncionario() {
        int linhaSelecionada = tabelaFuncionarios.getSelectedRow();
        if (linhaSelecionada == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um funcionário para excluir.", "Nenhum Funcionário Selecionado", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this, "Tem certeza que deseja excluir o funcionário selecionado?", "Confirmar Exclusão", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            Funcionario f = funcionarioDAO.getTodosOsFuncionarios().get(linhaSelecionada);
            funcionarioDAO.removerFuncionario(f);
            carregarDadosNaTabela(); 
            dadosAlterados = true;
        }
    }
    
    private void salvarAlteracoes() {
        if (tabelaFuncionarios.isEditing()) {
            tabelaFuncionarios.getCellEditor().stopCellEditing();
        }

        List<Funcionario> funcionarios = funcionarioDAO.getTodosOsFuncionarios();
        for (int i = 0; i < modeloTabela.getRowCount(); i++) {
            Funcionario f = funcionarios.get(i);
            if(f != null) {
                f.setNome((String) modeloTabela.getValueAt(i, 1));
                try {
                    f.setDataNascimento(LocalDate.parse(modeloTabela.getValueAt(i, 2).toString(), DATE_FORMATTER));
                    f.setSexo((String) modeloTabela.getValueAt(i, 3));
                    f.setCargo((String) modeloTabela.getValueAt(i, 4));
                    f.setUsuario((String) modeloTabela.getValueAt(i, 5));
                    f.setSenha((String) modeloTabela.getValueAt(i, 6));
                } catch(java.time.format.DateTimeParseException e) {
                    JOptionPane.showMessageDialog(this, "Erro de formato de data na linha " + (i+1) + ".\nUse o formato dd/MM/yyyy.", "Erro de Dados", JOptionPane.ERROR_MESSAGE);
                    return; // Interrompe o salvamento
                }
            }
        }
        
        funcionarioDAO.salvarAlteracoes();
        dadosAlterados = false;
        JOptionPane.showMessageDialog(this, "Alterações salvas com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
        carregarDadosNaTabela();
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
