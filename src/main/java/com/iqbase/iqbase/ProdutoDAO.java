
package com.iqbase.iqbase;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

public class ProdutoDAO {

    private static final String CAMINHO_ARQUIVO = "data/produtos.txt";
    private List<Produto> listaDeProdutos;

    public ProdutoDAO() {
        this.listaDeProdutos = new ArrayList<>();
        verificarECriarArquivoSeNecessario();
        carregarProdutosDoArquivo();
    }
    
    private void verificarECriarArquivoSeNecessario() {
        File arquivo = new File(CAMINHO_ARQUIVO);
        if (!arquivo.exists()) {
            try {
                // Garante que o diretório 'data' exista
                File diretorio = new File("data");
                if (!diretorio.exists()) {
                    diretorio.mkdirs();
                }
                arquivo.createNewFile();
                salvarListaNoArquivo(); // Salva um arquivo vazio com cabeçalho
                JOptionPane.showMessageDialog(null, "Arquivo 'produtos.txt' não encontrado.\nUm novo arquivo foi criado em 'data/'.", "Aviso", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Ocorreu um erro ao criar o arquivo 'produtos.txt'.", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void carregarProdutosDoArquivo() {
        File arquivo = new File(CAMINHO_ARQUIVO);
        if (!arquivo.exists() || arquivo.length() == 0) {
            return; // Não tenta carregar de um arquivo inexistente ou vazio
        }
        
        try (BufferedReader br = new BufferedReader(new FileReader(CAMINHO_ARQUIVO))) {
            String linha;
            br.readLine(); // Pula a linha do cabeçalho
            while ((linha = br.readLine()) != null) {
                String[] dados = linha.split(";");
                if (dados.length == 5) {
                    String codigo = dados[0];
                    String nome = dados[1];
                    String descricao = dados[2];
                    double precoUnitario = Double.parseDouble(dados[3].replace(",", "."));
                    int quantidadeEstoque = Integer.parseInt(dados[4]);
                    listaDeProdutos.add(new Produto(codigo, nome, descricao, precoUnitario, quantidadeEstoque));
                }
            }
        } catch (IOException | NumberFormatException e) {
            e.printStackTrace();
        }
    }
    
    private void salvarListaNoArquivo() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(CAMINHO_ARQUIVO))) {
            bw.write("codigo;nome;descricao;preco_unitario;quantidade_estoque");
            bw.newLine();
            for (Produto p : listaDeProdutos) {
                String linha = String.join(";",
                        p.getCodigo(),
                        p.getNome(),
                        p.getDescricao(),
                        String.format("%.2f", p.getPrecoUnitario()).replace(",", "."),
                        String.valueOf(p.getQuantidadeEstoque())
                );
                bw.write(linha);
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<Produto> getTodosOsProdutos() {
        return new ArrayList<>(listaDeProdutos);
    }
    
    public Produto getProduto(int index) {
        if (index >= 0 && index < listaDeProdutos.size()) {
            return listaDeProdutos.get(index);
        }
        return null;
    }

    public void adicionarProduto(Produto produto) {
        int maiorCodigo = 0;
        for (Produto p : listaDeProdutos) {
            int codigoAtual = Integer.parseInt(p.getCodigo());
            if (codigoAtual > maiorCodigo) {
                maiorCodigo = codigoAtual;
            }
        }
        produto.setCodigo(String.valueOf(maiorCodigo + 1));
        listaDeProdutos.add(produto);
    }
    
    public void removerProduto(int index) {
        if (index >= 0 && index < listaDeProdutos.size()) {
            listaDeProdutos.remove(index);
            // Reordena os códigos
            for (int i = 0; i < listaDeProdutos.size(); i++) {
                listaDeProdutos.get(i).setCodigo(String.valueOf(i + 1));
            }
        }
    }

    public void salvarAlteracoes() {
        salvarListaNoArquivo();
    }

    public Produto buscarPorCodigo(String codigo) {
        for (Produto produto : listaDeProdutos) {
            if (produto.getCodigo().equals(codigo)) {
                return produto;
            }
        }
        return null; // Retorna nulo se não encontrar
    }

    public List<Produto> buscarPorNome(String termoBusca) {
        List<Produto> produtosEncontrados = new ArrayList<>();
        for (Produto produto : listaDeProdutos) {
            if (produto.getNome().toLowerCase().contains(termoBusca.toLowerCase())) {
                produtosEncontrados.add(produto);
            }
        }
        return produtosEncontrados;
    }
}
