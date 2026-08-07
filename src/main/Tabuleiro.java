package main;

import java.util.ArrayList;
import java.util.List;

/**
 * classe responsável por toda a lógica estrutural do jogo de damas
 * aqui gerencia a matriz do tabuleiro, as regras de movimentação e as capturas
 */
public class Tabuleiro implements Cloneable {

    private char[][] matriz;
    private final int TAMANHO = 6; // Tabuleiro reduzido conforme solicitado no PDF

    public Tabuleiro() {
        this.matriz = new char[TAMANHO][TAMANHO];
        inicializar();
    }

    /**
     * Peças Brancas (1) ficam na parte inferior, Pretas (2) na superior
     */
    private void inicializar() {
        for (int i = 0; i < TAMANHO; i++) {
            for (int j = 0; j < TAMANHO; j++) {
                matriz[i][j] = '0'; // Casa vazia
                if ((i + j) % 2 != 0) { // Apenas casas escuras são usadas
                    if (i < 2) {
                        matriz[i][j] = '2'; // Pretas (IA - Jogador MAX)
                    } else if (i > 3) {
                        matriz[i][j] = '1'; // Brancas (Usuário - Jogador MIN)
                    }
                }
            }
        }
    }

    /**
     * cria copias do tabuleiro
     * pra simular jogadas futuras sem alterar o jogo real
     */
    @Override
    public Tabuleiro clone() {
        try {
            Tabuleiro clone = (Tabuleiro) super.clone();
            clone.matriz = new char[TAMANHO][];
            for (int i = 0; i < TAMANHO; i++) {
                clone.matriz[i] = this.matriz[i].clone();
            }
            return clone;
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }

    public char[][] getMatriz() { return matriz; }
    public void setMatriz(char[][] matriz) { this.matriz = matriz; }
    public int getTAMANHO() { return TAMANHO; }

    public boolean isDentro(int r, int c) {
        return r >= 0 && r < TAMANHO && c >= 0 && c < TAMANHO;
    }

    /**
     * classe interna para representar uma jogada completa,
     * incluindo a origem, destino e quais peças foram capturadas
     */
    public static class Jogada {
        public int r1, c1, r2, c2;
        public List<int[]> capturadas = new ArrayList<>();
        public boolean isCaptura = false;

        public Jogada(int r1, int c1, int r2, int c2) {
            this.r1 = r1; this.c1 = c1; this.r2 = r2; this.c2 = c2;
        }
    }

    /**
     * identifica todos os movimentos que um jogador pode fazer
     * implementa a regra de CAPTURA OBRIGATÓRIA
     */
    public List<Jogada> getJogadasPossiveis(char jogador) {
        List<Jogada> capturas = new ArrayList<>();
        List<Jogada> movimentosNormais = new ArrayList<>();

        char pecaComum = jogador;
        char dama = (jogador == '1') ? '3' : '4';

        // 1. Primeiro, buscamos todas as capturas possíveis (Prioridade)
        for (int i = 0; i < TAMANHO; i++) {
            for (int j = 0; j < TAMANHO; j++) {
                if (matriz[i][j] == pecaComum || matriz[i][j] == dama) {
                    buscarCapturas(i, j, i, j, new ArrayList<>(), capturas);
                }
            }
        }

        // se tiver capturas possiveis o jogador é OBRIGADO a capturar o máximo de peças
        if (!capturas.isEmpty()) {
            int maxCapturas = 0;
            for (Jogada j : capturas) {
                if (j.capturadas.size() > maxCapturas) maxCapturas = j.capturadas.size();
            }
            List<Jogada> melhorCapturas = new ArrayList<>();
            for (Jogada j : capturas) {
                if (j.capturadas.size() == maxCapturas) melhorCapturas.add(j);
            }
            return melhorCapturas;
        }

        // se não tiver capturas, busca movimentos simples
        for (int i = 0; i < TAMANHO; i++) {
            for (int j = 0; j < TAMANHO; j++) {
                if (matriz[i][j] == pecaComum || matriz[i][j] == dama) {
                    buscarMovimentosNormais(i, j, movimentosNormais);
                }
            }
        }

        return movimentosNormais;
    }

    /**
     * algoritmo recursivo para encontrar sequências de capturas (comida múltipla).
     */
    private void buscarCapturas(int rOrigem, int cOrigem, int rAtual, int cAtual, List<int[]> capturadasAteAgora, List<Jogada> todasCapturas) {
        char peca = matriz[rAtual][cAtual];
        boolean isDama = (peca == '3' || peca == '4');
        int[][] dirs = {{1, 1}, {1, -1}, {-1, 1}, {-1, -1}};

        boolean encontrouCaptura = false;

        for (int[] d : dirs) {
            // Peça comum não captura para trás (regra do PDF)
            if (!isDama) {
                if (peca == '1' && d[0] > 0) continue;
                if (peca == '2' && d[0] < 0) continue;
            }

            int nr = rAtual + d[0]; // Casa da peça adversária
            int nc = cAtual + d[1];
            
            // Lógica para Dama: ela pode "voar" casas vazias antes de capturar
            if (isDama) {
                while (isDentro(nr, nc) && matriz[nr][nc] == '0') {
                    nr += d[0]; nc += d[1];
                }
            }

            int fr = nr + d[0]; // Casa de pouso após a captura
            int fc = nc + d[1];

            if (isDentro(fr, fc)) {
                char alvo = matriz[nr][nc];
                // Verifica se a casa tem um inimigo e se a casa de pouso está vazia
                if (alvo != '0' && !isMesmoTime(peca, alvo) && matriz[fr][fc] == '0') {
                    boolean jaCapturada = false;
                    for (int[] cap : capturadasAteAgora) {
                        if (cap[0] == nr && cap[1] == nc) { jaCapturada = true; break; }
                    }

                    if (!jaCapturada) {
                        encontrouCaptura = true;
                        List<int[]> novaCapturadas = new ArrayList<>(capturadasAteAgora);
                        novaCapturadas.add(new int[]{nr, nc});
                        
                        // Simulação temporária para busca recursiva
                        char tempAlvo = matriz[nr][nc];
                        matriz[nr][nc] = '0';
                        matriz[fr][fc] = peca;
                        matriz[rAtual][cAtual] = '0';
                        
                        buscarCapturas(rOrigem, cOrigem, fr, fc, novaCapturadas, todasCapturas);
                        
                        // Backtracking: restaura o estado original
                        matriz[rAtual][cAtual] = peca;
                        matriz[fr][fc] = '0';
                        matriz[nr][nc] = tempAlvo;
                    }
                }
            }
        }

        // Se não há mais capturas nesta linha, adicionamos a jogada finalizada
        if (!encontrouCaptura && !capturadasAteAgora.isEmpty()) {
            Jogada j = new Jogada(rOrigem, cOrigem, rAtual, cAtual);
            j.capturadas = new ArrayList<>(capturadasAteAgora);
            j.isCaptura = true;
            todasCapturas.add(j);
        }
    }

    private void buscarMovimentosNormais(int r, int c, List<Jogada> movimentos) {
        char peca = matriz[r][c];
        boolean isDama = (peca == '3' || peca == '4');
        int[][] dirs = {{1, 1}, {1, -1}, {-1, 1}, {-1, -1}};

        for (int[] d : dirs) {
            if (!isDama) {
                if (peca == '1' && d[0] > 0) continue;
                if (peca == '2' && d[0] < 0) continue;
                int nr = r + d[0]; int nc = c + d[1];
                if (isDentro(nr, nc) && matriz[nr][nc] == '0') movimentos.add(new Jogada(r, c, nr, nc));
            } else {
                // Dama move múltiplas casas vazias
                int nr = r + d[0]; int nc = c + d[1];
                while (isDentro(nr, nc) && matriz[nr][nc] == '0') {
                    movimentos.add(new Jogada(r, c, nr, nc));
                    nr += d[0]; nc += d[1];
                }
            }
        }
    }

    private boolean isMesmoTime(char p1, char p2) {
        if (p1 == '0' || p2 == '0') return false;
        boolean time1 = (p1 == '1' || p1 == '3');
        boolean time2 = (p2 == '1' || p2 == '3');
        return time1 == time2;
    }

    /**
     * aplica a jogada no tabuleiro, remove peças comidas e promove a Dama se necessário
     */
    public void executarJogada(Jogada j) {
        char peca = matriz[j.r1][j.c1];
        matriz[j.r1][j.c1] = '0';
        for (int[] cap : j.capturadas) matriz[cap[0]][cap[1]] = '0';
        matriz[j.r2][j.c2] = peca;
        
        // Promoção para Dama
        if (peca == '1' && j.r2 == 0) matriz[j.r2][j.c2] = '3';
        if (peca == '2' && j.r2 == TAMANHO - 1) matriz[j.r2][j.c2] = '4';
    }
}
