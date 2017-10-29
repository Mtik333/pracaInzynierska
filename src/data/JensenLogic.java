/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package data;

import data.graph.Edge;
import data.graph.Vertice;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Mateusz
 */
public class JensenLogic extends Logic {

    @Override
    public void generateGraph() {
        countDecisionClasses();
        List<Vertice> vertices = new ArrayList<>();
        List<Edge> edges = new ArrayList<>();
        for (int i = 0; i < DataAccessor.getAllAttributes().size(); i++) {
            vertices.add(new Vertice(DataAccessor.getAllAttributes().get(i).getName(), i));
        }
        vertices.remove(DataAccessor.getDecisionMaker()); //usuwamy wierzcholek decyzyjny
        generateEdges(vertices, edges);
    }

}
