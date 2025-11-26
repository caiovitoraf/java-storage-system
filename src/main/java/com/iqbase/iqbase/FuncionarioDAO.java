package com.iqbase.iqbase;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

public class FuncionarioDAO {

    private static final String CAMINHO_ARQUIVO = "data/funcionarios.txt";
    private List<Funcionario> listaDeFuncionarios;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public FuncionarioDAO() {
        this.listaDeFuncionarios = new ArrayList<>();
        verificarECriarArquivoSeNecessario();
        carregarFuncionariosDoArquivo();
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
                JOptionPane.showMessageDialog(null, "Arquivo 'funcionarios.txt' não encontrado.\nUm novo arquivo foi criado em 'data/'.", "Aviso", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Ocorreu um erro ao criar o arquivo 'funcionarios.txt'.", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void carregarFuncionariosDoArquivo() {
        File arquivo = new File(CAMINHO_ARQUIVO);
        if (!arquivo.exists() || arquivo.length() == 0) {
            return; // Não tenta carregar de um arquivo inexistente ou vazio
        }

        try (BufferedReader br = new BufferedReader(new FileReader(CAMINHO_ARQUIVO))) {
            String linha;
            br.readLine(); // Pula a linha do cabeçalho
            while ((linha = br.readLine()) != null) {
                String[] dados = linha.split(";");
                if (dados.length == 7) {
                    int codigo = Integer.parseInt(dados[0]);
                    String nome = dados[1];
                    LocalDate dataNascimento = LocalDate.parse(dados[2], DATE_FORMATTER);
                    String sexo = dados[3];
                    String cargo = dados[4];
                    String usuario = dados[5];
                    String senha = dados[6];
                    listaDeFuncionarios.add(new Funcionario(codigo, nome, dataNascimento, sexo, cargo, usuario, senha));
                }
            }
        } catch (IOException | NumberFormatException e) {
            e.printStackTrace();
        }
    }
    
    private void salvarListaNoArquivo() {
        // Garante que o diretório 'data' exista
        File diretorio = new File("data");
        if (!diretorio.exists()) {
            diretorio.mkdirs();
        }

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(CAMINHO_ARQUIVO))) {
            bw.write("codigo;nome;data_nascimento;sexo;cargo;usuario;senha");
            bw.newLine();
            for (Funcionario f : listaDeFuncionarios) {
                String linha = String.join(";",
                        String.valueOf(f.getCodigo()),
                        f.getNome(),
                        f.getDataNascimento().format(DATE_FORMATTER),
                        f.getSexo(),
                        f.getCargo(),
                        f.getUsuario(),
                        f.getSenha()
                );
                bw.write(linha);
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<Funcionario> getTodosOsFuncionarios() {
        return new ArrayList<>(listaDeFuncionarios);
    }
    
    public void adicionarFuncionario(Funcionario funcionario) {
        int maiorCodigo = 0;
        for (Funcionario f : listaDeFuncionarios) {
            if (f.getCodigo() > maiorCodigo) {
                maiorCodigo = f.getCodigo();
            }
        }
        funcionario.setCodigo(maiorCodigo + 1);
        listaDeFuncionarios.add(funcionario);
        salvarAlteracoes();
    }
    
    public void removerFuncionario(Funcionario funcionario) {
        listaDeFuncionarios.remove(funcionario);
        salvarAlteracoes();
    }

    public void salvarAlteracoes() {
        salvarListaNoArquivo();
    }
    
    public Funcionario autenticar(String usuario, String senha) {
        for (Funcionario f : listaDeFuncionarios) {
            if (f.getUsuario().equals(usuario) && f.getSenha().equals(senha)) {
                return f; // Retorna o funcionário se o usuário e a senha corresponderem
            }
        }
        return null; // Retorna nulo se não encontrar correspondência
    }
}
