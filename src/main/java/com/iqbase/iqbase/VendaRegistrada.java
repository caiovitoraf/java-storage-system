
package com.iqbase.iqbase;

import java.time.LocalDateTime;
import java.util.List;

// Classe para representar um item que foi vendido, de forma simples
class ItemVendido {
    String codigo;
    String nome;
    int quantidade;
    double precoUnitario;
    double precoTotal;

    public ItemVendido(String codigo, String nome, int quantidade, double precoUnitario, double precoTotal) {
        this.codigo = codigo;
        this.nome = nome;
        this.quantidade = quantidade;
        this.precoUnitario = precoUnitario;
        this.precoTotal = precoTotal;
    }

    // Getters para uso futuro
    public String getNome() { return nome; }
    public int getQuantidade() { return quantidade; }
}

// Classe principal para representar uma venda registrada nos logs
public class VendaRegistrada {
    private LocalDateTime dataHoraVenda;
    private List<ItemVendido> itens;
    private double totalVenda;

    public VendaRegistrada(LocalDateTime dataHoraVenda, List<ItemVendido> itens, double totalVenda) {
        this.dataHoraVenda = dataHoraVenda;
        this.itens = itens;
        this.totalVenda = totalVenda;
    }

    // Getters
    public LocalDateTime getDataHoraVenda() {
        return dataHoraVenda;
    }

    public List<ItemVendido> getItens() {
        return itens;
    }

    public double getTotalVenda() {
        return totalVenda;
    }
}
