package main;

import java.util.ArrayList;


public class Node {
    
    private JogadaRealizada jogada; // A jogada que levou a este estado
    private boolean turn; // TRUE - white (User); FALSE - black (IA);
    private char[][] matrix;
    private double minMax;
    private ArrayList<Node> children;
    
    public static class JogadaRealizada {
        public int r1, c1, r2, c2;
        public JogadaRealizada(int r1, int c1, int r2, int c2) {
            this.r1 = r1; this.c1 = c1; this.r2 = r2; this.c2 = c2;
        }
    }

    public Node () {
        this.children = new ArrayList<>();
        this.minMax = Double.NaN;
    }

    public double getMinMax() {
        return minMax;
    }

    public void setMinMax(double minMax) {
        this.minMax = minMax;
    }
    
    public ArrayList<Node> getChild (){
        return this.children;
    }
    
    public void addChild (Node child){
        this.children.add(child);
    }
    
    public JogadaRealizada getJogada() {
        return jogada;
    }

    public void setJogada(JogadaRealizada jogada) {
        this.jogada = jogada;
    }

    public boolean isTurn() {
        return turn;
    }

    public void setTurn(boolean turn) {
        this.turn = turn;
    }

    public char[][] getMatrix() {
        return matrix;
    }

    public void setMatrix(char[][] matrix) {
        this.matrix = matrix;
    }
}
