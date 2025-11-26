package com.iqbase.iqbase;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Calendar;

public class TelaRelatorio extends JDialog {

    private JComboBox<String> comboTipoRelatorio;
    private JSpinner spinnerDia, spinnerMes, spinnerAno;
    private JButton botaoGerarRelatorio;
    private JTextArea areaRelatorio;
    private AnalisadorDeSessao analisador;

    public TelaRelatorio(Frame owner) {
        super(owner, "Relatórios de Vendas", true);
        setSize(700, 500);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout(10, 10));

        this.analisador = new AnalisadorDeSessao();

        // -- PAINEL DE CONTROLES --
        JPanel painelControles = new JPanel(new FlowLayout(FlowLayout.LEFT));
        painelControles.setBorder(BorderFactory.createTitledBorder("Gerar Relatório"));

        comboTipoRelatorio = new JComboBox<>(new String[]{"Diário", "Mensal"});
        
        Calendar cal = Calendar.getInstance();
        SpinnerModel modelDia = new SpinnerNumberModel(cal.get(Calendar.DAY_OF_MONTH), 1, 31, 1);
        SpinnerModel modelMes = new SpinnerNumberModel(cal.get(Calendar.MONTH) + 1, 1, 12, 1);
        SpinnerModel modelAno = new SpinnerNumberModel(cal.get(Calendar.YEAR), 2020, 2099, 1);

        spinnerDia = new JSpinner(modelDia);
        spinnerMes = new JSpinner(modelMes);
        spinnerAno = new JSpinner(modelAno);
        
        // Remove o editor para não permitir digitação
        ((JSpinner.DefaultEditor) spinnerAno.getEditor()).getTextField().setEditable(false);

        botaoGerarRelatorio = new JButton("Gerar Relatório");
        
        painelControles.add(new JLabel("Tipo:"));
        painelControles.add(comboTipoRelatorio);
        painelControles.add(new JLabel("Data:"));
        painelControles.add(spinnerDia);
        painelControles.add(spinnerMes);
        painelControles.add(spinnerAno);
        painelControles.add(botaoGerarRelatorio);
        
        add(painelControles, BorderLayout.NORTH);

        // -- ÁREA DE TEXTO DO RELATÓRIO --
        areaRelatorio = new JTextArea("Selecione o tipo de relatório e a data, depois clique em 'Gerar Relatório'.");
        areaRelatorio.setEditable(false);
        areaRelatorio.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(areaRelatorio);
        add(scrollPane, BorderLayout.CENTER);

        configurarAcoes();
    }
    
    private void configurarAcoes() {
        // Mostra ou esconde o seletor de dia baseado no tipo de relatório
        comboTipoRelatorio.addActionListener(e -> {
            String selecionado = (String) comboTipoRelatorio.getSelectedItem();
            spinnerDia.setVisible("Diário".equals(selecionado));
        });
        
        botaoGerarRelatorio.addActionListener(e -> gerarRelatorio());
    }

    private void gerarRelatorio() {
        String tipo = (String) comboTipoRelatorio.getSelectedItem();
        int ano = (Integer) spinnerAno.getValue();
        int mes = (Integer) spinnerMes.getValue();
        
        String relatorioGerado = "";
        
        try {
            if ("Diário".equals(tipo)) {
                int dia = (Integer) spinnerDia.getValue();
                LocalDate dataSelecionada = LocalDate.of(ano, mes, dia);
                relatorioGerado = analisador.gerarRelatorioDiario(dataSelecionada);
            } else { // Mensal
                YearMonth mesSelecionado = YearMonth.of(ano, mes);
                relatorioGerado = analisador.gerarRelatorioMensal(mesSelecionado);
            }
            areaRelatorio.setText(relatorioGerado);
        } catch (Exception e) {
            areaRelatorio.setText("Erro ao gerar relatório: Data inválida para o mês selecionado.\n" + e.getMessage());
        }
        areaRelatorio.setCaretPosition(0); // Rola para o topo
    }
}
