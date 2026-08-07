package main;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * classe Principal que gerencia a Interface Gráfica (Swing) e o fluxo do jogo.
 * integra a lógica do tabuleiro com o algoritmo de Inteligência Artificial MiniMax.
 * 
 * @author Maria Eduarda Batista Henrique
 */
public final class MainInterfaceGrafica extends JFrame {

    private final int TAMANHO = 6;
    private final CasaBotao[][] tabuleiroInterface = new CasaBotao[TAMANHO][TAMANHO];
    private final Tabuleiro tabuleiroLogico;
    
    // Variáveis de controle de estado
    private int linhaOrigem = -1, colOrigem = -1;
    private boolean turnoBrancas = true; // Brancas (Usuário) começam por padrão
    private int nivelInteligencia = 3;   // Profundidade da busca inteligente (1-9)
    private final int ALTURA_MINIMA_ARVORE = 10; // Requisito do PDF
    
    private List<Tabuleiro.Jogada> jogadasAtuaisPossiveis = new ArrayList<>();
    private final Random random = new Random();

    public MainInterfaceGrafica() {
        tabuleiroLogico = new Tabuleiro();

        setTitle("IFSULDEMINAS - IA - Jogo de Damas 6x6 (MiniMax)");
        setSize(600, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // Painel de controle superior
        JPanel painelControle = new JPanel();
        String[] niveis = {"1", "2", "3", "4", "5", "6", "7", "8", "9"};
        JComboBox<String> comboNivel = new JComboBox<>(niveis);
        comboNivel.setSelectedIndex(2);
        comboNivel.addActionListener(e -> nivelInteligencia = Integer.parseInt((String) comboNivel.getSelectedItem()));
        
        painelControle.add(new JLabel("Nível Inteligência (1-9):"));
        painelControle.add(comboNivel);
        
        JButton btnReiniciar = new JButton("Reiniciar");
        btnReiniciar.addActionListener(e -> reiniciarJogo());
        painelControle.add(btnReiniciar);

        // Construção do grid do tabuleiro
        JPanel painelTabuleiro = new JPanel(new GridLayout(TAMANHO, TAMANHO));
        for (int i = 0; i < TAMANHO; i++) {
            for (int j = 0; j < TAMANHO; j++) {
                tabuleiroInterface[i][j] = new CasaBotao();
                resetarCorCasa(i, j);
                int r = i, c = j;
                tabuleiroInterface[i][j].addActionListener(e -> tratarClique(r, c));
                painelTabuleiro.add(tabuleiroInterface[i][j]);
            }
        }

        add(painelControle, BorderLayout.NORTH);
        add(painelTabuleiro, BorderLayout.CENTER);

        sincronizarInterface();
        setVisible(true);
        
        perguntarQuemComeca();
    }

    /**
     * implementa o requisito de definir quem começa o jogo
     */
    private void perguntarQuemComeca() {
        int escolha = JOptionPane.showOptionDialog(this, "Quem começa o jogo?", "Início",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null,
                new String[]{"Eu (Brancas)", "IA (Pretas)"}, "Eu (Brancas)");
        
        if (escolha == JOptionPane.NO_OPTION) {
            turnoBrancas = false;
            executarJogadaIA();
        }
    }

    private void reiniciarJogo() {
        tabuleiroLogico.setMatriz(new Tabuleiro().getMatriz());
        turnoBrancas = true;
        cancelarSelecao();
        sincronizarInterface();
        perguntarQuemComeca();
    }

    private void resetarCorCasa(int r, int c) {
        if ((r + c) % 2 == 0) {
            tabuleiroInterface[r][c].setBackground(new Color(235, 235, 208));
        } else {
            tabuleiroInterface[r][c].setBackground(new Color(119, 149, 86));
        }
    }

    /**
     * Gerencia a interação do usuário com o tabuleiro.
     * Implementa o destaque de jogadas e a captura obrigatória.
     */
    private void tratarClique(int r, int c) {
        if (!turnoBrancas) return; // Bloqueia clique se for a vez da IA

        List<Tabuleiro.Jogada> todasPossiveis = tabuleiroLogico.getJogadasPossiveis('1');

        if (linhaOrigem == -1) { // Primeiro clique: selecionar peça
            char peca = tabuleiroLogico.getMatriz()[r][c];
            if (peca == '1' || peca == '3') {
                List<Tabuleiro.Jogada> jogadasDestaPeca = new ArrayList<>();
                for (Tabuleiro.Jogada j : todasPossiveis) {
                    if (j.r1 == r && j.c1 == c) jogadasDestaPeca.add(j);
                }

                if (!jogadasDestaPeca.isEmpty()) {
                    linhaOrigem = r; colOrigem = c;
                    jogadasAtuaisPossiveis = jogadasDestaPeca;
                    // Destaque visual (Requisito de usabilidade)
                    tabuleiroInterface[r][c].setBackground(Color.YELLOW);
                    for (Tabuleiro.Jogada j : jogadasDestaPeca) {
                        tabuleiroInterface[j.r2][j.c2].setBackground(new Color(173, 216, 230));
                    }
                } else {
                    // verifica se o impedimento é por causa de uma captura obrigatória em outra peça
                    boolean existeCaptura = false;
                    for(Tabuleiro.Jogada j : todasPossiveis) if(j.isCaptura) { existeCaptura = true; break; }
                    if(existeCaptura) JOptionPane.showMessageDialog(this, "Captura obrigatória disponível em outra peça!");
                }
            }
        } else { // segundo clique destino do movimento
            Tabuleiro.Jogada escolhida = null;
            for (Tabuleiro.Jogada j : jogadasAtuaisPossiveis) {
                if (j.r2 == r && j.c2 == c) { escolhida = j; break; }
            }

            if (escolhida != null) {
                tabuleiroLogico.executarJogada(escolhida);
                cancelarSelecao();
                sincronizarInterface();
                if (verificarFimDeJogo()) return;
                
                turnoBrancas = false;
                // delay pequeno para o usuário ver sua jogada antes da IA responder
                Timer timer = new Timer(500, e -> executarJogadaIA());
                timer.setRepeats(false);
                timer.start();
            } else {
                cancelarSelecao();
                // se clicar em outra peça válida, seleciona ela imediatamente
                if (tabuleiroLogico.getMatriz()[r][c] == '1' || tabuleiroLogico.getMatriz()[r][c] == '3') tratarClique(r, c);
            }
        }
    }

    private void cancelarSelecao() {
        if (linhaOrigem != -1) {
            resetarCorCasa(linhaOrigem, colOrigem);
            for (Tabuleiro.Jogada j : jogadasAtuaisPossiveis) resetarCorCasa(j.r2, j.c2);
        }
        linhaOrigem = -1; colOrigem = -1;
        jogadasAtuaisPossiveis.clear();
    }

    /**
     * ponto de entrada da Inteligência Artificial
     * constrói a árvore de possibilidades e aplica o MiniMax
     */
    private void executarJogadaIA() {
        Node raiz = new Node();
        raiz.setMatrix(tabuleiroLogico.clone().getMatriz());
        raiz.setTurn(false); // IA é o jogador MAX (Pretas)

        // 1. Montagem da árvore com altura 10 (Requisito)
        montarArvoreIA(raiz, ALTURA_MINIMA_ARVORE, 0);
        
        // 2. Execução do algoritmo MiniMax
        minMax(raiz, true);

        // 3. Escolha da melhor jogada baseada no valor retornado
        Node melhorNo = null;
        double melhorValor = Double.NEGATIVE_INFINITY;
        
        for (Node filho : raiz.getChild()) {
            if (filho.getMinMax() > melhorValor) {
                melhorValor = filho.getMinMax();
                melhorNo = filho;
            }
        }

        if (melhorNo != null) {
            Node.JogadaRealizada jr = melhorNo.getJogada();
            List<Tabuleiro.Jogada> possiveis = tabuleiroLogico.getJogadasPossiveis('2');
            for (Tabuleiro.Jogada j : possiveis) {
                if (j.r1 == jr.r1 && j.c1 == jr.c1 && j.r2 == jr.r2 && j.c2 == jr.c2) {
                    tabuleiroLogico.executarJogada(j);
                    break;
                }
            }
        }

        sincronizarInterface();
        turnoBrancas = true;
        verificarFimDeJogo();
    }

    /**
     * constrói a árvore de estados
     * implementa a estratégia de "Inteligência Limitada":
     * - até o nível definido pelo usuário: Busca todas as jogadas
     * - após o nível definido: escolhe jogadas aleatórias até completar a altura 10
     */
    private void montarArvoreIA(Node pai, int profRestante, int profAtual) {
        if (profRestante == 0) return;

        char jogador = pai.isTurn() ? '1' : '2';
        Tabuleiro tempTab = new Tabuleiro();
        tempTab.setMatriz(pai.getMatrix());
        
        List<Tabuleiro.Jogada> jogadas = tempTab.getJogadasPossiveis(jogador);

        // simulação de inteligência limitada
        if (profAtual >= nivelInteligencia && !jogadas.isEmpty()) {
            Tabuleiro.Jogada jAleatoria = jogadas.get(random.nextInt(jogadas.size()));
            jogadas = new ArrayList<>();
            jogadas.add(jAleatoria);
        }

        for (Tabuleiro.Jogada j : jogadas) {
            Tabuleiro cloneTab = tempTab.clone();
            cloneTab.executarJogada(j);
            
            Node filho = new Node();
            filho.setMatrix(cloneTab.getMatriz());
            filho.setTurn(!pai.isTurn());
            filho.setJogada(new Node.JogadaRealizada(j.r1, j.c1, j.r2, j.c2));
            
            pai.addChild(filho);
            montarArvoreIA(filho, profRestante - 1, profAtual + 1); //recursão
        }
    }

    /**
     * algoritmo MiniMax Clássico.
     * maximiza para a IA e minimiza para o usuário.
     */
    private double minMax(Node node, boolean isMax) {
        if (node.getChild().isEmpty()) {
            double h = aplicarHeuristicaVerificacaoGanhador(node.getMatrix());
            node.setMinMax(h);
            return h;
        }

        if (isMax) {
            double maxVal = Double.NEGATIVE_INFINITY;
            for (Node filho : node.getChild()) maxVal = Math.max(maxVal, minMax(filho, false));
            node.setMinMax(maxVal);
            return maxVal;
        } else {
            double minVal = Double.POSITIVE_INFINITY;
            for (Node filho : node.getChild()) minVal = Math.min(minVal, minMax(filho, true));
            node.setMinMax(minVal);
            return minVal;
        }
    }

    /**
     * função de avaliação e heurística inventada.
     * retorna +1 para vitória da IA, -1 para vitória do usuário
     * para estados intermediários, usa uma heurística baseada no peso das peças
     */
    private double aplicarHeuristicaVerificacaoGanhador(char[][] matriz) {
        int brancas = 0, pretas = 0, dBrancas = 0, dPretas = 0;
        for (int i = 0; i < TAMANHO; i++) {
            for (int j = 0; j < TAMANHO; j++) {
                if (matriz[i][j] == '1') brancas++;
                else if (matriz[i][j] == '3') { brancas++; dBrancas++; }
                else if (matriz[i][j] == '2') pretas++;
                else if (matriz[i][j] == '4') { pretas++; dPretas++; }
            }
        }
        
        // requisito: +1 vitória Max, -1 vitória Min
        if (pretas == 0) return -1.0; 
        if (brancas == 0) return 1.0;  
        
        // heurística Inventada: Peso 3 para peças e 7 para damas
        // normalizado com tangente hiperbolica para manter o valor entre -1 e 1
        double score = (pretas * 3 + dPretas * 7) - (brancas * 3 + dBrancas * 7);
        return Math.tanh(score / 20.0); 
    }

    /**
     * verifica as condições pra terminar o jogo:
     * - falta peças
     * - falta movimentos
     * - empate de duas damas
     */
    private boolean verificarFimDeJogo() {
        int brancas = 0, pretas = 0, dBrancas = 0, dPretas = 0;
        for (int i = 0; i < TAMANHO; i++) {
            for (int j = 0; j < TAMANHO; j++) {
                char p = tabuleiroLogico.getMatriz()[i][j];
                if (p == '1' || p == '3') { brancas++; if(p=='3') dBrancas++; }
                if (p == '2' || p == '4') { pretas++; if(p=='4') dPretas++; }
            }
        }

        if (pretas == 0) {
            JOptionPane.showMessageDialog(this, "Parabéns! Você (Brancas - MIN) venceu! A IA não tem mais peças.");
            reiniciarJogo(); return true;
        }
        if (brancas == 0) {
            JOptionPane.showMessageDialog(this, "IA (Pretas - MAX) venceu! Você não tem mais peças.");
            reiniciarJogo(); return true;
        }

        List<Tabuleiro.Jogada> jogadasBrancas = tabuleiroLogico.getJogadasPossiveis('1');
        List<Tabuleiro.Jogada> jogadasPretas = tabuleiroLogico.getJogadasPossiveis('2');
        
        if (turnoBrancas && jogadasBrancas.isEmpty()) {
            JOptionPane.showMessageDialog(this, "IA (Pretas - MAX) venceu! Você não tem movimentos.");
            reiniciarJogo(); return true;
        }
        if (!turnoBrancas && jogadasPretas.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Você (Brancas - MIN) venceu! A IA não tem movimentos.");
            reiniciarJogo(); return true;
        }
        
        // Regra de Empate: 2 damas sem captura imediata
        if ((brancas + pretas) == 2 && dBrancas == 1 && dPretas == 1) {
            boolean temCaptura = false;
            List<Tabuleiro.Jogada> j = turnoBrancas ? jogadasBrancas : jogadasPretas;
            for(Tabuleiro.Jogada jog : j) if(jog.isCaptura) temCaptura = true;
            
            if (!temCaptura) {
                JOptionPane.showMessageDialog(this, "Empate! Restam apenas duas damas sem captura imediata.");
                reiniciarJogo(); return true;
            }
        }
        return false;
    }

    public void sincronizarInterface() {
        for (int i = 0; i < TAMANHO; i++) {
            for (int j = 0; j < TAMANHO; j++) {
                tabuleiroInterface[i][j].setTipoPeca(tabuleiroLogico.getMatriz()[i][j]);
            }
        }
    }

    private class CasaBotao extends JButton {
        private char tipoPeca = '0';
        public void setTipoPeca(char tipo) { this.tipoPeca = tipo; repaint(); }
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int m = 10;
            if (tipoPeca == '1' || tipoPeca == '3') { // Brancas
                g2.setColor(Color.WHITE); g2.fillOval(m, m, getWidth()-2*m, getHeight()-2*m);
                g2.setColor(Color.BLACK); g2.drawOval(m, m, getWidth()-2*m, getHeight()-2*m);
            } else if (tipoPeca == '2' || tipoPeca == '4') { // Pretas
                g2.setColor(Color.BLACK); g2.fillOval(m, m, getWidth()-2*m, getHeight()-2*m);
            }
            if (tipoPeca == '3' || tipoPeca == '4') { // Destaque de Dama
                g2.setColor(Color.YELLOW); g2.setStroke(new BasicStroke(3));
                g2.drawOval(m+5, m+5, getWidth()-2*m-10, getHeight()-2*m-10);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MainInterfaceGrafica::new);
    }
}
