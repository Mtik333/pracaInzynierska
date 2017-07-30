/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package data.graph;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Mateusz
 */
public class Graph {
    
    private List<Vertice> vertices;
    private List<Edge> edges;

    public Graph(List<Vertice> vertices, List<Edge> edges) {
        this.vertices = new ArrayList<Vertice>();
        this.edges = new ArrayList<Edge>();
        this.vertices = vertices;
        this.edges = edges;
    }

    public List<Vertice> getVertices() {
        return vertices;
    }

    public void setVertices(List<Vertice> vertices) {
        this.vertices = vertices;
    }

    public List<Edge> getEdges() {
        return edges;
    }

    public void setEdges(List<Edge> edges) {
        this.edges = edges;
    }
    
    @Override
    public String toString(){
        StringBuilder s = new StringBuilder();
        for (Vertice x : vertices){
            s.append(x.toString()).append("\n");
        }
        for (Edge y : edges){
            s.append(y.toString()).append("\n");
        }
        return s.toString();
    }
}
