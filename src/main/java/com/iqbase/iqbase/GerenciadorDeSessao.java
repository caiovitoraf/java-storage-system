
package com.iqbase.iqbase;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

public class GerenciadorDeSessao {

    private static GerenciadorDeSessao instancia;
    private String nomeArquivoSessao;
    private boolean sessaoAtiva = false;

    private static final String PASTA_RAIZ_SESSOES = "data/sessoes/";
    private static final DateTimeFormatter FORMATO_NOME_ARQUIVO = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
    private static final DateTimeFormatter FORMATO_LOG = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private GerenciadorDeSessao() {
        // O construtor agora está mais simples, a criação da pasta é dinâmica
    }

    public static synchronized GerenciadorDeSessao getInstancia() {
        if (instancia == null) {
            instancia = new GerenciadorDeSessao();
        }
        return instancia;
    }

    private void iniciarNovaSessao() {
        if (sessaoAtiva) return;

        LocalDateTime agora = LocalDateTime.now();
        String ano = String.valueOf(agora.getYear());
        String mes = String.format("%02d", agora.getMonthValue());
        String dia = String.format("%02d", agora.getDayOfMonth());

        File pastaDaSessao = new File(PASTA_RAIZ_SESSOES + ano + File.separator + mes + File.separator + dia);
        if (!pastaDaSessao.exists()) {
            pastaDaSessao.mkdirs();
        }

        String timestamp = agora.format(FORMATO_NOME_ARQUIVO);
        this.nomeArquivoSessao = pastaDaSessao.getPath() + File.separator + "sessao_" + timestamp + ".txt";
        this.sessaoAtiva = true;
        
        try (PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(this.nomeArquivoSessao, true)))) {
            writer.println("SESSAO_INICIO;" + agora.format(FORMATO_LOG));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void registrarVenda(List<ItemVenda> itens, double totalVenda) {
        if (!sessaoAtiva) {
            iniciarNovaSessao();
        }
        
        try (PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(this.nomeArquivoSessao, true)))) {
            writer.println("VENDA;" + LocalDateTime.now().format(FORMATO_LOG) + ";" + String.format(Locale.US, "%.2f", totalVenda));
            
            for (ItemVenda item : itens) {
                Produto p = item.getProduto();
                writer.println("ITEM;" + 
                               p.getCodigo() + ";" + 
                               p.getNome() + ";" + 
                               item.getQuantidade() + ";" + 
                               String.format(Locale.US, "%.2f", p.getPrecoUnitario()) + ";" + 
                               String.format(Locale.US, "%.2f", item.getPrecoTotal()));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public String fecharSessaoAtual() {
        if (!sessaoAtiva) {
            return null;
        }
        
        String arquivoFinalizado = this.nomeArquivoSessao;
        
        try (PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(this.nomeArquivoSessao, true)))) {
            writer.println("SESSAO_FIM;" + LocalDateTime.now().format(FORMATO_LOG));
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        this.sessaoAtiva = false;
        this.nomeArquivoSessao = null;
        
        return arquivoFinalizado;
    }
}
