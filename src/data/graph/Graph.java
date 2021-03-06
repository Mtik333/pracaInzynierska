/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package data.graph;

import data.ConstStrings;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Mateusz
 */
public class Graph {

    private List<Vertice> vertices; //lista wierzcholkow
    private List<Edge> edges; //lista krawedzi

    public Graph(List<Vertice> vertices, List<Edge> edges) {
        this.vertices = new ArrayList<>();
        this.edges = new ArrayList<>();
        this.vertices = vertices;
        this.edges = edges;
    }

    public List<Vertice> getVertices() {
        return vertices;
    }

    public List<Edge> getEdges() {
        return edges;
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        vertices.forEach((x) -> s.append(x.toString()).append(ConstStrings.NEW_LINE));
        edges.forEach((y) -> s.append(y.toString()).append(ConstStrings.NEW_LINE));
        return s.toString();
    }
}
