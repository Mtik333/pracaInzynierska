/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package data.graph;

import data.ConstStrings;

/**
 * @author Mateusz
 */
public class Edge implements Comparable<Edge> {

    private final int weight; //waga sciezki
    private final Vertice start; //wierzcholek startowy (umownie)
    private final Vertice end; //wierzcholek koncowy (umownie)
    private double pheromone; //feromon na sciezce

    public Edge(Vertice start, Vertice end) {
        this.start = start;
        this.end = end;
        this.pheromone = ConstStrings.ZERO;
        this.weight = ConstStrings.ONE;
    }

    public int getWeight() {
        return weight;
    }

    public double getPheromone() {
        return pheromone;
    }

    public void setPheromone(double pheromone) {
        this.pheromone = pheromone;
    }

    public Vertice getStart() {
        return start;
    }

    public Vertice getEnd() {
        return end;
    }

    @Override
    public String toString() {
        return start + ConstStrings.COMMA_NOSPACE + end + ConstStrings.SEMICOLON_NOSPACE + pheromone;
    }

    @Override
    public int compareTo(Edge t) {
        Double thisPheromone = getPheromone();
        Double otherPheromone = t.getPheromone();
        return thisPheromone.compareTo(otherPheromone);
    }

}
