package com.iqbase.iqbase;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

public class AnalisadorDeSessao {

    private static final String PASTA_RAIZ_SESSOES = "data/sessoes/";
    private static final DateTimeFormatter FORMATO_LOG = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public List<VendaRegistrada> carregarTodasAsVendas() {
        List<VendaRegistrada> todasAsVendas = new ArrayList<>();
        List<File> todosOsArquivos = new ArrayList<>();
        encontrarArquivosDeLog(new File(PASTA_RAIZ_SESSOES), todosOsArquivos);

        for (File arquivo : todosOsArquivos) {
            try (BufferedReader reader = new BufferedReader(new FileReader(arquivo))) {
                VendaRegistrada vendaAtual = null;
                List<ItemVendido> itensAtuais = new ArrayList<>();

                for (String linha : reader.lines().collect(Collectors.toList())) {
                    String[] partes = linha.split(";", -1); // -1 para incluir strings vazias
                    if (partes.length < 1) continue;

                    String tipo = partes[0];
                    try {
                        if ("VENDA".equals(tipo)) {
                            // Finaliza a venda anterior antes de começar uma nova
                            if (vendaAtual != null) {
                                todasAsVendas.add(new VendaRegistrada(vendaAtual.getDataHoraVenda(), new ArrayList<>(itensAtuais), vendaAtual.getTotalVenda()));
                                itensAtuais.clear();
                            }
                            
                            LocalDateTime dataVenda = LocalDateTime.parse(partes[1], FORMATO_LOG);
                            double totalVenda = Double.parseDouble(partes[2]);
                            // Cria uma venda temporária, os itens serão adicionados depois
                            vendaAtual = new VendaRegistrada(dataVenda, null, totalVenda);

                        } else if ("ITEM".equals(tipo) && vendaAtual != null) {
                            String codigo = partes[1];
                            String nome = partes[2];
                            int qtd = Integer.parseInt(partes[3]);
                            double precoUn = Double.parseDouble(partes[4]);
                            double precoTotal = Double.parseDouble(partes[5]);
                            itensAtuais.add(new ItemVendido(codigo, nome, qtd, precoUn, precoTotal));
                        }
                    } catch (Exception e) {
                        // Loga o erro e continua para a próxima linha
                        System.err.println("Erro ao processar a linha: '" + linha + "' no arquivo " + arquivo.getName());
                        e.printStackTrace();
                    }
                }
                // Adiciona a última venda do arquivo
                if (vendaAtual != null) {
                    todasAsVendas.add(new VendaRegistrada(vendaAtual.getDataHoraVenda(), itensAtuais, vendaAtual.getTotalVenda()));
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return todasAsVendas;
    }

    private void encontrarArquivosDeLog(File diretorio, List<File> listaArquivos) {
        File[] arquivos = diretorio.listFiles();
        if (arquivos == null) return;

        for (File arquivo : arquivos) {
            if (arquivo.isDirectory()) {
                encontrarArquivosDeLog(arquivo, listaArquivos);
            } else {
                if (arquivo.getName().endsWith(".txt")) {
                    listaArquivos.add(arquivo);
                }
            }
        }
    }

    public String gerarRelatorioDiario(LocalDate dia) {
        List<VendaRegistrada> vendasDoDia = carregarTodasAsVendas().stream()
                .filter(v -> v.getDataHoraVenda().toLocalDate().equals(dia))
                .collect(Collectors.toList());

        return formatarRelatorio("Relatório Diário", dia.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")), vendasDoDia);
    }
    
    public String gerarRelatorioMensal(YearMonth mes) {
        List<VendaRegistrada> vendasDoMes = carregarTodasAsVendas().stream()
                .filter(v -> YearMonth.from(v.getDataHoraVenda()).equals(mes))
                .collect(Collectors.toList());
        
        DateTimeFormatter formatoBrasileiro = DateTimeFormatter.ofPattern("MMMM 'de' yyyy", new Locale("pt", "BR"));
        return formatarRelatorio("Relatório Mensal", mes.format(formatoBrasileiro), vendasDoMes);
    }

    private String formatarRelatorio(String titulo, String periodo, List<VendaRegistrada> vendas) {
        if (vendas.isEmpty()) {
            return "Nenhuma venda encontrada para o período de " + periodo + ".";
        }

        double faturamentoTotal = vendas.stream().mapToDouble(VendaRegistrada::getTotalVenda).sum();
        int numeroDeVendas = vendas.size();
        Map<String, Integer> produtosVendidos = new HashMap<>();

        for (VendaRegistrada venda : vendas) {
            for (ItemVendido item : venda.getItens()) {
                produtosVendidos.merge(item.getNome(), item.getQuantidade(), Integer::sum);
            }
        }

        StringBuilder relatorio = new StringBuilder();
        relatorio.append("========================================\n");
        relatorio.append(" ").append(titulo).append("\n");
        relatorio.append(" Período: ").append(periodo).append("\n");
        relatorio.append("========================================\n\n");

        relatorio.append("Faturamento Total: ").append(String.format("R$ %.2f\n", faturamentoTotal));
        relatorio.append("Número de Vendas: ").append(numeroDeVendas).append("\n\n");

        relatorio.append("--- Produtos Vendidos ---\n");
        if(produtosVendidos.isEmpty()){
            relatorio.append("Nenhum produto vendido no período.\n");
        } else {
            produtosVendidos.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .forEach(entry -> relatorio.append(String.format("% -30s | Quantidade: %d\n", entry.getKey(), entry.getValue())));
        }
        
        relatorio.append("\n========================================\n");
        
        return relatorio.toString();
    }
}
