# Trabalho Prático 1: Jogo de Damas (MiniMax)
**Disciplina:** Inteligência Artificial  
**Instituição:** IFSULDEMINAS - Campus Poços de Caldas  
**Professor:** Douglas Castilho  
**Aluna:** Maria Eduarda Batista Henrique  
---

## 1. Visão Geral do Projeto
Este projeto consiste no desenvolvimento de um jogo de damas em um tabuleiro reduzido (**6x6**), onde um jogador humano (Brancas/Min) enfrenta uma Inteligência Artificial (Pretas/Max). O motor de decisão da IA é baseado no algoritmo clássico **MiniMax**, com uma árvore de possibilidades de altura fixa e heurísticas personalizadas.

## 2. Lógica e Regras do Jogo
O sistema implementa as regras oficiais de damas com as adaptações solicitadas:
- **Captura Obrigatória:** Se houver uma peça adversária que possa ser capturada, o jogador é obrigado a fazê-lo. O sistema prioriza a jogada que captura o **maior número de peças**.
- **Movimentação de Damas:** As damas podem se mover múltiplas casas nas diagonais e capturar em qualquer direção.
- **Posição de Parada:** Após uma captura (simples ou múltipla), a peça para na casa imediatamente subsequente à última peça capturada.
- **Condições de Vitória/Empate:**
    - Vitória por extermínio (oponente sem peças).
    - Vitória por travamento (oponente sem movimentos válidos).
    - Empate técnico (apenas 2 damas no tabuleiro sem captura imediata).

## 3. Inteligência Artificial: Algoritmo MiniMax
O algoritmo MiniMax funciona simulando todas as jogadas possíveis até uma determinada profundidade para encontrar a jogada que maximiza o ganho da IA e minimiza o ganho do usuário.

### Estratégia de Busca e Árvore
- **Classe Node:** Utilizada obrigatoriamente para construir a árvore de estados. Cada nó armazena o estado da matriz, a jogada realizada e o valor MiniMax calculado.
- **Altura da Árvore:** Fixada em **10 níveis**.
- **Inteligência Limitada:** O usuário define um nível de 1 a 9. Até esse nível, a IA avalia todas as ramificações. Após esse nível, as jogadas são simuladas de forma **aleatória** até atingir a altura 10, simulando uma "visão limitada" do futuro.

### Função de Avaliação e Heurística
A avaliação dos estados finais da árvore segue dois critérios:
1. **Avaliação Padrão:**
    - `+1.0`: Vitória da IA (Max).
    - `-1.0`: Vitória do Usuário (Min).
    - `0.0`: Empate.
2. **Heurística Inventada (Estados Intermediários):**
    Como a árvore raramente atinge o fim do jogo em 10 níveis, implementamos uma heurística baseada em pesos:
    - Peça Comum: **3 pontos**.
    - Dama: **7 pontos**.
    - O valor final é calculado pela diferença de pontos entre IA e Usuário, normalizado pela função `Math.tanh` para manter o resultado entre -1 e 1.

## 4. Estrutura do Código
- `Tabuleiro.java`: Gerencia a matriz, valida movimentos, executa capturas e verifica regras de promoção.
- `Node.java`: Estrutura de dados para a árvore de busca do MiniMax.
- `MainInterfaceGrafica.java`: Gerencia a interface Swing, o loop do jogo e a integração com o algoritmo de IA.

