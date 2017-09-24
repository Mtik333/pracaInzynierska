/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package data.graph;

import data.ConstStrings;

/**
 *
 * @author Mateusz
 */
public class Edge implements Comparable<Edge> {

    private double pheromone; //feromon na sciezce
    private int weight; //waga sciezki
    private Vertice start; //wierzcholek startowy (umownie)
    private Vertice end; //wierzcholek koncowy (umownie)

    public Edge() {
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
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

    public void setStart(Vertice start) {
        this.start = start;
    }

    public Vertice getEnd() {
        return end;
    }

    public void setEnd(Vertice end) {
        this.end = end;
    }

    public Edge(Vertice start, Vertice end) {
        this.start = start;
        this.end = end;
        this.pheromone = ConstStrings.ZERO;
        this.weight = ConstStrings.ONE;
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
