# Documentação do Projeto IQBase

Este documento detalha a arquitetura e o funcionamento do projeto IQBase, um sistema de gestão de estoque e vendas desenvolvido em Java com a biblioteca Swing para a interface gráfica.

## Estrutura do Projeto

O projeto está organizado da seguinte forma:

- **`src/main/java/com/iqbase/iqbase`**: Contém todo o código-fonte da aplicação.
- **`data`**: Contém os arquivos de texto que servem como banco de dados para produtos e funcionários.
- **`pom.xml`**: Arquivo de configuração do Maven, que gerencia as dependências e o build do projeto.

## Análise dos Arquivos

Vamos analisar cada arquivo do projeto em detalhes.

### `IQBase.java`

Este é o arquivo principal da aplicação, responsável por iniciar a execução do programa.

```java
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.iqbase.iqbase;


import javax.swing.SwingUtilities;

/**
 *
 * @author caiov-fedora
 */
public class IQBase {

    public static void main(String[] args) {
        // Garante que a UI seja criada e exibida na Event Dispatch Thread
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                TelaInicial telaInicial = new TelaInicial();
                telaInicial.setVisible(true);
            }
        });
    }
}
```

#### Lógica Principal

- **`main(String[] args)`**: Este é o método de entrada da aplicação.
- **`SwingUtilities.invokeLater(new Runnable() { ... })`**: Esta é uma prática recomendada para aplicações Swing. Ela garante que a interface gráfica (UI) seja criada e atualizada no *Event Dispatch Thread* (EDT). O EDT é um thread de background dedicado a processar todos os eventos da UI, como cliques de botão, movimentos do mouse e atualizações de componentes. Ao colocar a criação da `TelaInicial` dentro do `invokeLater`, evitamos problemas de concorrência e garantimos que a UI permaneça responsiva.
- **`new TelaInicial().setVisible(true)`**: Dentro do `run()` do `Runnable`, uma nova instância da `TelaInicial` é criada e tornada visível. Isso efetivamente inicia a interface com o usuário, exibindo a primeira tela da aplicação.

### Conexões

- `IQBase.java` inicia a aplicação instanciando e exibindo a `TelaInicial.java`.

### `TelaInicial.java`

Esta é a primeira tela que o usuário vê. Ela serve como um portal de entrada, oferecendo as opções de fazer login no sistema ou sair da aplicação.

```java
package com.iqbase.iqbase;

import javax.swing.*;
import java.awt.*;

public class TelaInicial extends JFrame {

    public TelaInicial() {
        setTitle("IQBase - Bem-vindo");
        setSize(400, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Centraliza a janela

        JPanel painel = new JPanel(new GridLayout(2, 1, 10, 10));
        painel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));

        JButton botaoLogin = new JButton("Fazer Login");
        botaoLogin.addActionListener(e -> {
            TelaLogin telaLogin = new TelaLogin(this);
            telaLogin.setVisible(true);
            
            // Se o login for bem-sucedido, a tela de login vai esconder a tela inicial
            if (telaLogin.isLoginSucedido()) {
                dispose(); // Fecha a tela inicial
            }
        });
        painel.add(botaoLogin);

        JButton botaoSair = new JButton("Sair");
        botaoSair.addActionListener(e -> System.exit(0));
        painel.add(botaoSair);

        add(painel);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new TelaInicial().setVisible(true));
    }
}
```

#### Estrutura e Componentes

- **`TelaInicial extends JFrame`**: A classe herda de `JFrame`, o que a torna uma janela da aplicação.
- **`setTitle`, `setSize`, `setDefaultCloseOperation`, `setLocationRelativeTo`**: Configurações básicas da janela.
  - `setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE)`: Garante que a aplicação seja encerrada quando esta janela for fechada.
  - `setLocationRelativeTo(null)`: Centraliza a janela na tela.
- **`JPanel` e `GridLayout`**: Um `JPanel` é usado como contêiner para os botões. O `GridLayout(2, 1, 10, 10)` organiza os componentes em um grid de 2 linhas e 1 coluna, com espaçamento de 10 pixels entre eles.
- **`JButton("Fazer Login")`**: O botão que inicia o processo de login.
- **`JButton("Sair")`**: O botão que encerra a aplicação.

#### Lógica de Eventos

- **`botaoLogin.addActionListener(...)`**: Quando o botão "Fazer Login" é clicado:
  1. Uma nova instância da `TelaLogin` é criada. A `TelaInicial` (`this`) é passada como argumento para o construtor da `TelaLogin`.
  2. A `TelaLogin` é tornada visível.
  3. **Observação importante sobre o fluxo de login**: O código `if (telaLogin.isLoginSucedido()) { dispose(); }` é executado *imediatamente* após a `TelaLogin` se tornar visível. No entanto, a `TelaLogin` provavelmente é uma janela modal (um `JDialog`), o que significa que a execução do código na `TelaInicial` para até que a `TelaLogin` seja fechada. Se `isLoginSucedido()` for uma flag que a `TelaLogin` define internamente, o `dispose()` só ocorrerá *depois* que o usuário interagir com a tela de login e ela for fechada. O código funcionará como esperado se `TelaLogin` for um `JDialog` modal.
- **`botaoSair.addActionListener(...)`**: Quando o botão "Sair" é clicado, `System.exit(0)` é chamado, encerrando a JVM e, consequentemente, a aplicação.

### Conexões

- É instanciada pela `IQBase.java`.
- Instancia e exibe a `TelaLogin.java` quando o botão de login é pressionado.
