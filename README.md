# IA-jogo-de-Damas

Motor de Inteligência Artificial para o jogo de Damas (tabuleiro 6x6) em Java, com interface gráfica em Swing, em que a IA decide as jogadas pelo algoritmo **MiniMax**.

## Sobre

Projeto desenvolvido em Java para a disciplina de Inteligência Artificial. O usuário joga com as peças brancas (jogador **MIN**) e a IA joga com as pretas (jogador **MAX**). A IA constrói uma árvore de possibilidades de até **10 níveis** e aplica o MiniMax com uma heurística própria para escolher a melhor jogada.

## Funcionalidades

- Tabuleiro reduzido **6x6** com as regras clássicas de damas.
- **Captura obrigatória**: quando há captura disponível, o jogo só permite capturar, priorizando a sequência que remove o maior número de peças.
- **Damas** que se movem por múltiplas casas na diagonal e capturam em qualquer direção, com promoção automática das peças.
- Condições de fim de jogo: eliminação de peças, travamento (jogador sem movimentos válidos) e empate técnico (duas damas sem captura imediata).
- IA com **MiniMax**: árvore de estados com profundidade fixa (10 níveis) e nível de inteligência configurável (1–9) — acima do nível escolhido, a busca simula jogadas aleatórias ("visão limitada" do futuro).
- **Heurística de avaliação**: pesos 3 para peça comum e 7 para dama, com resultado normalizado pela função `tanh`; vitória da IA = +1.0, do usuário = −1.0 e empate = 0.0.
- Interface gráfica em **Swing**: escolha de quem começa, seletor do nível de inteligência, destaque das jogadas possíveis e botão de reiniciar.

## Tecnologias

- **Java** (pacote `main`)
- **Swing** (`javax.swing`) para a interface gráfica
- Projeto simples, sem gerenciador de dependências (sem Maven/Gradle)

## Estrutura

```
src/
└── main/
    ├── MainInterfaceGrafica.java   # interface Swing, fluxo do jogo e integração com a IA
    ├── Node.java                   # nó da árvore de busca (estado + valor MiniMax)
    └── Tabuleiro.java              # lógica do tabuleiro, movimentos, capturas e promoção
```

## Como executar

Abra a pasta do projeto em uma IDE Java (ex.: IntelliJ IDEA ou Eclipse) e execute a classe `main.MainInterfaceGrafica`.

Também é possível compilar e executar pelo terminal:

```
javac -d out src/main/*.java
java -cp out main.MainInterfaceGrafica
```
